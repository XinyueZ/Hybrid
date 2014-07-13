package com.hybrid.app;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.hybrid.app.adapters.AppListAdapter;
import com.hybrid.app.bus.BusProvider;
import com.hybrid.app.data.AppList;
import com.hybrid.app.data.AppListItem;
import com.hybrid.app.net.GsonRequestTask;
import com.hybrid.app.utils.Utils;
import com.hybrid.app.views.OneDirectionSwipeRefreshLayout;
import com.squareup.otto.Subscribe;

/**
 * Main activity that holds a webview to load a social application.
 * 
 * @author Android Studio
 */
public class MainActivity extends ActionBarActivity implements OneDirectionSwipeRefreshLayout.OnRefreshListener {
	private static final int LAYOUT = R.layout.activity_main;
	/** A list which provides all available hybrid apps. */
	private static final String URL_APP_LIST = "https://dl.dropboxusercontent.com/s/y2dwthhcdu3p1g8/hybrid_apps.json";
	/**
	 * WebView that contains social-app.
	 */
	private WebView mWebView;
	/**
	 * Pull-2-load.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayout;

	/** Use navigation-drawer for this fork. */
	private ActionBarDrawerToggle mDrawerToggle;

	/** Drawer. */
	private DrawerLayout mDrawerLayout;

	/** The Adapter for the ListView showing external apps. */
	private AppListAdapter mListAdapter;

	/** The header of ListView.*/
	private TextView mHeaderListView;

	/** ListView showing external apps. */
	private ListView mAppListView;

	/**
	 * The height of actionbar, because we use overlay, so that some views
	 * should be seen under it.
	 */
	private int mActionBarHeight;

	// ------------------------------------------------
	// Subscribes, event-handlers
	// ------------------------------------------------

	@Subscribe
	public void onAppListLoaded(AppList _e) {
		showAppList(_e);
	}

	// ------------------------------------------------

	/**
	 * Show app list onto the ListView.
	 * 
	 * @param _e
	 *            The data source(a sort of apps).
	 */
	private void showAppList(AppList _e) {
		AppListItem[] apps = _e.getItems();
		/* It should filter itself. */
		int i = 0;
		String packageName = getPackageName();
		AppListItem[] appsFiltered = new AppListItem[apps.length - 1];
		for (AppListItem app : apps) {
			if (TextUtils.equals(packageName, app.getPackageName())) {
				continue;
			}
			appsFiltered[i] = app;
			i++;
		}
		if (mListAdapter == null) {
			mListAdapter = new AppListAdapter(this, appsFiltered);
			mHeaderListView = new TextView(getApplicationContext());
			Resources resources = getResources();
			int padding = (int) resources.getDimension(R.dimen.padding_item_external_app);
			mHeaderListView.setPadding(padding,padding, padding, padding);
			mHeaderListView.setTextSize(resources.getDimension(R.dimen.extapp_title));
			mHeaderListView.setTextColor(resources.getColor(R.color.text_general));
			mHeaderListView.setText(R.string.extapp_title);
			mAppListView.addHeaderView(mHeaderListView, null, false);
			mAppListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.setList(appsFiltered);
			mListAdapter.notifyDataSetChanged();
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		initActionBar();
		initRefreshLayout();
		initWebView();
		initExtAppListView();
		new GsonRequestTask<AppList>(getApplicationContext(), Request.Method.GET, URL_APP_LIST, AppList.class)
				.execute();
	}

	/**
	 * Initialize the ListView showing external apps.
	 */
	private void initExtAppListView() {
		mAppListView = (ListView) findViewById(R.id.lv_app_list);
		((ViewGroup.MarginLayoutParams) mAppListView.getLayoutParams()).topMargin = mActionBarHeight;
	}

	/**
	 * Initialize ActionBar and Navigation-Drawer.
	 */
	private void initActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setHomeButtonEnabled(true);
			actionBar.setDisplayHomeAsUpEnabled(true);
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
					R.string.app_name);
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}
	}

	/**
	 * Initialize Pull-2-Load.
	 */
	private void initRefreshLayout() {
		mRefreshLayout = (OneDirectionSwipeRefreshLayout) findViewById(R.id.refresh_layout);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3,
				R.color.refresh_color_4);

		/* Get ActionBar's height. */
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		mActionBarHeight = a.getDimensionPixelSize(0, -1);
		mRefreshLayout.setTopMargin(mActionBarHeight);

		getSupportActionBar().setTitle("");
	}

	/**
	 * Initialize webview.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebView) findViewById(R.id.fullscreen_content);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				mRefreshLayout.setRefreshing(true);
				getSupportActionBar().setTitle(R.string.loading);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mRefreshLayout.setRefreshing(false);
				getSupportActionBar().setTitle("");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mRefreshLayout.setRefreshing(true);
				view.loadUrl(url);
				return true;
			}
		});
		WebSettings settings = mWebView.getSettings();
		settings.setLoadWithOverviewMode(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setCacheMode(WebSettings.LOAD_NORMAL);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		mRefreshLayout.setRefreshing(true);
		mWebView.loadUrl(getString(R.string.url));
	}

	@Override
	public void onBackPressed() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		BusProvider.getBus().register(this);
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
		mRefreshLayout.setRefreshing(true);
		mWebView.reload();
	}

	@Override
	protected void onPause() {
		BusProvider.getBus().unregister(this);
		super.onPause();
	}

	@Override
	public void onRefresh() {
		mWebView.reload();
	}

	@Override
	public void onProgress(float progress) {

	}

	@Override
	public void onReturnedToTop() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu _menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, _menu);
		MenuItem menuShare = _menu.findItem(R.id.menu_share);
		/*
		 * Getting the actionprovider associated with the menu item whose id is
		 * share
		 */
		android.support.v7.widget.ShareActionProvider provider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat
				.getActionProvider(menuShare);

		/* Setting a share intent */
		String packageName = getPackageName();
		String appName = getString(R.string.app_name);
		String subject = String.format(getString(R.string.sharing_title), appName);
		String text = String.format(getString(R.string.sharing_this_app), appName, packageName);
		provider.setShareIntent(Utils.getDefaultShareIntent(provider, subject, text));

		return true;
	}
}
