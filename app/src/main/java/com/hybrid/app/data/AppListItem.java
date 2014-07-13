package com.hybrid.app.data;

import com.google.gson.annotations.SerializedName;

public final class AppListItem {
	@SerializedName("name")
	private String mName;
	@SerializedName("free")
	private String mFree;
	@SerializedName("packageName")
	private String mPackageName;
	@SerializedName("logo_url")
	private String mLogoUrl;
	@SerializedName("playstore_url")
	private String mPlaystoreUrl;

	public AppListItem(String _name, String _free, String _packageName, String _logoUrl, String _playstoreUrl) {
		mName = _name;
		mFree = _free;
		mPackageName = _packageName;
		mLogoUrl = _logoUrl;
		mPlaystoreUrl = _playstoreUrl;
	}

	public String getName() {
		return mName;
	}

	public String getFree() {
		return mFree;
	}

	public String getPackageName() {
		return mPackageName;
	}

	public String getLogoUrl() {
		return mLogoUrl;
	}

	public String getPlaystoreUrl() {
		return mPlaystoreUrl;
	}
}
