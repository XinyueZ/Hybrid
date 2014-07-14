package com.hybrid.app.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebView;

/**
 * An extension of standard WebView that we can detect which direction user
 * scrolled.
 * 
 * @author Xinyue Zhao
 */
public final class WebViewEx extends WebView {
	/** A listener hooks the WebView when it scrolled. */
	private OnWebViewExScrolledListener mOnWebViewExScrolledListener;
	/** A listener hooks the WebView when it scrolled on TOP. */
	private OnWebViewExScrolledTopListener mOnWebViewExScrolledTopListener;

	public WebViewEx(Context context) {
		super(context);
	}

	public WebViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		Log.d("Hybrid", "WebViewEx t:" + t + ",  oldt:" + oldt);
		if (t > 0) {
			if (mOnWebViewExScrolledListener != null) {
				mOnWebViewExScrolledListener.onScrollChanged(t > oldt);
			}
		} else {
			if (t == 0) {
				if (mOnWebViewExScrolledTopListener != null) {
					mOnWebViewExScrolledTopListener.onScrolledTop();
				}
			}
		}
		super.onScrollChanged(l, t, oldl, oldt);
	}

	/**
	 * Set listener hooks the WebView when it scrolled.
	 * 
	 * @param onWebViewExScrolledListener
	 *            The instance of listener.
	 */
	public void setOnWebViewExScrolledListener(OnWebViewExScrolledListener onWebViewExScrolledListener) {
		mOnWebViewExScrolledListener = onWebViewExScrolledListener;
	}

	/**
	 * Set listener hooks the WebView when it scrolled on TOP.
	 * 
	 * @param onWebViewExScrolledTopListener
	 *            The instance of listener.
	 */
	public void setOnWebViewExScrolledTopListener(OnWebViewExScrolledTopListener onWebViewExScrolledTopListener) {
		mOnWebViewExScrolledTopListener = onWebViewExScrolledTopListener;
	}

	/**
	 * A listener hooks the WebView when it scrolled.
	 * 
	 * @author Xinyue Zhao
	 */
	public interface OnWebViewExScrolledListener {
		/**
		 * Event fired when user scrolled the WebView.
		 * 
		 * @param isUp
		 *            True if user scrolled up, false then down.
		 */
		void onScrollChanged(boolean isUp);
	}

	/**
	 * A listener hooks the WebView when it scrolled on TOP.
	 * 
	 * @author Xinyue Zhao
	 */
	public interface OnWebViewExScrolledTopListener {
		/**
		 * Event fired when user scrolled the WebView onto TOP.
		 */
		void onScrolledTop();
	}
}
