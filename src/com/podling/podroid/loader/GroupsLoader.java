package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Group;

import android.content.Context;

public class GroupsLoader extends The86AsyncTaskLoader<List<Group>> {
	private List<Group> mGroups;

	public GroupsLoader(Context context) {
		super(context);
	}

	@Override
	public List<Group> loadInBackground() {
		try {
			mGroups = getThe86().getGroups();
			return mGroups;
		} catch (The86Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onStartLoading() {
		if (mGroups != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mGroups);
		} else {
			forceLoad();
		}
	}

}
