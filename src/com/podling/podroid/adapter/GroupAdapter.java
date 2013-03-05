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
	private final Activity context;

	public GroupAdapter(Activity context, List<Group> groups) {
		super(context, R.layout.group, groups);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.group, null);

		TextView groupName = (TextView) view.findViewById(R.id.group_name);
		groupName.setText(getItem(position).getName());

		return view;
	}
}
