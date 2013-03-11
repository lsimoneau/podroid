package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.content.Context;

public class GroupMembersLoader extends
		The86AsyncTaskLoader<LoaderResult<List<User>>> {
	private final String groupSlug;
	private List<User> mUsers;

	public GroupMembersLoader(Context context, String groupSlug) {
		super(context);
		this.groupSlug = groupSlug;
	}

	@Override
	public LoaderResult<List<User>> loadInBackground() {
		try {
			mUsers = getThe86().getGroupMemberships(groupSlug);
			return new LoaderResult<List<User>>(mUsers);
		} catch (The86Exception e) {
			return new LoaderResult<List<User>>(e);
		}
	}

	@Override
	protected void onStartLoading() {
		if (mUsers != null) {
			deliverResult(new LoaderResult<List<User>>(mUsers));
		} else {
			forceLoad();
		}
	}

}