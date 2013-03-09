package com.podling.podroid;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;
import org.the86.model.Like;
import org.the86.model.Post;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.podling.podroid.adapter.PostsAdapter;

public class PostsActivity extends ListActivity {
	private String groupSlug;
	private String conversationId;
	private The86 the86;

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
		super.onCreate(savedInstanceState);
		the86 = ((PodroidApplication) getApplicationContext()).getThe86();

		Bundle extras = getIntent().getExtras();
		groupSlug = extras.getString("groupSlug");
		conversationId = extras.getString("conversationId");
		new RetrievePostsTask(this).execute();

		setContentView(R.layout.posts);
		
		//getListView().setItemsCanFocus(true);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.post_context_menu_like:
			Post post = (Post) getListAdapter().getItem(info.position);
			new TogglePostLikeTask().execute(post);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void populate(List<Post> posts) {
		setListAdapter(new PostsAdapter(this, posts));
	}

	class TogglePostLikeTask extends AsyncTask<Post, Void, Like> {

		@Override
		protected Like doInBackground(Post... params) {
			Post post = params[0];
			boolean liked = false;
			try {
				if (liked) {
					// the86.unlikePost(groupSlug, conversationId,
					// post.getId());
				} else {
					return the86.likePost(groupSlug, conversationId,
							post.getId());
				}
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	class RetrievePostsTask extends AsyncTask<Void, Void, List<Post>> {
		protected ProgressDialog dialog;

		public RetrievePostsTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("loading posts");
			dialog.setCancelable(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected List<Post> doInBackground(Void... params) {
			try {
				return the86.getConversationPosts(groupSlug, conversationId);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Post> posts) {
			populate(posts);
			dialog.dismiss();
		}

	}

}
