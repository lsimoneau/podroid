package com.podling.podroid.adapter;

import java.util.List;

import org.the86.model.Conversation;
import org.the86.model.Post;
import org.the86.model.User;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.podling.podroid.DownloadImageTask;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
	private final Activity context;

	public ConversationAdapter(Activity context,
			List<Conversation> conversations) {
		super(context, R.layout.conversation, conversations);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.conversation, null);

		Conversation conversation = getItem(position);
		Post post = conversation.getPosts().get(0);
		User user = post.getUser();

		TextView tPoster = (TextView) view
				.findViewById(R.id.conversation_poster);
		tPoster.setText(user.getName());

		TextView tPost = (TextView) view
				.findViewById(R.id.conversation_content);
		tPost.setText(Html.fromHtml(post.getContentHtml()));

		TextView replies = (TextView) view
				.findViewById(R.id.conversation_replies);
		replies.setText(replyCount(conversation));

		TextView likes = (TextView) view.findViewById(R.id.conversation_likes);
		likes.setText(likeCount(post));

		ImageView avatar_image = (ImageView) view
				.findViewById(R.id.conversation_poster_avatar);
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

	private String replyCount(Conversation conversation) {
		int count = conversation.getPosts().size() - 1; // -1 for OP
		switch (count) {
		case 0:
			return "";
		case 1:
			return "1 reply";
		default:
			return String.format("%d replies", count);
		}
	}
}
