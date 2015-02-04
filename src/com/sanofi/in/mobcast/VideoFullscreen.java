package com.sanofi.in.mobcast;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class VideoFullscreen extends Activity {

	/**
	 * @param args
	 */

	String name;
	int StartTime;
	VideoView videoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_fullscreen);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		videoView = (VideoView) findViewById(R.id.VideoView);
		// onNewIntent(getIntent());
		name = getIntent().getStringExtra("name");
		StartTime=getIntent().getIntExtra("StartAt", 0);
		videoView = (VideoView) findViewById(R.id.VideoView);
		Log.d("StartAt",""+StartTime);
		Log.d("name", name);
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/.mobcast/mobcast_videos");
		// myDir.mkdirs();
		String fname = name;
		File file = new File(myDir, fname);
		String roo1t = Environment.getExternalStorageDirectory().toString()
				+ "/.mobcast/mobcast_videos/" + name;
		Log.d("path", roo1t);
		MediaController mMedia = new MediaController(this);
		mMedia.setMediaPlayer(videoView);
		mMedia.setAnchorView(videoView);
		videoView.setMediaController(mMedia);
		videoView.setVideoPath(roo1t);
		Log.d("StartAt",""+StartTime);
		videoView.seekTo(StartTime);
		videoView.start();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		prefs.edit().putInt("StartAt", videoView.getCurrentPosition()).commit();
	}
	int lastOrientation = 0;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
	}
	@Override
	public void onNewIntent(Intent intent) {
		// vid.start();
	}
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

}
