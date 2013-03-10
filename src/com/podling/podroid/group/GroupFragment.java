package com.podling.podroid.group;

import org.the86.The86;

import android.app.ListFragment;
import android.os.Bundle;

import com.podling.podroid.util.The86Util;

public abstract class GroupFragment extends ListFragment {
	protected The86 the86;
	protected String groupSlug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();

		groupSlug = extras.getString("groupSlug");

		the86 = The86Util.get(getActivity());
	}

}
