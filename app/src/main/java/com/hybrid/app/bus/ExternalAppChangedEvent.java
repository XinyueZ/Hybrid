package com.hybrid.app.bus;

/**
 * Event fired after an external App has been installed or removed.
 *
 * @author Xinyue Zhao
 */
public final class ExternalAppChangedEvent {
	/**
	 * Package-Name of the installed external App.
	 */
	private final String mPackageName;

	public ExternalAppChangedEvent(String _packageName) {
		mPackageName = _packageName;
	}

	/**
	 * Package-Name of the installed external App.
	 */
	public String getPackageName() {
		return mPackageName;
	}
}
