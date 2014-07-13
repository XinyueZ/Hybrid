package com.hybrid.app.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hybrid.app.R;

/**
 * Util/Tools of app.
 */
public final class Utils {

	public static void showLongToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context _context, int _messageId) {
		Toast.makeText(_context, _context.getString(_messageId), Toast.LENGTH_SHORT).show();
	}

	public static void showLongToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_LONG).show();
	}

	public static void showShortToast(Context _context, String _message) {
		Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
	}

	/** Standard sharing app for sharing on actionbar.*/
	public static Intent getDefaultShareIntent(android.support.v7.widget.ShareActionProvider _provider,
	                                           String _subject, String _body) {
		if (_provider != null) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(android.content.Intent.EXTRA_SUBJECT, _subject);
			i.putExtra(android.content.Intent.EXTRA_TEXT, _body);
			_provider.setShareIntent(i);
			return i;
		}
		return null;
	}

	/**
	 * Use packageName to detect and find the app-logo.
	 * @param _packageName The package name of an app that needs a logo.
	 * @return The resId of the logo.
	 */
	public static int getAppLogo(String _packageName) {
		if(_packageName.contains("sina")) {
			return R.drawable.ic_sina_weibo_logo;
		}
		if (_packageName.contains("facebook")) {
			return R.drawable.ic_facebook_logo;
		}
		if(_packageName.contains("twitter")) {
			return R.drawable.ic_twitter_logo;
		}
		return R.drawable.ic_action_hybrid;
	}
}
