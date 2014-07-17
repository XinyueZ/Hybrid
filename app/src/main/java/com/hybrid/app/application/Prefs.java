package com.hybrid.app.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

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
 * Store app and device information.
 * 
 * @author Chris.Xinyue Zhao
 */
public final class Prefs extends BasicPrefs {
	/** The Constant VERSION. */
	private final static String VERSION = "DeviceData.version";
	/** The Constant DEVICE_ID. */
	private final static String DEVICE_ID = "DeviceData.deviceid";
	/** The Constant MODEL. */
	private final static String MODEL = "DeviceData.model";
	/** The Constant OS. */
	private final static String OS = "DeviceData.os";
	/** The Constant OS_VERSION. */
	private final static String OS_VERSION = "DeviceData.osversion";
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
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			setAppVersion(info.versionName);
			if (TextUtils.isEmpty(android.os.Build.MODEL)) {
				setDeviceModel("UNKNOWN");
			} else {
				setDeviceModel(android.os.Build.MODEL);
			}
			setOs("ANDROID");
			setOsVersion(android.os.Build.VERSION.RELEASE);
		} catch (NameNotFoundException _e) {
			_e.printStackTrace();
		} catch (Exception _e) {
			_e.printStackTrace();
		}
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
	// Description: System's preference.
	//
	// Below defines set/get methods for system properties, like
	// OS-type, App-Version etc.
	//----------------------------------------------------------

	/**
	 * Get app version.
	 * 
	 * @return the app version
	 */
	public String getAppVersion() {
		return getString(VERSION, "");
	}

	/**
	 * Save app version.
	 * 
	 * @param value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setAppVersion(String value) {
		return setString(VERSION, value);
	}



	/**
	 * Save device id.
	 * 
	 * @param value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setDeviceId(String value) {
		return setString(DEVICE_ID, value);
	}

	/**
	 * Get device model.
	 * 
	 * @return the device model
	 */
	public String getDeviceModel() {
		return getString(MODEL, "");
	}

	/**
	 * Save device model.
	 * 
	 * @param value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setDeviceModel(String value) {
		return setString(MODEL, value);
	}

	/**
	 * Get OS name.
	 * 
	 * @return the os
	 */
	public String getOs() {
		return getString(OS, "");
	}

	/**
	 * Save OS name.
	 * 
	 * @param value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setOs(String value) {
		return setString(OS, value);
	}

	/**
	 * Get OS version.
	 *
	 * @return the os version
	 */
	public String getOsVersion() {
		return getString(OS_VERSION, "");
	}

	/**
	 * Save current OS version.
	 * 
	 * @param value
	 *            the _value
	 * @return true, if successful
	 */
	private boolean setOsVersion(String value) {
		return setString(OS_VERSION, value);
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
