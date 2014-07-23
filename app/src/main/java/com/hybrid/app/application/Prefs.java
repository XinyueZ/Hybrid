package com.hybrid.app.application;

import android.content.Context;

import com.chopping.BasicPrefs;

/**
 * Store app and device information.
 * 
 * @author Chris.Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/** Storage. The url of web-app.*/
	private final static String KEY_WEB_APP_URL = "app_url";
	/** Storage. The url to the list of apps.*/
	private final static String KEY_APP_LIST = "app_list_url";

	/** The Instance. */
	private static Prefs sInstance;

	private Prefs() {
		super(null);
	}

	/**
	 * Created a DeviceData storage.
	 * @param context A context object.
	 */
	private Prefs(Context context) {
		super(context);
	}



	/**
	 * Singleton method.
	 * @param context A context object.
	 * @return single instance of DeviceData
	 */
	public static Prefs createInstance(Context context) {
		if (sInstance == null) {
			synchronized (Prefs.class) {
				if (sInstance == null) {
					sInstance = new Prefs(context);
				}
			}
		}
		return sInstance;
	}

	/**
	 * Singleton getInstance().
	 * @return The instance of Prefs.
	 */
	public static Prefs getInstance() {
		return sInstance;
	}



	//----------------------------------------------------------
	// Description: Application's preference.
	//
	// Below defines set/get methods for preference of the whole
	// App, inc. data that was stored in app's config or local.
	//----------------------------------------------------------
	/**
	 * Url of the web-app.
	 * @return Url in string.
	 */
	public String getWebAppUrl()  {
		return getString(KEY_WEB_APP_URL,  null );
	}

	/**
	 * Url of the list of apps.
	 * @return Url in string.
	 */
	public String getAppListUrl()  {
		return getString(KEY_APP_LIST,  null );
	}
}
