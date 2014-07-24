package com.hybrid.app;

import com.android.volley.VolleyError;
import com.chopping.bus.ApplicationConfigurationDownloadedEvent;
import com.hybrid.app.bus.ExternalAppChangedEvent;
import com.hybrid.app.bus.LinkToExternalAppEvent;
import com.hybrid.app.data.AppList;
import com.squareup.otto.Subscribe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Setting .
 */
public final class SettingsActivity extends BaseActivity {

	private static final int LAYOUT = R.layout.activity_settings;
	// ------------------------------------------------
	// Subscribes, event-handlers
	// ------------------------------------------------
	@Subscribe
	public void onApplicationConfigurationDownloaded(ApplicationConfigurationDownloadedEvent e) {
		super.onApplicationConfigurationDownloaded(e);
	}


	@Subscribe
	public void onVolleyError(VolleyError e) {
		super.onVolleyError(e);
	}

	@Subscribe
	public void onAppListLoaded(AppList e) {
		super.onAppListLoaded(e);
	}

	@Subscribe
	public void onExternalAppChanged(ExternalAppChangedEvent e) {
		super.onExternalAppChanged(e);
	}

	@Subscribe
	public void onLinkToExternalApp(LinkToExternalAppEvent e) {
		super.onLinkToExternalApp(e);
	}

	// ------------------------------------------------

	/**
	 * Show an instance of SettingsActivity.
	 * @param context A context object.
	 */
	public static void showInstance(Context context) {
		Intent intent = new Intent(context, SettingsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
	}

}
