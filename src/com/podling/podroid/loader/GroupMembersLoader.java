package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.content.Context;

public class GroupMembersLoader extends The86AsyncTaskLoader<List<User>> {
	private String groupSlug;
	private List<User> mUsers;

	public GroupMembersLoader(Context context, String groupSlug) {
		super(context);
		this.groupSlug = groupSlug;
	}

	@Override
	public List<User> loadInBackground() {
		try {
			mUsers = getThe86().getGroupMemberships(groupSlug);
			return mUsers;
		} catch (The86Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onStartLoading() {
		if (mUsers != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mUsers);
		} else {
			forceLoad();
		}
	}

}