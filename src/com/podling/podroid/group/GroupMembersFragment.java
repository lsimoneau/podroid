package com.podling.podroid.group;

import java.util.ArrayList;
import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupMembershipAdapter;

public class GroupMembersFragment extends GroupFragment {
	private LinearLayout progress;
	private boolean fetched = false;
	private boolean allowRefresh = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setListAdapter(new GroupMembershipAdapter(getActivity(),
				new ArrayList<User>()));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.group_members, container, false);
		progress = (LinearLayout) v
				.findViewById(R.id.group_members_loading_progress);
		return v;
	}

	// TODO api doesn't support fetching individual users
	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// super.onListItemClick(l, v, position, id);
	// User user = (User) getListAdapter().getItem(position);
	// startActivity(UserActivity.newInstance(getActivity(), user.getId()));
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!fetched) {
			fetchMembers();
		}
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
			fetchMembers();
		}
		return true;
	}

	private void fetchMembers() {
		setRefreshable(false);
		((GroupMembershipAdapter) getListAdapter()).clear();
		new RetrieveMembersTask().execute();
	}

	private void populate(List<User> memberships) {
		((GroupMembershipAdapter) getListAdapter()).addAll(memberships);
		setRefreshable(true);
	}

	private void setRefreshable(boolean allowRefresh) {
		this.allowRefresh = allowRefresh;
		getActivity().invalidateOptionsMenu();
	}

	class RetrieveMembersTask extends AsyncTask<Void, Void, List<User>> {

		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		protected List<User> doInBackground(Void... params) {
			try {
				return the86.getGroupMemberships(groupSlug);
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<User> users) {
			populate(users);
			fetched = true;
			progress.setVisibility(View.GONE);
		}
	}
}
