package com.podling.podroid;

import java.io.InputStream;
import java.net.URL;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.podling.podroid.adapter.AvatarViewHolder;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private final PodroidApplication application;
	private final AvatarViewHolder holder;
	private final int position;

	public DownloadImageTask(PodroidApplication application, int position,
			AvatarViewHolder holder) {
		this.application = application;
		this.holder = holder;
		this.position = position;
	}

	protected Bitmap doInBackground(String... urls) {
		String imageUrl = urls[0];
		try {
			Bitmap bitmap = getBitmapFromDiskCache(imageUrl);

			if (bitmap == null) {
				InputStream in = new URL(imageUrl).openStream();
				bitmap = BitmapFactory.decodeStream(in);
			}

			addBitmapToCache(imageUrl, bitmap);

			return bitmap;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Bitmap result) {
		if (holder.position == position) {
			holder.avatar.setImageBitmap(result);
		}
	}

	private void addBitmapToCache(String key, Bitmap bitmap) {
		// Also add to disk cache
		synchronized (application.getDiskCacheLock()) {
			BitmapLruCache cache = application.getDiskCache();

			if (cache != null && cache.get(key) == null) {
				cache.put(key, bitmap);
			}
		}
	}

	private Bitmap getBitmapFromDiskCache(String key) {
		synchronized (application.getDiskCacheLock()) {
			// Wait while disk cache is started from background thread
			while (application.diskCacheStarting()) {
				try {
					application.getDiskCacheLock().wait();
				} catch (InterruptedException e) {
				}
			}
			BitmapLruCache cache = application.getDiskCache();
			if (cache != null && cache.get(key) != null) {
				return cache.get(key).getBitmap();
			}
		}
		return null;
	}
}