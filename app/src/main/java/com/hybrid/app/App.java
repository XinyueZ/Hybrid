package com.hybrid.app;

import android.app.Application;

import com.hybrid.app.net.TaskHelper;


public final class App extends Application  {
	 

	@Override
	public void onCreate() {
		super.onCreate();
		init();

	}



	private void init() { 
		TaskHelper.init(getApplicationContext()); 
	}
}
