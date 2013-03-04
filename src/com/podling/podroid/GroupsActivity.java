package com.podling.podroid;

import java.util.List;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Group;

import com.podling.podroid.adapter.GroupsAdapter;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class GroupsActivity extends ListActivity {
	private The86 the86;
	
	public static Intent newInstance(Context context) {
		Intent intent = new Intent(context, GroupsActivity.class);
		return intent;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new RetrieveGroupsTask(this).execute();
		the86 = ((PodroidApplication) getApplicationContext()).getThe86();
		setContentView(R.layout.groups);
	}

	protected void onListItemClick(ListView l, View v, int position,
			long groupId) {
		super.onListItemClick(l, v, position, groupId);
		Cursor c = (Cursor) getListAdapter().getItem(position);
		String groupSlug = c.getString(2);
		startActivity(ConversationsActivity.newInstance(this, groupSlug));
	}

	private void populate(List<Group> groups) {
		String[] cols = { "_id", "name", "slug" };
		MatrixCursor cursor = new MatrixCursor(cols);

		for (Group group : groups) {
			cursor.addRow(new String[] { group.getId(), group.getName(),
					group.getSlug() });
		}

		setListAdapter(new GroupsAdapter(this, cursor));
	}

	class RetrieveGroupsTask extends AsyncTask<Void, Void, List<Group>> {
		protected ProgressDialog dialog;

		public RetrieveGroupsTask(Context context) {
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage("loading groups");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
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
			dialog.dismiss();
		}

	}
}
