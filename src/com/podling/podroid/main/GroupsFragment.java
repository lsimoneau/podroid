package com.podling.podroid.main;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Group;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.podling.podroid.CreateGroupActivity;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupAdapter;
import com.podling.podroid.group.GroupActivity;

public class GroupsFragment extends ListFragment {
	private The86 the86;

	protected LinearLayout progress;
	private boolean fetched;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fetched = false;
		setHasOptionsMenu(true);
		the86 = ((PodroidApplication) getActivity().getApplicationContext())
				.getThe86();
	}

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
		if (!fetched) {
			new RetrieveGroupsTask().execute();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.group_list_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_create_group) {
			startActivity(CreateGroupActivity.newInstance(getActivity()));
		}
		return true;
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Group group = (Group) getListAdapter().getItem(position);
		startActivity(GroupActivity.newInstance(getActivity(), group));
	}

	private void populate(List<Group> groups) {
		setListAdapter(new GroupAdapter(getActivity(), groups));
	}

	class RetrieveGroupsTask extends AsyncTask<Void, Void, List<Group>> {

		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		protected List<Group> doInBackground(Void... params) {
			try {
				return the86.getGroups();
			} catch (The86Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(List<Group> groups) {
			populate(groups);
			progress.setVisibility(View.GONE);
			fetched = true;
		}
	}
}