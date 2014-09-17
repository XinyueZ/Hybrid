package com.hybrid.app;

import static android.view.View.INVISIBLE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.animation.AnimationUtils.loadAnimation;

import com.chopping.application.BasicPrefs;
import com.crashlytics.android.Crashlytics;
import com.hybrid.app.application.Prefs;
import com.hybrid.app.utils.Utils;
import com.hybrid.app.views.WebViewEx;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Main activity that holds a webview to load a social application.
 *
 * @author Android Studio, Xinyue Zhao
 */
public class MainActivity extends BasicActivity implements OnClickListener, WebViewEx.OnWebViewExScrolledListener {
	private static final int LAYOUT = R.layout.activity_main;
	/**
	 * WebView that contains social-app.
	 */
	private WebViewEx mWebView;

	/**
	 * An alternative to navigate the browser.
	 */
	private View mBrowserNavi;

	/**
	 * Url to the Web-App.
	 */
	private String mUrlWebApp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(LAYOUT);
		initWebView();
		initNaviButtons();
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
	@Override
	protected void hideActionBar() {
		super.hideActionBar();
		if (mBrowserNavi.getVisibility() == VISIBLE) {
			mBrowserNavi.setAnimation(loadAnimation(getApplicationContext(), R.anim.slide_out_to_right));
			mBrowserNavi.setVisibility(INVISIBLE);
		}
	}

	/**
	 * Show the ActionBar, also the navi-buttons will be shown.
	 */
	@Override
	protected void showActionBar() {
		super.showActionBar();
		if (mBrowserNavi.getVisibility() != VISIBLE && Prefs.getInstance().isNaviBarForBrowserVisible()) {
			mBrowserNavi.setAnimation(loadAnimation(getApplicationContext(), R.anim.slide_in_from_right));
			mBrowserNavi.setVisibility(VISIBLE);
		}
	}

	@Override
	protected void onAppConfigLoaded() {
		loadWebApp();
		super.onAppConfigLoaded();
	}


	/**
	 * Initialize webview.
	 */
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		mWebView = (WebViewEx) findViewById(R.id.fullscreen_content);
		mWebView.setOnWebViewExScrolledListener(this);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
				showProgressIndicator(true);
				getSupportActionBar().setTitle(R.string.loading);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				showProgressIndicator(false);
				getSupportActionBar().setTitle(getString(R.string.action_bar_title));
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showProgressIndicator(true);
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
		settings.setDomStorageEnabled(true);
		showProgressIndicator(true);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (Prefs.getInstance().canAppLive()) {
			/*
			 *Navi for browser could be hidden by setting.
			 */
			if (mBrowserNavi.getVisibility() == VISIBLE && !Prefs.getInstance().isNaviBarForBrowserVisible()) {
				mBrowserNavi.setAnimation(loadAnimation(getApplicationContext(), R.anim.slide_out_to_right));
				mBrowserNavi.setVisibility(INVISIBLE);
			}
		}
	}

	@Override
	protected BasicPrefs getPrefs() {
		return Prefs.getInstance();
	}


	/**
	 * Load web-app.
	 */
	private void loadWebApp() {
		mUrlWebApp = Prefs.getInstance().getWebAppUrl();
		if (mWebView != null && !TextUtils.isEmpty(mUrlWebApp)) {
			mWebView.loadUrl(mUrlWebApp);
		}
	}

	/**
	 * Load web-app. Ignore if app's config has not provided url to the web-app.
	 */
	private void reloadWebApp() {
		if (!TextUtils.isEmpty(mUrlWebApp)) {
			mWebView.reload();
		} else {
			loadWebApp();
		}
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
		android.support.v7.widget.ShareActionProvider provider =
				(android.support.v7.widget.ShareActionProvider) MenuItemCompat
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
		mWebView.startAnimation(loadAnimation(getApplicationContext(), R.anim.abc_fade_in));
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
		super.onClick(v);
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

	@Override
	public void onScrollChanged(boolean isUp) {
		if (isUp) {
			onScrolledTop();
		} else {
			showActionBar();
		}
	}

	@Override
	public void onScrolledTop() {
		if (Prefs.getInstance().isActionBarForScrollingUpVisible()) {
			if (!getSupportActionBar().isShowing()) {
				getSupportActionBar().show();
			}
		} else {
			hideActionBar();
		}
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
}
