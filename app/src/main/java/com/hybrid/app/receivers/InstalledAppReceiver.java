package com.hybrid.app.receivers;

import com.chopping.bus.BusProvider;
import com.hybrid.app.bus.ExternalAppChangedEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Event that will be sent after an external App has been installed.
 *
 * @author Xinyue Zhao
 */
public final class InstalledAppReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context _context, Intent _intent) {
		//Info UI to refresh button status.
		Uri data = _intent.getData();
		String packageName = data.getSchemeSpecificPart();
		BusProvider.getBus().post(new ExternalAppChangedEvent(packageName));
	}
}
