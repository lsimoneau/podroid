package com.podling.podroid.group;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupMembershipAdapter;

public class GroupMembersFragment extends GroupFragment {
	private LinearLayout progress;
	private boolean fetched = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.group_members, container, false);
		progress = (LinearLayout) v
				.findViewById(R.id.group_members_loading_progress);
		return v;
	}

	// TODO api doesn't support fetching individual users
//	@Override
//	public void onListItemClick(ListView l, View v, int position, long id) {
//		super.onListItemClick(l, v, position, id);
//		 User user = (User) getListAdapter().getItem(position);
//		 startActivity(UserActivity.newInstance(getActivity(), user.getId()));
//	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!fetched) {
			fetchMembers();
		}
	}

	private void fetchMembers() {
		getListView().setVisibility(View.GONE);
		new RetrieveMembersTask().execute();
	}

	private void populate(List<User> memberships) {
		setListAdapter(new GroupMembershipAdapter(getActivity(), memberships));
		getListView().setVisibility(View.VISIBLE);
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
			progress.setVisibility(View.GONE);
		}
	}
}
