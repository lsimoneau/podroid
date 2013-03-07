package com.podling.podroid;

import org.the86.The86;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.Application;

public class PodroidApplication extends Application {

	public static final String THE86_HOSTNAME = "https://podling.com";

	private The86 the86;
	private BitmapLruCache diskLruCache = null;
	private boolean diskCacheStarting = true;
	private final Object diskCacheLock = new Object();

	public The86 getThe86() {
		return the86;
	}

	public void setThe86(The86 the86) {
		this.the86 = the86;
	}

	// TODO wrap disk stuff in interface, pass to tasks

	public Object getDiskCacheLock() {
		return diskCacheLock;
	}

	public boolean diskCacheStarting() {
		return diskCacheStarting;
	}

	public BitmapLruCache getDiskCache() {
		return diskLruCache;
	}

	public void setDiskCache(BitmapLruCache bitmapLruCache) {
		this.diskLruCache = bitmapLruCache;
		this.diskCacheStarting = false; // Finished initialization
		this.diskCacheLock.notifyAll(); // Wake any waiting threads

	}
}
