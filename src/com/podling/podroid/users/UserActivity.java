package com.podling.podroid.users;

import org.the86.The86;
import org.the86.exception.The86Exception;
import org.the86.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.podling.podroid.DownloadImageTask;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;

public class UserActivity extends Activity {
	private The86 the86;
	protected LinearLayout progress;

	public static Intent newInstance(Context context, String userId) {
		Intent intent = new Intent(context, UserActivity.class);
		intent.putExtra("userId", userId);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		setContentView(R.layout.user);
		progress = (LinearLayout) findViewById(R.id.user_loading_progress);
		the86 = ((PodroidApplication) getApplicationContext()).getThe86();
		new RetrieveUserTask().execute(extras.getString("userId"));
	}

	private void populate(User user) {
		ImageView avatar = (ImageView) findViewById(R.id.user_avatar);
		if (user.getAvatarUrl() != null) {
			new DownloadImageTask((PodroidApplication) getApplication(), avatar)
					.execute(user.getAvatarUrl());
		}

		TextView name = (TextView) findViewById(R.id.user_name);
		name.setText(user.getName());

		TextView twitterHandle = (TextView) findViewById(R.id.user_twitter_handle);
		twitterHandle.setText(user.getTwitterUsername());

		TextView location = (TextView) findViewById(R.id.user_location);
		location.setText(user.getLocation());

		TextView profile = (TextView) findViewById(R.id.user_profile);
		profile.setText(user.getProfile());
	}

	class RetrieveUserTask extends AsyncTask<String, Void, User> {

		protected void onPreExecute() {
			super.onPreExecute();
			progress.setVisibility(View.VISIBLE);
		}

		protected User doInBackground(String... params) {
			String userId = params[0];
			try {
				return the86.getUser(userId);
			} catch (The86Exception e) {
				return null;
			}
		}

		protected void onPostExecute(User user) {
			populate(user);
			progress.setVisibility(View.VISIBLE);
		}

	}
}
