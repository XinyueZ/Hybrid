package com.hybrid.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.hybrid.app.data.AppListItem;

/**
 * Util/Tools of app.
 *
 * @author Xinyue Zhao
 */
public final class Utils {

	public static void showLongToast(Context context, int messageId) {
		Toast.makeText(context, context.getString(messageId), Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, int messageId) {
		Toast.makeText(context, context.getString(messageId), Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/** Standard sharing app for sharing on actionbar.*/
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider provider,
	                                           String subject, String body) {
		if (provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, body);
			provider.setShareIntent(i);
			return i;
		}
		return null;
	}


	/**
	 * Link to an external app that has _packageName. If the App has not been
	 * installed, then links to store.
	 *
	 * It will be tracked by Tracker.
	 *
	 * @param context A context object
	 * @param app The app to open or direct to store if not be installed before.
	 */
	public static void linkToExternalApp(Context context,    AppListItem app ) {
		/* Try to find the app with _packageName. */
		boolean found;
		PackageManager pm = context.getPackageManager();
		found = isAppInstalled(app.getPackageName(), pm);
		/* Launch the App or go to store. */
		if (found) {
			/* Found. Start app. */
			Intent LaunchIntent = pm.getLaunchIntentForPackage(app.getPackageName());
			context.startActivity(LaunchIntent);
		} else {
			/*To Store.*/
			openUrl(context, app.getPlaystoreUrl());
		}
	}

	/**
	 * Check whether the App with _packageName has been installed or not.
	 *
	 * @param packageName
	 * @param pm
	 * @return
	 */
	public static boolean isAppInstalled(String packageName, PackageManager pm) {
		boolean found;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			found = true;
		} catch (PackageManager.NameNotFoundException e) {
			found = false;
		}
		return found;
	}

	/**
	 * Link to an external view.
	 *
	 * @param context
	 * @param to
	 */
	public static void openUrl(Context context, String to) {
		if (context != null) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(to));
			context.startActivity(i);
		}
	}
}
