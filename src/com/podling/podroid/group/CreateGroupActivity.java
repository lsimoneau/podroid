package com.podling.podroid.group;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.Group;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.R.id;
import com.podling.podroid.R.layout;

public class CreateGroupActivity extends Activity {
	private EditText groupNameEdit;
	private The86 the86;

	public static Intent newInstance(Context context) {
		Intent intent = new Intent(context, CreateGroupActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_group);

		getActionBar().setTitle("Create a pod");

		the86 = ((PodroidApplication) getApplicationContext()).getThe86();

		groupNameEdit = ((EditText) findViewById(R.id.create_group_name));

		Button button = (Button) findViewById(R.id.btn_create_group);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new CreateGroupTask(v.getContext()).execute();
			}
		});
	}

	private void onGroupCreated(Group group) {
		if (group != null) {
			startActivity(GroupActivity.newInstance(getBaseContext(), group));
			finish();
		} else {
			Toast.makeText(this, "Couldn't create a group", Toast.LENGTH_SHORT)
					.show();
		}
	}

	class CreateGroupTask extends AsyncTask<Void, Void, Group> {
		protected ProgressDialog dialog;

		public CreateGroupTask(Context context) {
			dialog = new ProgressDialog(context);
			dialog.setMessage("creating group");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected Group doInBackground(Void... params) {
			try {
				String groupName = groupNameEdit.getText().toString();
				return the86.createGroup(groupName);
			} catch (The86Exception e) {
				return null;
			}
		}

		protected void onPostExecute(Group group) {
			dialog.dismiss();
			onGroupCreated(group);
		}

	}
}
