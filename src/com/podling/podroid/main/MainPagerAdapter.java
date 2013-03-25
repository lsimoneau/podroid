package com.podling.podroid.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainPagerAdapter extends FragmentPagerAdapter {

	public MainPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment f;
		switch (position) {
		case 0:
			f = new GroupsFragment();
		case 1:
		default:
			f = new LatestConversationsFragment();
		}
		return f;
	}

	@Override
	public int getCount() {
		return 2;
	}

}
