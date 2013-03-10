package com.podling.podroid.adapter;

import java.util.List;

import org.the86.model.Post;
import org.the86.model.User;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.kevinsawicki.timeago.TimeAgo;
import com.podling.podroid.DownloadImageTask;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.util.HtmlTextView;

public class PostsAdapter extends ArrayAdapter<Post> {
	private final Activity context;
	private static final int LAYOUT = R.layout.post;

	public PostsAdapter(Activity context, List<Post> posts) {
		super(context, LAYOUT, posts);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(LAYOUT, null);

		Post post = getItem(position);
		User user = post.getUser();

		TextView name = (TextView) view.findViewById(R.id.post_user_name);
		name.setText(post.getUser().getName());

		TextView statusTxt = (TextView) view.findViewById(R.id.post_status);
		String status = new TimeAgo().timeAgo(post.getCreatedAt());
		if (post.getInReplyToId() != null) {
			status = String.format("%s in reply to %s", status, post
					.getInReplyTo().getUser().getName());
		}
		statusTxt.setText(status);

		HtmlTextView content = (HtmlTextView) view
				.findViewById(R.id.post_content);
		content.setHtml(post.getContentHtml());
		content.setMovementMethod(HtmlTextView.LocalLinkMovementMethod
				.getInstance());

		TextView likes = (TextView) view.findViewById(R.id.post_likes);
		likes.setText(likeCount(post));

		ImageView avatar_image = (ImageView) view
				.findViewById(R.id.post_user_avatar);
		if (user.getAvatarUrl() != null) {
			new DownloadImageTask(
					(PodroidApplication) context.getApplication(), avatar_image)
					.execute(user.getAvatarUrl());
		}

		return view;
	}

	// TODO extract
	private String likeCount(Post post) {
		int count = post.getLikes().size();
		switch (count) {
		case 0:
			return "";
		case 1:
			return "1 like";
		default:
			return String.format("%d likes", count);
		}
	}
}