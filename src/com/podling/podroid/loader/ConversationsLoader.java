package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Conversation;

import android.content.Context;

public class ConversationsLoader extends
		The86AsyncTaskLoader<List<Conversation>> {
	private List<Conversation> mConversations;
	private String groupSlug;

	public ConversationsLoader(Context context) {
		this(context, null);
	}

	public ConversationsLoader(Context context, String groupSlug) {
		super(context);
		this.groupSlug = groupSlug;
	}

	@Override
	public List<Conversation> loadInBackground() {
		try {
			if (groupSlug != null) {
				mConversations = getThe86().getGroupConversations(groupSlug);
			} else {
				mConversations = getThe86().getUserConversations();
			}
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