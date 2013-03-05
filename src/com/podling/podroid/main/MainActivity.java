package com.podling.podroid.main;


import com.podling.podroid.TabListener;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getActionBar();
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		Tab tab = actionBar
				.newTab()
				.setText("latest")
				.setTabListener(
						new TabListener<LatestConversationsFragment>(this,
								"latest", LatestConversationsFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText("groups")
				.setTabListener(
						new TabListener<GroupsFragment>(this, "groups",
								GroupsFragment.class));
		actionBar.addTab(tab);
	}
}
