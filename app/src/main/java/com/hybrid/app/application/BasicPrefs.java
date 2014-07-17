package com.hybrid.app.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hybrid.app.bus.ApplicationConfigurationDownloadedEvent;
import com.hybrid.app.bus.BusProvider;
import com.hybrid.app.net.TaskHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Basic class that provides Preference storage and make it easy to store data.
 * 
 * Forget now edit().xxx.commit().
 * 
 * @author Xinyue Zhao
 * 
 */
public class BasicPrefs {
	/**
	 * Standard name of application's properties that contains url to the app's
	 * config.
	 */
	private static final String APP_PROPERTIES = "app.properties";
	/** Url to the application's configuration. */
	public static final String APP_CONFIG = "app_config";
	/** Fallback Url to the application's configuration. */
	public static final String APP_CONFIG_FALLBACK = "app_config_fallback";
	protected SharedPreferences preference = null;
	protected Context mContext;

	/**
	 * Get the url to the application's configuration.
	 * 
	 * @param context
	 *            A context object.
	 * @return The url to the app's config.
	 */
	private String getCfgUrl(Context context) {
		Properties prop = new Properties();
		InputStream input = null;
		String url = null;
		try {
			input = context.getClassLoader().getResourceAsStream(APP_PROPERTIES);
			// load a properties file
			prop.load(input);

			url = prop.getProperty(APP_CONFIG);
			setString(APP_CONFIG, url);
			url = prop.getProperty(APP_CONFIG_FALLBACK);
			setString(APP_CONFIG_FALLBACK, url);
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
	 * Get url to application's configuration..
	 * 
	 * @return Url in string.
	 */
	String getAppConfigUrl() {
		return getString(APP_CONFIG, null);
	}

	/**
	 * Get fallback url to application's configuration..
	 *
	 * @return Url in string.
	 */
	String getAppConfigFallbackUrl() {
		return getString(APP_CONFIG_FALLBACK, null);
	}


	/**
	 * Download application's configuration, internal will use url that has been
	 * loaded from app.properties. It could use fallback if the url is invalid.
	 */
	public  void downloadApplicationConfiguration() {
		/*
		 * Request app's configuration.
		 */
		StringRequest request = new StringRequest(Request.Method.GET, Prefs.getInstance().getAppConfigUrl(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						LL.i(":) Loaded app's config: " + Prefs.getInstance().getAppConfigUrl());
						writePrefsWithStream(new ByteArrayInputStream(response.getBytes()));
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Prefs prefs = Prefs.getInstance();
						LL.w(":( Can't load remote config: " + prefs.getAppConfigUrl());
						LL.i(":) We load fallback: " + prefs.getAppConfigFallbackUrl());
						writePrefsWithStream(mContext.getClassLoader().getResourceAsStream(prefs.getAppConfigFallbackUrl()));
					}
				});
		TaskHelper.getRequestQueue().add(request);
	}

	/**
	 * Read .properties of app's configuration in stream and write into
	 * preference. Finally send event to info front.
	 * 
	 * @param input
	 *            An input-stream.
	 */
	private static void writePrefsWithStream(InputStream input) {
		Properties prop = new Properties();
		try {
			prop.load(input);
			Set<String> names = prop.stringPropertyNames();
			String valueStr;
			/*
			 * Read all properties and store into Android's preference.
			 */
			for (String name : names) {
				valueStr = prop.getProperty(name);
				Prefs.getInstance().setString(name, valueStr);
			}
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
			/* Read and info front. */
			BusProvider.getBus().post(new ApplicationConfigurationDownloadedEvent());
		}
	}
}
