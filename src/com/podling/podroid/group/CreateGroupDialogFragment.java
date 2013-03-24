package com.podling.podroid.group;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.podling.podroid.R;
import com.podling.podroid.main.GroupsFragment;
import com.podling.podroid.util.The86Util;

public class CreateGroupDialogFragment extends DialogFragment {
	private The86 the86;
	private GroupsFragment groupsFragment;

	public static CreateGroupDialogFragment newInstance() {
		CreateGroupDialogFragment dialog = new CreateGroupDialogFragment();
		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		the86 = The86Util.get(getActivity());
		groupsFragment = (GroupsFragment) getTargetFragment();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.create_group_dialog, null);
		final EditText groupName = (EditText) view
				.findViewById(R.id.create_group_name);

		builder.setView(view)
				.setPositiveButton("create",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								new CreateGroupTask(getActivity())
										.execute(groupName.getText().toString());
							}
						})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// do nothing
							}
						});

		return builder.create();
	}

	private void onGroupCreated(Group group) {
		if (group != null) {
			groupsFragment.refreshData();
		} else {
			Resources res = getActivity().getResources();
			Toast.makeText(getActivity(),
					res.getString(R.string.create_group_failed),
					Toast.LENGTH_SHORT).show();
		}
	}

	class CreateGroupTask extends AsyncTask<String, Void, Group> {
		protected ProgressDialog dialog;

		public CreateGroupTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage(context.getResources().getString(
					R.string.creating_group));
			dialog.setCancelable(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		@Override
		protected Group doInBackground(String... params) {
			String groupName = params[0];
			try {
				return the86.createGroup(groupName);
			} catch (The86Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(Group group) {
			dialog.dismiss();
			onGroupCreated(group);
		}

	}
}
