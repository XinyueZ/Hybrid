package com.hybrid.app.bus;

import com.hybrid.app.data.AppListItem;

/**
 * A call to an external app, open or direct to store when not be installed before.
 *
 * @author Xinyue Zhao
 */
public final class LinkToExternalAppEvent {
	/**
	 * The data-set represent an external app.
	 */
	private AppListItem mAppListItem;

	/**
	 * Constructor of LinkToExternalAppEvent
	 *
	 * @param _appListItem
	 * 		The data-set represent an external app.
	 */
	public LinkToExternalAppEvent(AppListItem _appListItem) {
		mAppListItem = _appListItem;
	}

	/**
	 * Get the data-set represent an external app.
	 */
	public AppListItem getAppListItem() {
		return mAppListItem;
	}
}
