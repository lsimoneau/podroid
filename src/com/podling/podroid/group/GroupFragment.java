package com.podling.podroid.group;

import org.the86.The86;

import com.podling.podroid.PodroidApplication;

import android.app.ListFragment;
import android.os.Bundle;

public abstract class GroupFragment extends ListFragment {
	protected The86 the86;
	protected String groupSlug;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getArguments();

		groupSlug = extras.getString("groupSlug");

		the86 = ((PodroidApplication) getActivity().getApplicationContext())
				.getThe86();
	}

}
