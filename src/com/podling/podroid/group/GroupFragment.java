package com.podling.podroid.group;

import android.app.ListFragment;
import android.os.Bundle;

public abstract class GroupFragment extends ListFragment {
	protected String groupSlug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();

		groupSlug = extras.getString("groupSlug");
	}

}
