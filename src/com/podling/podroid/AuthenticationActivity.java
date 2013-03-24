package com.podling.podroid;

import org.the86.The86;
import org.the86.The86Impl;
import org.the86.exception.The86Exception;
import org.the86.model.Authorization;
import org.the86.model.User;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.podling.podroid.main.MainActivity;

public class AuthenticationActivity extends Activity {
	private EditText emailEdit;
	private EditText passwordEdit;
	private The86 the86;

	public static Intent newInstance(Context context) {
		Intent intent = new Intent(context, AuthenticationActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentication);

		getActionBar().hide();

		the86 = new The86Impl(PodroidApplication.THE86_HOSTNAME);

		emailEdit = ((EditText) findViewById(R.id.authentication_email));
		passwordEdit = ((EditText) findViewById(R.id.authentication_password));

		Button button = (Button) findViewById(R.id.btnLogin);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new AuthenticateTask(v.getContext()).execute();
			}
		});
	}

	private void authSuccess(Authorization authorization) {
		User user = authorization.getUser();

		SharedPreferences prefs = getSharedPreferences("com.podling.podroid",
				Context.MODE_PRIVATE);
		prefs.edit().putString("userId", user.getId()).commit();
		prefs.edit()
				.putString("userAuthToken", authorization.getUserAccessToken())
				.commit();

		((PodroidApplication) getApplicationContext()).setThe86(the86);

		startActivity(MainActivity.newInstance(this));
		finish();
	}

	class AuthenticateTask extends AsyncTask<Void, Void, Authorization> {
		protected ProgressDialog dialog;
		private Context context;

		public AuthenticateTask(Context context) {
			this.context = context;
			Resources res = context.getResources();
			// create a progress dialog
			dialog = new ProgressDialog(context);
			dialog.setMessage(res.getString(R.string.signing_in));
			dialog.setCancelable(false);
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();
		}

		protected Authorization doInBackground(Void... params) {
			try {
				String email = emailEdit.getText().toString();
				String password = passwordEdit.getText().toString();

				return the86.authorize(email, password);
			} catch (The86Exception e) {
				return null;
			}
		}

		protected void onPostExecute(Authorization authorization) {
			dialog.dismiss();
			if (authorization != null) {
				// success!
				authSuccess(authorization);
			} else {
				Toast.makeText(
						context,
						context.getResources().getString(
								R.string.authentication_failed),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
