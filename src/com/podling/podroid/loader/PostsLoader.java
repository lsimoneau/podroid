package com.podling.podroid.loader;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.Post;

import android.content.Context;

public class PostsLoader extends The86AsyncTaskLoader<LoaderResult<List<Post>>> {
	private List<Post> mPosts;
	private final String groupSlug;
	private final String conversationId;

	public PostsLoader(Context context, String groupSlug, String conversationId) {
		super(context);
		this.groupSlug = groupSlug;
		this.conversationId = conversationId;
	}

	@Override
	public LoaderResult<List<Post>> loadInBackground() {
		try {
			mPosts = getThe86().getConversationPosts(groupSlug, conversationId);
			return new LoaderResult<List<Post>>(mPosts);
		} catch (The86Exception e) {
			return new LoaderResult<List<Post>>(e);
		}
	}

	@Override
	protected void onStartLoading() {
		if (mPosts != null) {
			deliverResult(new LoaderResult<List<Post>>(mPosts));
		} else {
			forceLoad();
		}
	}

}