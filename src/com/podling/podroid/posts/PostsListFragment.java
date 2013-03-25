package com.podling.podroid.posts;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Like;
import org.the86.model.Post;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.widget.IcsAdapterView.AdapterContextMenuInfo;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.PostsAdapter;
import com.podling.podroid.loader.LoaderResult;
import com.podling.podroid.loader.PostsLoader;
import com.podling.podroid.util.The86Util;



public class PostsListFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<LoaderResult<List<Post>>> {
	public static String BROADCAST_ACTION = "com.podling.podroid.REFRESH_POSTS";

	private The86 the86;
	private LinearLayout progress;
	private PostsAdapter mAdapter;
	private boolean allowRefresh = false;
	private String groupSlug;
	private String conversationId;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			refreshData();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		the86 = The86Util.get(this.getActivity());

		Bundle extras = getActivity().getIntent().getExtras();
		groupSlug = extras.getString("groupSlug");
		conversationId = extras.getString("conversationId");

		progress = (LinearLayout) getView().findViewById(
				R.id.posts_loading_progress);

		mAdapter = new PostsAdapter(this.getActivity());
		setListAdapter(mAdapter);
		
		setHasOptionsMenu(true);

		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(
				PodroidApplication.GROUP_MEMBERS_LOADER_ID, loaderArguments(),
				this);

		registerForContextMenu(getListView());
	}

	@Override
	public void onResume() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BROADCAST_ACTION);
		getActivity().registerReceiver(receiver, filter);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		getActivity().unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		android.view.MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.post_context_menu, menu);

		Post post = (Post) getListAdapter().getItem(
				((AdapterContextMenuInfo) menuInfo).position);
		int likeText = (post.likeForUser(the86.getUserId()) == null) ? R.string.like_post
				: R.string.unlike_post;

		menu.findItem(R.id.post_context_menu_like).setTitle(likeText);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		Post post = (Post) getListAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case R.id.post_context_menu_like:
			new TogglePostLikeTask(getActivity()).execute(post);
			return true;
		case R.id.post_context_menu_reply:
			CreatePostDialogFragment dialog = CreatePostDialogFragment
					.newInstance(groupSlug, conversationId, post.getId());
			FragmentManager fragmentManager = getActivity()
					.getSupportFragmentManager();
			dialog.show(fragmentManager, "reply_to_post");
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.posts_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_posts_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_post) {
			CreatePostDialogFragment dialog = CreatePostDialogFragment
					.newInstance(groupSlug, conversationId);
			FragmentManager fragmentManager = getFragmentManager();
			dialog.show(fragmentManager, "create_post");
		} else if (item.getItemId() == R.id.refresh_posts_menu_item) {
			refreshData();
		}
		return true;
	}

	@Override
	public Loader<LoaderResult<List<Post>>> onCreateLoader(int id, Bundle args) {
		return new PostsLoader(getActivity(), args.getString("groupSlug"),
				args.getString("conversationId"));
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<List<Post>>> loader,
			LoaderResult<List<Post>> result) {
		Exception e = result.getException();
		if (e != null) {
			Toast.makeText(getActivity(), R.string.error_posts_load,
					Toast.LENGTH_SHORT).show();
		}
		mAdapter.setData(result.getData());
		progress.setVisibility(View.GONE);
		setRefreshable(true);
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<List<Post>>> loader) {
		mAdapter.setData(null);
	}

	private void setRefreshable(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
		getActivity().invalidateOptionsMenu();
	}

	public void refreshData() {
		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		mAdapter.setData(null);
		getLoaderManager().restartLoader(
				PodroidApplication.GROUP_MEMBERS_LOADER_ID, loaderArguments(),
				this);
	}

	private Bundle loaderArguments() {
		Bundle args = new Bundle();
		args.putString("groupSlug", groupSlug);
		args.putString("conversationId", conversationId);
		return args;
	}

	class TogglePostLikeTask extends AsyncTask<Post, Void, Like> {
		protected ProgressDialog dialog;

		public TogglePostLikeTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("liking");
			dialog.setCancelable(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

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

		@Override
		protected void onPostExecute(Like like) {
			refreshData();
			dialog.hide();
		}
	}

}
