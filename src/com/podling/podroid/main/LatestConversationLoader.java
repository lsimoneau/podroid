package com.podling.podroid.main;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.app.Activity;
import android.content.AsyncTaskLoader;
import android.content.Context;

import com.podling.podroid.util.The86Util;

public class LatestConversationLoader extends
		AsyncTaskLoader<List<Conversation>> {
	private The86 the86;
	private List<Conversation> mConversations;

	public LatestConversationLoader(Context context) {
		super(context);
		the86 = The86Util.get((Activity) context);
	}

	@Override
	public List<Conversation> loadInBackground() {
		try {
			mConversations = the86.getUserConversations();
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