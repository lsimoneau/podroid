package com.podling.podroid.adapter;

import java.util.List;

import org.the86.model.Conversation;
import org.the86.model.Post;
import org.the86.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import com.podling.podroid.util.The86Util;

public class ConversationAdapter extends ArrayAdapter<Conversation> {
	private final Activity context;
	private static final int LAYOUT = R.layout.conversation;

	public ConversationAdapter(Activity context) {
		super(context, LAYOUT);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ConversationViewHolder holder;

		Conversation conversation = getItem(position);
		Post post = conversation.getPosts().get(
				conversation.getPosts().size() - 1);
		User user = post.getUser();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(LAYOUT, null);

			holder = new ConversationViewHolder();

			holder.avatar = (ImageView) convertView
					.findViewById(R.id.conversation_poster_avatar);
			holder.posterName = (TextView) convertView
					.findViewById(R.id.conversation_poster);
			holder.groupName = (TextView) convertView
					.findViewById(R.id.conversation_group_name);
			holder.bumpedAt = (TextView) convertView
					.findViewById(R.id.conversation_bumped_at);
			holder.replies = (TextView) convertView
					.findViewById(R.id.conversation_replies);
			holder.likes = (TextView) convertView
					.findViewById(R.id.conversation_likes);
			holder.content = (TextView) convertView
					.findViewById(R.id.conversation_content);

			convertView.setTag(holder);
		} else {
			holder = (ConversationViewHolder) convertView.getTag();
		}

		holder.posterName.setText(user.getName());

		holder.content.setText(Html.fromHtml(post.getContentHtml()));

		if (conversation.getGroup() != null) {
			holder.groupName.setText(conversation.getGroup().getName());
		} else {
			holder.groupName.setVisibility(View.GONE);
		}

		holder.bumpedAt.setText(The86Util.getRelativeLocalTime(conversation
				.getBumpedAt()));

		int count = conversation.getPosts().size() - 1; // OP not counted
		Resources res = context.getResources();
		holder.replies.setText(res.getQuantityString(R.plurals.reply_count, count, count));

		int likes = post.getLikes().size();
		holder.likes.setText(res.getQuantityString(R.plurals.like_count, likes, likes));

		holder.position = position;

		if (user.getAvatarUrl() != null) {
			new DownloadImageTask(
					(PodroidApplication) context.getApplication(), position,
					holder).execute(user.getAvatarUrl());
		} else {
			holder.avatar.setImageResource(R.drawable.ic_contact_picture);
		}

		return convertView;
	}

	public void setData(List<Conversation> data) {
		clear();
		if (data != null) {
			addAll(data);
		}
	}

	static class ConversationViewHolder extends AvatarViewHolder {
		TextView posterName;
		TextView groupName;
		TextView bumpedAt;
		TextView replies;
		TextView likes;
		TextView content;
	}
}
