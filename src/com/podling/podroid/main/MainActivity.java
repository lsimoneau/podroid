package com.podling.podroid.main;

import java.io.File;

import org.the86.The86;
import org.the86.The86Impl;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.podling.podroid.AuthenticationActivity;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.TabListener;

public class MainActivity extends Activity {

	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 5; // 5 MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	private ActionBar actionBar;
	private boolean tabsSetup = false;
	private The86 the86;

	public static Intent newInstance(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setupDiskCache();

		actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		SharedPreferences prefs = getSharedPreferences("com.podling.podroid",
				Context.MODE_PRIVATE);
		String userId = prefs.getString("userId", null);
		String userAuthToken = prefs.getString("userAuthToken", null);

		if (userId == null || userAuthToken == null) {
			startActivity(AuthenticationActivity.newInstance(this));
			finish();
		} else {
			the86 = new The86Impl(PodroidApplication.THE86_HOSTNAME);

			the86.setAuthorization(userId, userAuthToken);
			((PodroidApplication) getApplicationContext()).setThe86(the86);
			createTabs();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		the86 = ((PodroidApplication) getApplicationContext()).getThe86();

		if (!tabsSetup && the86 != null) {
			createTabs();
		}
	}

	private void setupDiskCache() {
		File cacheDir = getDiskCacheDir(getApplicationContext(),
				DISK_CACHE_SUBDIR);
		new InitDiskCacheTask().execute(cacheDir);
	}

	public void createTabs() {

		Tab tab = actionBar
				.newTab()
				.setText("latest")
				.setTabListener(
						new TabListener<LatestConversationsFragment>(this,
								"latest", LatestConversationsFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText("pods")
				.setTabListener(
						new TabListener<GroupsFragment>(this, "pods",
								GroupsFragment.class));
		actionBar.addTab(tab);

		tabsSetup = true;
	}

	class InitDiskCacheTask extends AsyncTask<File, Void, Void> {
		@Override
		protected Void doInBackground(File... params) {
			PodroidApplication app = ((PodroidApplication) getApplication());
			synchronized (app.getDiskCacheLock()) {
				File cacheDir = params[0];

				BitmapLruCache.Builder cacheBuilder = new BitmapLruCache.Builder();
				cacheBuilder.setDiskCacheMaxSize(DISK_CACHE_SIZE);
				cacheBuilder.setDiskCacheEnabled(true);
				cacheBuilder.setMemoryCacheEnabled(true);
				cacheBuilder.setDiskCacheLocation(cacheDir);

				app.setDiskCache(cacheBuilder.build());
			}
			return null;
		}
	}

	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable() ? context
				.getExternalCacheDir().getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + uniqueName);
	}
}
