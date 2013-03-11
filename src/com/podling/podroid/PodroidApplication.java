package com.podling.podroid;

import org.the86.The86;

import uk.co.senab.bitmapcache.BitmapLruCache;
import android.app.Application;

public class PodroidApplication extends Application {

	public static final String THE86_HOSTNAME = "https://podling.com";

	public static final int LATEST_CONVERSATIONS_LOADER_ID = 0;
	public static final int GROUPS_LOADER_ID = 1;
	public static final int GROUP_CONVERSATIONS_LOADER_ID = 2;
	public static final int GROUP_MEMBERS_LOADER_ID = 3;
	public static final int POSTS_LOADER_ID = 4;

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
