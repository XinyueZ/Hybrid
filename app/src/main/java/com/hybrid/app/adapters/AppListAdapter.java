package com.hybrid.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hybrid.app.R;
import com.hybrid.app.data.AppListItem;
import com.hybrid.app.net.TaskHelper;

/**
 * The Adapter for the ListView showing external apps.
 */
public final class AppListAdapter extends BaseAdapter {

	public static final int LAYOUT_ITEM = R.layout.item_app;
	private AppListItem[] mList;
	private int mSize;
	private final LayoutInflater mInflater;
	private ImageLoader mImageLoader;

	public AppListAdapter(Context context, AppListItem[] list) {
		mInflater = LayoutInflater.from(context);
		setList(list);
		mImageLoader = TaskHelper.getImageLoader();
	}

	public void setList(AppListItem[] list) {
		mList = list;
		mSize = list.length;
	}

	@Override
	public int getCount() {
		return mSize;
	}

	@Override
	public Object getItem(int position) {
		return mList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;

		if (convertView == null) {
			convertView = mInflater.inflate(LAYOUT_ITEM, parent, false);
			vh = new ViewHolder(convertView.findViewById(R.id.iv_app_logo), convertView.findViewById(R.id.tv_app_name),
					convertView.findViewById(R.id.btn_start_app));
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		AppListItem item = mList[position];
		Log.d("hybrid", "logo url:"  + item.getLogoUrl());
		vh.AppLogo.setDefaultImageResId(R.drawable.ic_launcher);
		vh.AppLogo.setImageUrl(item.getLogoUrl(), TaskHelper.getImageLoader() );
		vh.AppName.setText(item.getName());

		return convertView;
	}

	private static class ViewHolder {
		NetworkImageView AppLogo;
		TextView AppName;
		Button AppStart;

		private ViewHolder(View _appLogo, View _appName, View _appStart) {
			AppLogo = (NetworkImageView) _appLogo;
			AppName = (TextView) _appName;
			AppStart = (Button) _appStart;
		}
	}
}
