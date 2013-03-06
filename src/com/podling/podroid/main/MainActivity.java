package com.podling.podroid.main;

import java.io.File;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.podling.podroid.PodroidApplication;
import com.podling.podroid.TabListener;

public class MainActivity extends Activity {

	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 5; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setup disk cache
		File cacheDir = getDiskCacheDir(getApplicationContext(),
				DISK_CACHE_SUBDIR);
		new InitDiskCacheTask().execute(cacheDir);

		ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);

		Tab tab = actionBar
				.newTab()
				.setText("latest")
				.setTabListener(
						new TabListener<LatestConversationsFragment>(this,
								"latest", LatestConversationsFragment.class));
		actionBar.addTab(tab);

		tab = actionBar
				.newTab()
				.setText("groups")
				.setTabListener(
						new TabListener<GroupsFragment>(this, "groups",
								GroupsFragment.class));
		actionBar.addTab(tab);
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

	// Creates a unique subdirectory of the designated app cache directory.
	// Tries to use external
	// but if not mounted, falls back on internal storage.
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable() ? context
				.getExternalCacheDir().getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + uniqueName);
	}
}
