package com.podling.podroid.posts;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Conversation;
import org.the86.model.Like;
import org.the86.model.Post;

import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.LinearLayout;

import com.podling.podroid.CreatePostDialogFragment;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.PostsAdapter;

public class PostsActivity extends ListActivity {
	protected LinearLayout progress;
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

		setContentView(R.layout.posts);

		progress = (LinearLayout) findViewById(R.id.posts_loading_progress);

		fetchPosts();

		// getListView().setItemsCanFocus(true);
		registerForContextMenu(getListView());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.post_context_menu, menu);

		Post post = (Post) getListAdapter().getItem(
				((AdapterContextMenuInfo) menuInfo).position);
		int likeText = (post.likeForUser(the86.getUserId()) == null) ? R.string.like_post
				: R.string.unlike_post;

		menu.findItem(R.id.post_context_menu_like).setTitle(likeText);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Post post = (Post) getListAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case R.id.post_context_menu_like:
			new TogglePostLikeTask().execute(post);
			return true;
		case R.id.post_context_menu_reply:
			CreatePostDialogFragment dialog = CreatePostDialogFragment
					.newInstance(groupSlug, conversationId, post.getId());
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "reply_to_post");
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.posts_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_post) {
			CreatePostDialogFragment dialog = CreatePostDialogFragment
					.newInstance(groupSlug, conversationId);
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "create_post");
		} else if (item.getItemId() == R.id.refresh_posts_menu_item) {
			fetchPosts();
		}
		return true;
	}

	private void fetchPosts() {
		getListView().setVisibility(View.GONE);
		new RetrievePostsTask().execute();
	}

	private void populate(List<Post> posts) {
		setListAdapter(new PostsAdapter(this, posts));
		getListView().setVisibility(View.VISIBLE);
	}

	class TogglePostLikeTask extends AsyncTask<Post, Void, Like> {

		@Override
		protected Like doInBackground(Post... params) {
			Post post = params[0];
			try {
				Like userLike = post.likeForUser(the86.getUserId());
				if (userLike != null) {
					the86.unlikePost(groupSlug, conversationId, post.getId(),
							userLike.getId());
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
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
			progress.setVisibility(View.GONE);
		}

	}

}
