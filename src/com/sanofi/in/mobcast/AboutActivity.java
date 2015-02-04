package com.sanofi.in.mobcast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.TextView;

public class AboutActivity extends Activity {

	private TextView about;
	private WebView mWebView;
	private WebView mWebView1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		initUi();
		setUiListener();
		setWebData();
	}
	
	
	private void initUi(){
		about = (TextView)findViewById(R.id.about_text);
		mWebView = (WebView)findViewById(R.id.textdisc);
		
		mWebView.setBackgroundColor(0x00000000);
	}
	
	private void setWebData(){
		String text= "Sanofi India Mobcast is our very own engagement and communication app that connects all of us across the country.</br></br>You will get news and information about the company and the industry as it happens wherever you are, no matter how far you are, you will always stay connected.";
		String htmlText = "<html><body style=\"text-align:justify\"><font size=\"4\"> %s </font></body></Html>";
		mWebView.loadData(String.format(htmlText, text), "text/html", "utf-8");
	}
	private void setUiListener(){
		
		about.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String url ="http://www.mobcast.in";
				Intent mIntent = new Intent(Intent.ACTION_VIEW);
				mIntent.setData(Uri.parse(url));
				startActivity(mIntent);
			}
		});
		
	}

	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
