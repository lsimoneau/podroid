package com.podling.podroid;

import org.the86.The86;
import org.the86.The86Impl;
import org.the86.exception.The86Exception;
import org.the86.model.Authorization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticationActivity extends Activity {
	private EditText email;
	private EditText password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentication);
		email = ((EditText) findViewById(R.id.authentication_email));
		password = ((EditText) findViewById(R.id.authentication_password));

		Button button = (Button) findViewById(R.id.btnLogin);

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AuthenticateTask(v.getContext()).execute();
			}
		});
	}

	class AuthenticateTask extends AsyncTask<Void, Void, Authorization> {
		protected ProgressDialog dialog;
		private Context context;

		public AuthenticateTask(Context context) {
			this.context = context;
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage("logging in");
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected Authorization doInBackground(Void... params) {
			try {
				The86 the86 = new The86Impl("https://podling.com", email
						.getText().toString(), password.getText().toString());

				((PodroidApplication) getApplicationContext()).setThe86(the86);

				return the86.getAuthorization();
			} catch (The86Exception e) {
				return null;
			}
		}

		protected void onPostExecute(Authorization authorization) {
			dialog.dismiss();
			if (authorization != null) {
				// success!
				startActivity(GroupsActivity.newInstance(context));
				AuthenticationActivity.this.finish();
			} else {
				Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT)
						.show();
			}
		}

	}

}
