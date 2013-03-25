package com.podling.podroid.posts;

import org.the86.model.Conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.podling.podroid.R;

public class PostsActivity extends SherlockFragmentActivity {
	public static Intent newInstance(Context context, Conversation conversation) {
		return newInstance(context, conversation.getGroup().getSlug(),
				conversation.getId());
	}

	public static Intent newInstance(Context context, String groupSlug,
			String conversationId) {
		Intent intent = new Intent(context, PostsActivity.class);
		intent.putExtra("groupSlug", groupSlug);
		intent.putExtra("conversationId", conversationId);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.posts);
	}

}
