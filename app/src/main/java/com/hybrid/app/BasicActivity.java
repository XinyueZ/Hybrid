package com.hybrid.app;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;

import com.android.volley.Request;
import com.chopping.activities.BaseActivity;
import com.chopping.net.GsonRequestTask;
import com.hybrid.app.adapters.AppListAdapter;
import com.hybrid.app.application.Prefs;
import com.hybrid.app.bus.ExternalAppChangedEvent;
import com.hybrid.app.bus.LinkToExternalAppEvent;
import com.hybrid.app.data.AppList;
import com.hybrid.app.data.AppListItem;
import com.hybrid.app.utils.Utils;
import com.hybrid.app.views.OneDirectionSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

/**
 * Base class for all activities that need drawer-layout, ActionBar and handling Home-Up etc.
 */
public abstract class BasicActivity extends BaseActivity implements
		OneDirectionSwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
	/**
	 * Basic layout for whole App to hold a navigation-drawer.
	 */
	private static final int NAVI_DRAWER_LAYOUT = R.layout.activity_basic;
	/**
	 * ListView-header on drawer.
	 */
	private static final int LAYOUT_LIST_HEADER = R.layout.header_app_list;
	/**
	 * Use navigation-drawer for this fork.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	/**
	 * Drawer.
	 */
	private DrawerLayout mDrawerLayout;

	/**
	 * Content of every activity.
	 */
	private View mActivityContent;

	/**
	 * The Adapter for the ListView showing external apps.
	 */
	private AppListAdapter mListAdapter;


	/**
	 * The divide on the header of ListView. Showing when mRefreshLayoutAppList has finished.
	 */
	private View mDivHeaderListView;

	/**
	 * ListView showing external apps.
	 */
	private ListView mAppListView;


	/**
	 * The height of actionbar, because we use overlay, so that some views should be seen under it.
	 */
	private int mActionBarHeight;

	/**
	 * True if a net-req has been asked and not finished.
	 */
	private boolean mReqInProcess = false;

	/**
	 * Container for title(app list)
	 */
	private View mAppListTitleLL;

	/**
	 * Pull-2-load indicator for loading content.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayout;


	/**
	 * Pull-2-load indicator for loading app-list.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayoutAppList;

	//------------------------------------------------
	//Subscribes, event-handlers
	//------------------------------------------------

	/**
	 * Event, show app-list when they have been loaded.
	 *
	 * @param _e
	 * 		{@link com.hybrid.app.data.AppList}.
	 */
	public void onEvent(AppList _e) {
		showAppList(_e);
		onFinishLoadedAppList();
		mAppListTitleLL.setVisibility(VISIBLE);
	}

	/**
	 * Event, update list of external apps.
	 *
	 * @param _e
	 * 		{@link com.hybrid.app.bus.ExternalAppChangedEvent}.
	 */
	public void onEvent(ExternalAppChangedEvent _e) {
		if (mListAdapter != null) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Event, open an external app that has been installed.
	 *
	 * @param _e
	 * 		{@link com.hybrid.app.bus.LinkToExternalAppEvent}.
	 */
	public void onEvent(LinkToExternalAppEvent _e) {
		Utils.linkToExternalApp(this, _e.getAppListItem());
		mDrawerLayout.closeDrawers();
	}


	//------------------------------------------------

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(NAVI_DRAWER_LAYOUT);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		final ViewGroup drawerContent = (ViewGroup) findViewById(R.id.refresh_draw_content_layout);
		mActivityContent = getLayoutInflater().inflate(layoutResID, null);
		drawerContent.addView(mActivityContent, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		initActionBar();
		initRefreshLayout();
		initExtAppListView();
	}


	@Override
	protected void onResume() {
		super.onResume();

		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
		if (Prefs.getInstance().canAppLive()) {
			/* Should update external app list, some apps might have been removed. */
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

	protected void showProgressIndicator(boolean show) {
		mRefreshLayout.setRefreshing(show);
	}


	@Override
	protected void onAppConfigLoaded() {
		super.onAppConfigLoaded();
		loadAppList();
	}

	@Override
	protected void onNetworkError() {
		onFinishLoadedAppList();
		mAppListTitleLL.setVisibility(GONE);
		super.onNetworkError();
	}


	/**
	 * Load list of apps.
	 */
	private void loadAppList() {
		final String urlAppList = Prefs.getInstance().getAppListUrl();
		if (!TextUtils.isEmpty(urlAppList)) {
			new GsonRequestTask<AppList>(getApplicationContext(), Request.Method.GET, urlAppList,
					AppList.class).execute();
			mReqInProcess = true;
		}
	}


	/**
	 * Initialize ActionBar and Navigation-Drawer.
	 */
	private void initActionBar() {
		/* Get ActionBar's height. */
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[]{android.R.attr.actionBarSize};
		} else {
			abSzAttr = new int[]{R.attr.actionBarSize};
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);


		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
					R.string.app_name) {
				@Override
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);
					showActionBar();
					if (!mReqInProcess) {

						/*
						 * Show indicator loading app-list, dismiss divide in
						 * header.
						 */
						if (mRefreshLayoutAppList != null) {
							mRefreshLayoutAppList.setRefreshing(true);
							mRefreshLayoutAppList.setVisibility(VISIBLE);
						}
						if (mDivHeaderListView != null) {
							mDivHeaderListView.setVisibility(GONE);
						}

						loadAppList();
					}
				}

				@Override
				public void onDrawerSlide(View drawerView, float slideOffset) {
					showActionBar();
					super.onDrawerSlide(drawerView, slideOffset);
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}
	}


	/**
	 * Initialize Pull-2-Load.
	 */
	private void initRefreshLayout() {
		mRefreshLayout = (OneDirectionSwipeRefreshLayout) findViewById(R.id.refresh_draw_content_layout);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3,
				R.color.refresh_color_4);
		mRefreshLayout.setTopMargin(mActionBarHeight);
		getSupportActionBar().setTitle(getString(R.string.action_bar_title));
	}

	/**
	 * Initialize the ListView showing external apps.
	 */
	private void initExtAppListView() {
		mAppListView = (ListView) findViewById(R.id.lv_app_list);

		((ViewGroup.MarginLayoutParams) mAppListView.getLayoutParams()).topMargin = mActionBarHeight;

		final View headerListView = inflate(this, LAYOUT_LIST_HEADER, null);
		/* Title and its progress indicator. They should be dismissed when error comes after loading external apps.*/
		mAppListTitleLL = headerListView.findViewById(R.id.app_list_title_ll);
		headerListView.findViewById(R.id.drawer_menu_settings).setOnClickListener(this);
		mRefreshLayoutAppList = (OneDirectionSwipeRefreshLayout) headerListView
				.findViewById(R.id.refresh_app_list_layout);
		mRefreshLayoutAppList.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3,
				R.color.refresh_color_4);
		/*
		 * Show indicator loading app-list, dismiss divide in header(default in
		 * xml).
		 */
		mRefreshLayoutAppList.setRefreshing(true);
		mDivHeaderListView = headerListView.findViewById(R.id.div_header);
		mAppListView.addHeaderView(headerListView, null, false);

		mListAdapter = new AppListAdapter(this);
		mAppListView.setAdapter(mListAdapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.drawer_menu_settings:
				mDrawerLayout.closeDrawers();
				SettingsActivity.showInstance(this);
				break;
		}
	}

	/**
	 * Show the ActionBar, also the navi-buttons will be shown.
	 */
	protected void showActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (!actionBar.isShowing()) {
			actionBar.show();
			mRefreshLayout.setTopMargin(mActionBarHeight);
			mRefreshLayout.requestLayout();
		}
	}

	/**
	 * Dismiss the ActionBar, also the navi-buttons will be dismissed.
	 */
	protected void hideActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar.isShowing()) {
			actionBar.hide();
			mRefreshLayout.setTopMargin(0);
			mRefreshLayout.requestLayout();
		}
	}

	/**
	 * Event after loaded external app-list, either success or not.
	 */
	private void onFinishLoadedAppList() {
		mReqInProcess = false;
		/* Dismiss indicator loading app-list, show divide in header. */
		if (mRefreshLayoutAppList != null) {
			mRefreshLayoutAppList.setRefreshing(false);
			mRefreshLayoutAppList.setVisibility(GONE);
		}
		if (mDivHeaderListView != null) {
			mDivHeaderListView.setVisibility(VISIBLE);
		}
	}


	/**
	 * Show app list onto the ListView.
	 *
	 * @param _e
	 * 		The data source(a sort of apps).
	 */
	private void showAppList(AppList _e) {
		AppListItem[] apps = _e.getItems();
		/* It should filter itself. */
		String packageName = getPackageName();
		List<AppListItem> appsFiltered = new ArrayList<AppListItem>();
		for (AppListItem app : apps) {
			if (TextUtils.equals(packageName, app.getPackageName())) {
				continue;
			}
			appsFiltered.add(app);
		}
		if (mListAdapter == null) {
			mListAdapter = new AppListAdapter(this, appsFiltered);
			mAppListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.setList(appsFiltered);
			mListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Get height of actionbar, because we use overlay, so that some views should be seen under it.
	 */
	protected int getActionBarHeight() {
		return mActionBarHeight;
	}


	@Override
	public void onRefresh() {

	}

	@Override
	public void onProgress(float progress) {

	}

	@Override
	public void onReturnedToTop() {

	}

}
