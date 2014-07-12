package com.hybrid.app;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hybrid.app.views.OneDirectionSwipeRefreshLayout;

/**
 * Main activity that holds a webview to load a social application.
 * 
 * @author Android Studio
 */
public class MainActivity extends ActionBarActivity implements OneDirectionSwipeRefreshLayout.OnRefreshListener {
	private static final int LAYOUT = R.layout.activity_main;

	/**
	 * WebView that contains social-app.
	 */
	private WebView mWebView;
	/**
	 * Pull-2-load.
	 */
	private OneDirectionSwipeRefreshLayout mRefreshLayout;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(LAYOUT);
		initWebView();
		initRefreshLayout();

	}

	/**
	 * Initialize Pull-2-Load.
	 */
	private void initRefreshLayout() {
		mRefreshLayout = (OneDirectionSwipeRefreshLayout) findViewById(R.id.refresh_layout);
		mRefreshLayout.setOnRefreshListener(this);
		mRefreshLayout.setColorScheme(R.color.refresh_color_1, R.color.refresh_color_2, R.color.refresh_color_3,
				R.color.refresh_color_4);

		/*Get ActionBar's height.*/
		int actionBarHeight;
		int[] abSzAttr;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			abSzAttr = new int[] { android.R.attr.actionBarSize };
		} else {
			abSzAttr = new int[] { R.attr.actionBarSize };
		}
		TypedArray a = obtainStyledAttributes(abSzAttr);
		actionBarHeight = a.getDimensionPixelSize(0, -1);
		mRefreshLayout.setTopMargin(actionBarHeight);

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
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mRefreshLayout.setRefreshing(false);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
		mWebView.loadUrl(getString(R.string.url));
	}

	@Override
	public void onRefresh() {
		mWebView.reload();
	}

	@Override
	public void onProgress(float _progress) {

	}

	@Override
	public void onReturnedToTop() {

	}
}
