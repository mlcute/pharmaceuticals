package com.sanofi.in.mobcast;

import com.sanofi.in.mobcast.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PDFWebViewer extends Activity {
	WebView mWebView;
	private static final String TAG = PDFWebViewer.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		mWebView = (WebView) findViewById(R.id.webview_wv);
		mWebView.getSettings().setJavaScriptEnabled(true);
//		mWebView.setWebViewClient(new HelloWebViewClient());
		mWebView.loadUrl("https://docs.google.com/viewer?url="+getIntent().getStringExtra("DOC"));
	}

	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			String googleDocs = "https://docs.google.com/viewer?url=";

			if (url.endsWith(".pdf")) {
				// Load "url" in google docs
				Log.i(TAG, url);
				view.loadUrl(googleDocs + url);
			} else {
				// Load all other urls normally.
				view.loadUrl(url);
			}

			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}