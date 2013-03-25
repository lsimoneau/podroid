package com.podling.podroid.main;

import java.io.File;

import org.the86.The86;
import org.the86.The86Impl;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.podling.podroid.AuthenticationActivity;
import com.podling.podroid.PodroidApplication;
import com.podling.podroid.R;
import com.podling.podroid.util.Storage;

public class MainActivity extends SherlockFragmentActivity {

	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 5; // 5 MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	private ActionBar actionBar;
	private ViewPager mViewPager;
	private MainPagerAdapter mPagerAdapter;
	private The86 the86;

	public static Intent newInstance(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupDiskCache();
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		SharedPreferences prefs = getSharedPreferences("com.podling.podroid",
				Context.MODE_PRIVATE);
		String userId = prefs.getString("userId", null);
		String userAuthToken = prefs.getString("userAuthToken", null);

		if (userId == null || userAuthToken == null) {
			startActivity(AuthenticationActivity.newInstance(this));
			finish();
			return;
		}
		
		the86 = new The86Impl(PodroidApplication.THE86_HOSTNAME);

		the86.setAuthorization(userId, userAuthToken);
		((PodroidApplication) getApplicationContext()).setThe86(the86);
		
		this.setContentView(R.layout.main);
		
		mViewPager = (ViewPager) findViewById(R.id.main_pager);
		mViewPager.setOffscreenPageLimit(1);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// When swiping between pages, select the
						// corresponding tab.
						getSupportActionBar().setSelectedNavigationItem(
								position);
					}
				});

		createTabs();
		
		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState.getInt(
					"tab", 0));
		}
	
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (mPagerAdapter == null) {
			mPagerAdapter = new MainPagerAdapter(
					getSupportFragmentManager());
			mViewPager.setAdapter(mPagerAdapter);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
	}

	private void setupDiskCache() {
		File cacheDir = getDiskCacheDir(getApplicationContext(),
				DISK_CACHE_SUBDIR);
		new InitDiskCacheTask().execute(cacheDir);
	}

	public void createTabs() {
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub

			}
		};
		
		actionBar.addTab(actionBar.newTab().setText("pods")
				.setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("latest")
				.setTabListener(tabListener));
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
				|| !Storage.isExternalStorageRemovable() ? context
				.getExternalCacheDir().getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + uniqueName);
	}
}
