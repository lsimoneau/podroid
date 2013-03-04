package com.podling.podroid;

import org.the86.The86;

import android.app.Application;

public class PodroidApplication extends Application {

	private The86 the86;

	public The86 getThe86() {
		return the86;
	}

	public void setThe86(The86 the86) {
		this.the86 = the86;
	}
}
