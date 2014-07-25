package com.hybrid.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarPreferenceActivity;

/**
 * Setting .
 */
public final class SettingsActivity extends ActionBarPreferenceActivity {

	private static final int LAYOUT = R.layout.activity_settings;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
