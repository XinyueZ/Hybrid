package com.hybrid.app.application;

import com.chopping.net.TaskHelper;

import android.app.Application;

public final class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		init();

	}

	private void init() {
		Prefs.createInstance(this);
		TaskHelper.init(getApplicationContext());
	}
}
