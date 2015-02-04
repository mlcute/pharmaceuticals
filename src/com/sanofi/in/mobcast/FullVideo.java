package com.sanofi.in.mobcast;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager.LayoutParams;
import android.widget.MediaController;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class FullVideo extends Activity {

	VideoView vid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullvideo);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		vid = (VideoView) findViewById(R.id.vvTfull);
		String image = getIntent().getStringExtra("name");
		String root = Environment.getExternalStorageDirectory().toString()
				+ Constants.APP_FOLDER_VIDEO + image;
		vid.setVideoPath(root);
		vid.setMediaController(new MediaController(this));
		vid.requestFocus();
		vid.start();
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
