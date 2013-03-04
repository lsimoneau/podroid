package com.podling.podroid.adapter;

import com.podling.podroid.R;
import com.podling.podroid.R.id;
import com.podling.podroid.R.layout;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GroupsAdapter extends CursorAdapter {
	private static final int CURSOR_INDEX_GROUP_NAME = 1;

	public GroupsAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String name = cursor.getString(CURSOR_INDEX_GROUP_NAME);
		TextView tPoster = (TextView) view.findViewById(R.id.group_name);
		tPoster.setText(name);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.list_item_group, null);
	}

}
