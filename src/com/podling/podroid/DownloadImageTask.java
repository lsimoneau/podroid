package com.podling.podroid;

import java.io.InputStream;
import java.net.URL;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	private ImageView bmImage;
	private PodroidApplication application;

	public DownloadImageTask(PodroidApplication application, ImageView bmImage) {
		this.application = application;
		this.bmImage = bmImage;
	}

	protected Bitmap doInBackground(String... urls) {
		String imageUrl = urls[0];
		try {
			Bitmap bitmap = getBitmapFromDiskCache(imageUrl);

			if (bitmap == null) { // Not found in disk cache
				InputStream in = new URL(imageUrl).openStream();
				bitmap = BitmapFactory.decodeStream(in);
			}

			// Add final bitmap to caches
			addBitmapToCache(imageUrl, bitmap);

			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(Bitmap result) {
		bmImage.setImageBitmap(result);
	}

	public void addBitmapToCache(String key, Bitmap bitmap) {
		// Also add to disk cache
		synchronized (application.getDiskCacheLock()) {
			BitmapLruCache cache = application.getDiskCache();

			if (cache != null && cache.get(key) == null) {
				cache.put(key, bitmap);
			}
		}
	}

	public Bitmap getBitmapFromDiskCache(String key) {
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