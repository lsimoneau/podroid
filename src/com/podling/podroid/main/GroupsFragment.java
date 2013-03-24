package com.podling.podroid.main;

import java.util.List;

import org.the86.model.Group;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupAdapter;
import com.podling.podroid.group.CreateGroupDialogFragment;
import com.podling.podroid.group.GroupActivity;
import com.podling.podroid.loader.GroupsLoader;
import com.podling.podroid.loader.LoaderResult;

public class GroupsFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<LoaderResult<List<Group>>> {
	private LinearLayout progress;
	private GroupAdapter mAdapter;
	private boolean allowRefresh = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.groups, container, false);
		progress = (LinearLayout) v.findViewById(R.id.groups_loading_progress);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setHasOptionsMenu(true);

		mAdapter = new GroupAdapter(getActivity());
		setListAdapter(mAdapter);

		setRefreshable(false);
		progress.setVisibility(View.VISIBLE);
		getLoaderManager().initLoader(PodroidApplication.GROUPS_LOADER_ID,
				null, this);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.group_list_menu, menu);
		MenuItem refresh = menu.findItem(R.id.refresh_groups_menu_item);
		refresh.setEnabled(allowRefresh);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_group) {
			CreateGroupDialogFragment dialog = CreateGroupDialogFragment
					.newInstance();
			FragmentManager fragmentManager = getFragmentManager();
			dialog.setTargetFragment(this, 0);
			dialog.show(fragmentManager, "create_group");
		} else if (item.getItemId() == R.id.refresh_groups_menu_item) {
			refreshData();
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Group group = (Group) getListAdapter().getItem(position);
		startActivity(GroupActivity.newInstance(getActivity(), group));
	}

	@Override
	public Loader<LoaderResult<List<Group>>> onCreateLoader(int id, Bundle args) {
		return new GroupsLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<LoaderResult<List<Group>>> loader,
			LoaderResult<List<Group>> result) {
		Exception e = result.getException();
		if (e != null) {
			Toast.makeText(getActivity(), R.string.error_groups_load,
					Toast.LENGTH_SHORT).show();
		}
		mAdapter.setData(result.getData());
		progress.setVisibility(View.GONE);
		setRefreshable(true);
	}

	@Override
	public void onLoaderReset(Loader<LoaderResult<List<Group>>> loader) {
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
		getLoaderManager().restartLoader(PodroidApplication.GROUPS_LOADER_ID,
				null, this);
	}
}