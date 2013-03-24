package com.podling.podroid.loader;

import org.the86.The86;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.podling.podroid.util.The86Util;

public abstract class The86AsyncTaskLoader<T> extends AsyncTaskLoader<T> {

	private final The86 the86;

	public The86AsyncTaskLoader(Context context) {
		super(context);
		the86 = The86Util.get((Activity) context);
	}

	protected The86 getThe86() {
		return the86;
	}
}
