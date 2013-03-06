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

	private final Activity context;

	public GroupMembershipAdapter(Activity context, List<User> memberships) {
		super(context, R.layout.group_member, memberships);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.group_member, null);

		User user = getItem(position);

		TextView name = (TextView) view.findViewById(R.id.group_member_name);
		name.setText(user.getName());

		TextView profile = (TextView) view
				.findViewById(R.id.group_member_profile);
		profile.setText(user.getProfile());

		ImageView avatar_image = (ImageView) view
				.findViewById(R.id.group_member_avatar);
		if (user.getAvatarUrl() != null) {
			new DownloadImageTask((PodroidApplication) context.getApplication(), avatar_image).execute(user.getAvatarUrl());
		}
		return view;
	}

}
