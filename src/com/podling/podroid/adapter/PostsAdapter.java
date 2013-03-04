package com.podling.podroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.podling.podroid.R;

public class PostsAdapter extends CursorAdapter {
	// private static final int CURSOR_INDEX_GROUP_NAME = 1;

	public PostsAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String name = cursor.getString(1);
		TextView tPoster = (TextView) view.findViewById(R.id.post_poster);
		tPoster.setText(name);

		String post = cursor.getString(2);
		TextView tPost = (TextView) view.findViewById(R.id.post_content);
		tPost.setText(post);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(R.layout.post, null);
	}

}