package com.podling.podroid.group;

import java.util.List;

import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.podling.podroid.R;
import com.podling.podroid.adapter.GroupMembershipAdapter;

public class GroupMembersFragment extends GroupFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new RetrieveMembersTask(getActivity()).execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.group_members, container, false);
		return v;
	}

	private void populate(List<User> memberships) {
		setListAdapter(new GroupMembershipAdapter(getActivity(), memberships));
	}

	class RetrieveMembersTask extends
			AsyncTask<Void, Void, List<User>> {
		protected ProgressDialog dialog;

		public RetrieveMembersTask(Context context) {
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage("loading members");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
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
			dialog.dismiss();
		}
	}
}
