package com.podling.podroid.util;

import org.the86.The86;

import com.podling.podroid.PodroidApplication;

import android.app.Activity;

public class The86Util {

	public static The86 get(Activity activity) {
		return ((PodroidApplication) activity.getApplicationContext())
				.getThe86();
	}

}
