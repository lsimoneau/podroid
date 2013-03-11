package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Group;

import android.content.Context;

public class GroupsLoader extends
		The86AsyncTaskLoader<LoaderResult<List<Group>>> {
	private List<Group> mGroups;

	public GroupsLoader(Context context) {
		super(context);
	}

	@Override
	public LoaderResult<List<Group>> loadInBackground() {
		try {
			mGroups = getThe86().getGroups();
			return new LoaderResult<List<Group>>(mGroups);
		} catch (The86Exception e) {
			return new LoaderResult<List<Group>>(e);
		}
	}

	@Override
	protected void onStartLoading() {
		if (mGroups != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(new LoaderResult<List<Group>>(mGroups));
		} else {
			forceLoad();
		}
	}

}