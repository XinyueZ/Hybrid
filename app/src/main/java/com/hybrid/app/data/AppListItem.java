package com.hybrid.app.data;

import com.google.gson.annotations.SerializedName;

public final class AppListItem {
	@SerializedName("name")
	private String mName;
	@SerializedName("packageName")
	private String mPackageName;
	@SerializedName("playstore_url")
	private String mPlaystoreUrl;

	public AppListItem(String _name, String _packageName, String _playstoreUrl) {
		mName = _name;
		mPackageName = _packageName;
		mPlaystoreUrl = _playstoreUrl;
	}

	public String getName() {
		return mName;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getPlaystoreUrl() {
		return mPlaystoreUrl;
	}
}
