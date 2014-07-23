package com.hybrid.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.chopping.bus.ApplicationConfigurationDownloadedEvent;
import com.chopping.bus.BusProvider;
import com.chopping.exceptions.CanNotOpenOrFindAppPropertiesException;
import com.chopping.exceptions.InvalidAppPropertiesException;
import com.chopping.net.GsonRequestTask;
import com.hybrid.app.adapters.AppListAdapter;
import com.hybrid.app.application.Prefs;
import com.hybrid.app.bus.ExternalAppChangedEvent;
import com.hybrid.app.bus.LinkToExternalAppEvent;
import com.hybrid.app.data.AppList;
import com.hybrid.app.data.AppListItem;
import com.hybrid.app.utils.Utils;
import com.hybrid.app.views.OneDirectionSwipeRefreshLayout;
import com.hybrid.app.views.WebViewEx;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.View.inflate;
import static android.view.animation.AnimationUtils.loadAnimation;

/**
 * Main activity that holds a webview to load a social application.
 * 
 * @author Android Studio, Xinyue Zhao
 */
public class MainActivity extends ActionBarActivity implements OneDirectionSwipeRefreshLayout.OnRefreshListener,
		OnClickListener {
	private static final int LAYOUT = R.layout.activity_main;
	public static final int LAYOUT_LIST_HEADER = R.layout.header_app_list;
	/**
	 * WebView that contains social-app.
	 */
	private WebViewEx mWebView;
	/**
	 * Pull-2-load indicator for loading content.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayout;

	/**
	 * Pull-2-load indicator for loading app-list.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayoutAppList;

	/** Use navigation-drawer for this fork. */
	private ActionBarDrawerToggle mDrawerToggle;

	/** Drawer. */
	private DrawerLayout mDrawerLayout;

	/** The Adapter for the ListView showing external apps. */
	private AppListAdapter mListAdapter;

	/** The header of ListView. */
	private View mHeaderListView;

	/**
	 * The divide on the header of ListView. Showing when mRefreshLayoutAppList
	 * has finished.
	 */
	private View mDivHeaderListView;

	/** ListView showing external apps. */
	private ListView mAppListView;

	/**
	 * The height of actionbar, because we use overlay, so that some views
	 * should be seen under it.
	 */
	private int mActionBarHeight;

	/** True if a net-req has been asked and not finished. */
	private boolean mReqInProcess = false;

	/** An alternative to navigate the browser. */
	private View mBrowserNavi;

	/** Url to the Web-App.*/
	private String mUrlWebApp;

	/** Url to the App-List.*/
	private String mUrlAppList;

	/** Container for title(app list)*/
	private View mAppListTitleLL;


	// ------------------------------------------------
	// Subscribes, event-handlers
	// ------------------------------------------------
	@Subscribe
	public void onApplicationConfigurationDownloaded(ApplicationConfigurationDownloadedEvent _e){
		loadWebApp();
		loadAppList();
	}


	@Subscribe
	public void onVolleyError(VolleyError _e) {
		Utils.showLongToast(this, R.string.err_net_can_load_ext_app);
		onFinishLoadedAppList();
		mAppListTitleLL.setVisibility(GONE);
	}

	@Subscribe
	public void onAppListLoaded(AppList _e) {
		showAppList(_e);
		onFinishLoadedAppList();
		mAppListTitleLL.setVisibility(VISIBLE);
	}

	@Subscribe
	public void onExternalAppChanged(ExternalAppChangedEvent _e) {
		if (mListAdapter != null) {
			mListAdapter.notifyDataSetChanged();
		}
	}

	@Subscribe
	public void onLinkToExternalApp(LinkToExternalAppEvent _e) {
		Utils.linkToExternalApp(this, _e.getAppListItem());
		mDrawerLayout.closeDrawers();
	}

	// ------------------------------------------------

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
	 *            The data source(a sort of apps).
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

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String mightError = null;
		try {
			Prefs.getInstance().downloadApplicationConfiguration();
			setContentView(LAYOUT);
			initActionBar();
			initRefreshLayout();
			initWebView();
			initExtAppListView();
			initNaviButtons();
		} catch (InvalidAppPropertiesException _e) {
			mightError = _e.getMessage();
		} catch (CanNotOpenOrFindAppPropertiesException _e) {
			mightError = _e.getMessage();
		}
		if(mightError != null) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.app_name)
					.setMessage(mightError)
					.setCancelable(false)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create().show();
		}
	}



	/**
	 * Initialize button group for navigation of the webview.
	 */
	private void initNaviButtons() {
		mBrowserNavi = findViewById(R.id.browser_navi_buttons);
		mBrowserNavi.findViewById(R.id.btn_backward).setOnClickListener(this);
		mBrowserNavi.findViewById(R.id.btn_top).setOnClickListener(this);
		mBrowserNavi.findViewById(R.id.btn_forward).setOnClickListener(this);
	}

	/**
	 * Dismiss the ActionBar, also the navi-buttons will be dismissed.
	 */
	private void hideActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (actionBar.isShowing()) {
			actionBar.hide();
			mRefreshLayout.setTopMargin(0);
			mRefreshLayout.requestLayout();
		}

		if(mBrowserNavi.getVisibility() == VISIBLE) {
			mBrowserNavi.setAnimation(loadAnimation(getApplicationContext(), R.anim.slide_out_to_right));
			mBrowserNavi.setVisibility(INVISIBLE);
		}
	}

	/**
	 * Show the ActionBar, also the navi-buttons will be shown.
	 */
	private void showActionBar() {
		ActionBar actionBar = getSupportActionBar();
		if (!actionBar.isShowing()) {
			actionBar.show();
			mRefreshLayout.setTopMargin(mActionBarHeight);
			mRefreshLayout.requestLayout();
		}
		if(mBrowserNavi.getVisibility() != VISIBLE) {
			mBrowserNavi.setAnimation(loadAnimation(getApplicationContext(), R.anim.slide_in_from_right));
			mBrowserNavi.setVisibility(VISIBLE);
		}
	}

	/**
	 * Initialize the ListView showing external apps.
	 */
	private void initExtAppListView() {
		mAppListView = (ListView) findViewById(R.id.lv_app_list);
		mListAdapter = new AppListAdapter(this);
		mAppListView.setAdapter(mListAdapter);
		((ViewGroup.MarginLayoutParams) mAppListView.getLayoutParams()).topMargin = mActionBarHeight;
		mHeaderListView = inflate(this, LAYOUT_LIST_HEADER, null);
		/* Title and its progress indicator. They should be dismissed when error comes after loading external apps.*/
		mAppListTitleLL = mHeaderListView.findViewById(R.id.app_list_title_ll);
		mRefreshLayoutAppList = (OneDirectionSwipeRefreshLayout) mHeaderListView
				.findViewById(R.id.refresh_app_list_layout);
		mRefreshLayoutAppList.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3,
				R.color.refresh_color_4);
		/*
		 * Show indicator loading app-list, dismiss divide in header(default in
		 * xml).
		 */
		mRefreshLayoutAppList.setRefreshing(true);
		mDivHeaderListView = mHeaderListView.findViewById(R.id.div_header);
		mAppListView.addHeaderView(mHeaderListView, null, false);
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
					R.string.app_name) {
				@Override
				public void onDrawerOpened(View drawerView) {
					super.onDrawerOpened(drawerView);

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

		getSupportActionBar().setTitle(getString(R.string.action_bar_title));
	}

	/**
	 * Initialize webview.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebViewEx) findViewById(R.id.fullscreen_content);
		mWebView.setOnWebViewExScrolledListener(new WebViewEx.OnWebViewExScrolledListener() {
			@Override
			public void onScrollChanged(boolean isUp) {
				if (isUp) {
					hideActionBar();
				} else {
					showActionBar();
				}
			}
		});

		mWebView.setOnWebViewExScrolledTopListener(new WebViewEx.OnWebViewExScrolledTopListener() {
			@Override
			public void onScrolledTop() {
				hideActionBar();
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				mRefreshLayout.setRefreshing(true);
				getSupportActionBar().setTitle(R.string.loading);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mRefreshLayout.setRefreshing(false);
				getSupportActionBar().setTitle(getString(R.string.action_bar_title));
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
		settings.setBuiltInZoomControls(false);
		mRefreshLayout.setRefreshing(true);
	}

	@Override

	protected void onResume() {
		BusProvider.getBus().register(this);
		super.onResume();
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
		if(Prefs.getInstance().canAppLive()) {
			mRefreshLayout.setRefreshing(true);
			reloadWebApp();
			/* Should update external app list, some apps might have been removed. */
			if (mListAdapter != null) {
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Load list of apps.
	 */
	private void loadAppList() {
		mUrlAppList = Prefs.getInstance().getAppListUrl();
		if(!TextUtils.isEmpty(mUrlAppList)) {
			new GsonRequestTask<AppList>(getApplicationContext(), Request.Method.GET, mUrlAppList,
					AppList.class).execute();
			mReqInProcess = true;
		}
	}

	/**
	 * Load web-app.
	 */
	private void loadWebApp() {
		mUrlWebApp = Prefs.getInstance().getWebAppUrl();
		if(mWebView != null && !TextUtils.isEmpty(mUrlWebApp)) {
			mWebView.loadUrl(mUrlWebApp);
		}
	}

	/**
	 * Load web-app.
	 * Ignore if app's config has not provided url to the web-app.
	 */
	private void reloadWebApp() {
		if(!TextUtils.isEmpty(mUrlWebApp)) {
			mWebView.reload();
		} else {
			loadWebApp();
		}
	}

	@Override
	protected void onPause() {
		BusProvider.getBus().unregister(this);
		super.onPause();
	}

	@Override
	public void onRefresh() {
		reloadWebApp();
	}

	@Override
	public void onProgress(float progress) {

	}

	@Override
	public void onReturnedToTop() {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem menuShare = menu.findItem(R.id.menu_share);
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


	/**
	 * Go forward on webview.
	 */
	private void forward() {
		if (mWebView.canGoForward()) {
			mWebView.goForward();
		}
	}

	/**
	 * Go top on webview.
	 */
	private void top() {
		mWebView.startAnimation( loadAnimation(getApplicationContext(), R.anim.abc_fade_in));
		mWebView.scrollTo(0, 0);
	}

	/**
	 * Go backward on webview.
	 */
	private void backward() {
		if (mWebView.canGoBack()) {
			mWebView.goBack();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_forward:
			forward();
			break;
		case R.id.btn_top:
			top();
			break;
		case R.id.btn_backward:
			backward();
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_forward:
				forward();
				break;
			case R.id.menu_top:
				top();
				break;
			case R.id.menu_backward:
				backward();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
