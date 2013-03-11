package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.content.Context;

public class LatestConversationLoader extends
		The86AsyncTaskLoader<List<Conversation>> {
	private List<Conversation> mConversations;

	public LatestConversationLoader(Context context) {
		super(context);
	}

	@Override
	public List<Conversation> loadInBackground() {
		try {
			mConversations = getThe86().getUserConversations();
			return mConversations;
		} catch (The86Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onStartLoading() {
		if (mConversations != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mConversations);
		} else {
			forceLoad();
		}
	}

}