package com.podling.podroid.group;

import java.util.List;

import org.the86.model.Conversation;

import android.app.FragmentManager;
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

import com.podling.podroid.CreateConversationDialogFragment;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.ConversationAdapter;
import com.podling.podroid.loader.ConversationsLoader;
import com.podling.podroid.posts.PostsActivity;

public class GroupConversationsFragment extends GroupFragment implements
		LoaderManager.LoaderCallbacks<List<Conversation>> {
	private LinearLayout progress;
	private ConversationAdapter mAdapter;
	private boolean allowRefresh = false;

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Conversation conversation = (Conversation) getListAdapter().getItem(
				position);
		startActivity(PostsActivity.newInstance(getActivity(), groupSlug,
				conversation.getId()));
	}

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
		inflater.inflate(R.menu.group_conversations_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_conversations_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_conversation) {
			CreateConversationDialogFragment dialog = CreateConversationDialogFragment
					.newInstance(groupSlug);
			FragmentManager fragmentManager = getFragmentManager();
			dialog.setTargetFragment(this, 0);
			dialog.show(fragmentManager, "create_conversation");
		} else if (item.getItemId() == R.id.refresh_conversations_menu_item) {
			refreshData();
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
				PodroidApplication.GROUP_CONVERSATIONS_LOADER_ID,
				loaderArguments(), this);
	}

	@Override
	public Loader<List<Conversation>> onCreateLoader(int id, Bundle args) {
		return new ConversationsLoader(getActivity(),
				args.getString("groupSlug"));
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

	public void refreshData() {
		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		mAdapter.setData(null);
		getLoaderManager().restartLoader(
				PodroidApplication.GROUP_CONVERSATIONS_LOADER_ID,
				loaderArguments(), this);
	}

	private Bundle loaderArguments() {
		Bundle args = new Bundle();
		args.putString("groupSlug", groupSlug);
		return args;
	}
}
