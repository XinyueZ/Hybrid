package com.hybrid.app.application;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Basic class that provides Preference storage and make it easy to store data.
 *
 * Forget now edit().xxx.commit().
 * 
 * @author Xinyue Zhao
 * 
 */
public class BasicPrefs {
	/** Standard name of application's properties that contains url to the app's config.*/
	private static final String APP_PROPERTIES = "app.properties";
	/** Url to the application's configuration.*/
	public static final String APP_CONFIG = "app_config";
	/** Storage for url of app's configuration. **/
	private static final  String KEY_APP_CONFIG = "key.app.config";

	protected SharedPreferences preference = null;
	protected Context mContext;

	/**
	 * Get the url to the application's configuration.
	 * @param context A context object.
	 * @return The url to the app's config.
	 */
	private   String getCfgUrl(Context context) {
		Properties prop = new Properties();
		InputStream input = null;
		String url = null;
		try {
			input = context.getClassLoader().getResourceAsStream(APP_PROPERTIES);
			// load a properties file
			prop.load(input);
			// get the property value and print it out.
			url = prop.getProperty(APP_CONFIG);
			setString(KEY_APP_CONFIG, url);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return url;
	}

	public BasicPrefs(Context context) {
		mContext = context;
		preference = context.getSharedPreferences(getClass().getPackage().toString(), Context.MODE_PRIVATE);
		getCfgUrl(context);
	}


	public String getString(String key, String defValue) {
		return preference.getString(key, defValue);
	}


	public boolean setString(String key, String value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putString(key, value);
		return edit.commit();
	}


	public boolean getBoolean(String key, boolean defValue) {
		return preference.getBoolean(key, defValue);
	}


	public boolean setBoolean(String key, boolean value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putBoolean(key, value);
		return edit.commit();
	}


	public int getInt(String key, int defValue) {
		return preference.getInt(key, defValue);
	}


	public boolean setInt(String key, int value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putInt(key, value);
		return edit.commit();
	}


	public long getLong(String key, long defValue) {
		return preference.getLong(key, defValue);
	}


	public boolean setLong(String key, long value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putLong(key, value);
		return edit.commit();
	}


	public float getFloat(String key, float defValue) {
		return preference.getFloat(key, defValue);
	}


	public boolean setFloat(String key, float value) {
		SharedPreferences.Editor edit = preference.edit();
		edit.putFloat(key, value);
		return edit.commit();
	}


	public boolean contains(String key) {
		return preference.contains(key);
	}

	/**
	 * Get  url to application's configuration..
	 *
	 * @return Url in string.
	 */
	protected String getAppConfig() {
		return getString(KEY_APP_CONFIG, null);
	}
}
