package com.podling.podroid.group;

import java.util.List;

import org.the86.model.User;

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
import android.widget.Toast;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupMembershipAdapter;
import com.podling.podroid.loader.GroupMembersLoader;
import com.podling.podroid.loader.LoaderResult;

public class GroupMembersFragment extends GroupFragment implements
		LoaderManager.LoaderCallbacks<LoaderResult<List<User>>> {
	private LinearLayout progress;
	private GroupMembershipAdapter mAdapter;
	private boolean allowRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.group_members, container, false);
		progress = (LinearLayout) v
				.findViewById(R.id.group_members_loading_progress);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		mAdapter = new GroupMembershipAdapter(getActivity());
		setListAdapter(mAdapter);

		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(
				PodroidApplication.GROUP_MEMBERS_LOADER_ID, loaderArguments(),
				this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.group_members_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_group_members_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh_group_members_menu_item) {
			refreshData();
		}
		return true;
	}

	@Override
	public Loader<LoaderResult<List<User>>> onCreateLoader(int id, Bundle args) {
		return new GroupMembersLoader(getActivity(),
				args.getString("groupSlug"));
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<List<User>>> loader,
			LoaderResult<List<User>> result) {
		Exception e = result.getException();
		if (e != null) {
			Toast.makeText(getActivity(), R.string.error_group_members_load,
					Toast.LENGTH_SHORT).show();
		}
		mAdapter.setData(result.getData());
		progress.setVisibility(View.GONE);
		setRefreshable(true);
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<List<User>>> loader) {
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
		return args;
	}

}
