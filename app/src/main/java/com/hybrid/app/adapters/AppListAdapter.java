package com.hybrid.app.adapters;

import com.android.volley.toolbox.NetworkImageView;
import com.chopping.bus.BusProvider;
import com.chopping.net.TaskHelper;
import com.hybrid.app.R;
import com.hybrid.app.bus.LinkToExternalAppEvent;
import com.hybrid.app.data.AppListItem;
import com.hybrid.app.utils.Utils;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * The Adapter for the ListView showing external apps.
 *
 * @author Xinyue Zhao
 */
public final class AppListAdapter extends BaseAdapter {

	public static final int LAYOUT_ITEM = R.layout.item_app;
	private List<AppListItem> mList;
	private int mSize;
	private LayoutInflater mInflater;
	private PackageManager mPackageManager;
	private int mInsTxtClr;
	private int mUninsTxtClr;

	public AppListAdapter(Context context, List<AppListItem> list) {
		setList(list);
		init(context);
	}

	public AppListAdapter(Context context) {
		init(context);
	}

	private void init(Context context) {
		mInflater = LayoutInflater.from(context);
		mPackageManager = context.getPackageManager();
		Resources resources = context.getResources();
		mInsTxtClr = resources.getColor(R.color.installed_text);
		mUninsTxtClr = resources.getColor(R.color.not_installed_text);
	}


	public void setList(List<AppListItem> list) {
		mList = list;
		mSize = list.size();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mSize ;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
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
			vh = new ViewHolder(
					convertView.findViewById(R.id.iv_app_logo), convertView.findViewById(R.id.tv_app_name),
					convertView.findViewById(R.id.btn_start_app));
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final AppListItem item = mList.get(position);

		vh.AppLogo.setDefaultImageResId(R.drawable.ic_launcher);
		vh.AppLogo.setImageUrl(item.getLogoUrl(), TaskHelper.getImageLoader());
		vh.AppName.setText(item.getName());
		refreshExternalAppButtonStatus(vh.AppStart, item);
		vh.AppStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BusProvider.getBus().post(new LinkToExternalAppEvent(item));
			}
		});
		return convertView;
	}

	/**
	 * Update the status of buttons that can open store linking to the external _app or directly on the _app.
	 *
	 * @param appOpen
	 * 		The button for the app, open, install, or buy.
	 * @param app
	 * 		The data-set represent an external app.
	 */
	private void refreshExternalAppButtonStatus(final Button appOpen, final AppListItem app) {
		if (Utils.isAppInstalled(app.getPackageName(), mPackageManager)) {
			appOpen.setText(R.string.extapp_open);
			appOpen.setTextColor(mInsTxtClr);
			appOpen.setBackgroundResource(R.drawable.selector_intstalled_app_item_btn_color);
		} else {
			appOpen.setText(app.getFree() ? R.string.extapp_download : R.string.extapp_buy);
			appOpen.setTextColor(mUninsTxtClr);
			appOpen.setBackgroundResource(R.drawable.selector_not_intstalled_app_item_btn_color);
		}
	}

	private static class ViewHolder {
		NetworkImageView AppLogo;
		TextView AppName;
		Button AppStart;

		private ViewHolder(  View _appLogo, View _appName, View _appStart) {
			AppLogo = (NetworkImageView) _appLogo;
			AppName = (TextView) _appName;
			AppStart = (Button) _appStart;
		}
	}
}
