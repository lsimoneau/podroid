package com.podling.podroid.adapter;

import java.util.List;

import org.the86.model.Group;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.podling.podroid.R;

public class GroupAdapter extends ArrayAdapter<Group> {
	private static final int LAYOUT = R.layout.group;
	private final Activity context;

	public GroupAdapter(Activity context, List<Group> groups) {
		super(context, LAYOUT, groups);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupViewHolder holder;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(LAYOUT, null);
			holder = new GroupViewHolder();
			holder.groupName = (TextView) convertView
					.findViewById(R.id.group_name);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}

		holder.groupName.setText(getItem(position).getName());

		return convertView;
	}

	static class GroupViewHolder {
		TextView groupName;
	}
}
