package com.podling.podroid.main;

import java.util.List;

import org.the86.model.Conversation;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.loader.LatestConversationLoader;
import com.podling.podroid.posts.PostsActivity;

public class LatestConversationsFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<Conversation>> {
	private LinearLayout progress;
	private ConversationAdapter mAdapter;
	private boolean allowRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.conversations, container, false);
		progress = (LinearLayout) v
				.findViewById(R.id.conversation_loading_progress);
		return v;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.conversations_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_conversations_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_conversations_menu_item) {
			progress.setVisibility(View.VISIBLE);
			mAdapter.setData(null);
			getLoaderManager().restartLoader(
					PodroidApplication.LATEST_CONVERSATIONS_LOADER_ID, null,
					this);
		}
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		mAdapter = new ConversationAdapter(getActivity());
		setListAdapter(mAdapter);

		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(
				PodroidApplication.LATEST_CONVERSATIONS_LOADER_ID, null, this);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Conversation conversation = (Conversation) getListAdapter().getItem(
				position);
		startActivity(PostsActivity.newInstance(getActivity(), conversation));
	}

	@Override
	public Loader<List<Conversation>> onCreateLoader(int id, Bundle args) {
		return new LatestConversationLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Conversation>> loader,
			List<Conversation> conversations) {
		mAdapter.setData(conversations);
		progress.setVisibility(View.GONE);
		setRefreshable(true);
	}

	@Override
	public void onLoaderReset(Loader<List<Conversation>> loader) {
		mAdapter.setData(null);
	}

	private void setRefreshable(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
		getActivity().invalidateOptionsMenu();
	}
}
