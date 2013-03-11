package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Post;

import android.content.Context;

public class PostsLoader extends The86AsyncTaskLoader<List<Post>> {
	private List<Post> mPosts;
	private String groupSlug;
	private String conversationId;

	public PostsLoader(Context context, String groupSlug, String conversationId) {
		super(context);
		this.groupSlug = groupSlug;
		this.conversationId = conversationId;
	}

	@Override
	public List<Post> loadInBackground() {
		try {
			mPosts = getThe86().getConversationPosts(groupSlug, conversationId);

			return mPosts;
		} catch (The86Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onStartLoading() {
		if (mPosts != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(mPosts);
		} else {
			forceLoad();
		}
	}

}