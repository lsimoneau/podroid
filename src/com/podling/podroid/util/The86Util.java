package com.podling.podroid.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.the86.The86;

import com.podling.podroid.PodroidApplication;

import android.app.Activity;
import android.text.format.DateUtils;

public class The86Util {

	public static The86 get(Activity activity) {
		return ((PodroidApplication) activity.getApplicationContext())
				.getThe86();
	}

	public static String getRelativeLocalTime(Date date) {
		long time = date.getTime()
				+ TimeZone.getDefault().getOffset(date.getTime());
		long now = Calendar.getInstance().getTimeInMillis();
		return DateUtils.getRelativeTimeSpanString(time, now,
				DateUtils.MINUTE_IN_MILLIS).toString();
	}
}
