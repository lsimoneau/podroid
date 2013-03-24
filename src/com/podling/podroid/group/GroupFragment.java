package com.podling.podroid.group;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;

public abstract class GroupFragment extends SherlockListFragment {
	protected String groupSlug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();

		groupSlug = extras.getString("groupSlug");
	}

}
