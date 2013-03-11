package com.podling.podroid.adapter;

import java.util.List;

import org.the86.model.User;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.podling.podroid.DownloadImageTask;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;

public class GroupMembershipAdapter extends ArrayAdapter<User> {
	private static final int LAYOUT = R.layout.group_member;
	private final Activity context;

	public GroupMembershipAdapter(Activity context) {
		super(context, LAYOUT);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupMembershipViewHolder holder;

		User user = getItem(position);

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(LAYOUT, null);

			holder = new GroupMembershipViewHolder();

			holder.avatar = (ImageView) convertView
					.findViewById(R.id.group_member_avatar);
			holder.memberName = (TextView) convertView
					.findViewById(R.id.group_member_name);
			holder.memberProfile = (TextView) convertView
					.findViewById(R.id.group_member_profile);

			convertView.setTag(holder);
		} else {
			holder = (GroupMembershipViewHolder) convertView.getTag();
		}

		holder.memberName.setText(user.getName());
		holder.memberProfile.setText(user.getProfile());
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

	public void setData(List<User> data) {
		clear();
		if (data != null) {
			addAll(data);
		}
	}

	static class GroupMembershipViewHolder extends AvatarViewHolder {
		TextView memberName;
		TextView memberProfile;
	}
}
