package com.sanofi.in.mobcast;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class HRandTraining extends Activity implements OnClickListener,
		OnPreparedListener, OnTouchListener {

	Button share;
	TextView content, duration, detail, title;
	VideoView vid;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.training);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		title = (TextView) findViewById(R.id.tvTtitle);
		detail = (TextView) findViewById(R.id.tvTtime);
		content = (TextView) findViewById(R.id.tvTsummary);
		duration = (TextView) findViewById(R.id.tvDuration);
		vid = (VideoView) findViewById(R.id.vvTraining);
		// share = (Button) findViewById(R.id.bVshare);
		onNewIntent(getIntent());

		String roo1t = Environment.getExternalStorageDirectory().toString()
				+ "/.mobcast/mobcast_videos/" + name;
		Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(roo1t,
				MediaStore.Images.Thumbnails.MINI_KIND);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
		vid.setBackgroundDrawable(bitmapDrawable);
		vid.setVideoPath(roo1t);
		vid.setMediaController(new MediaController(this));
		vid.requestFocus();
		vid.setOnTouchListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	protected void shareIt() {
		// TODO Auto-generated method stub
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = content.getText().toString();
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		int dur = vid.getDuration();
		duration.setText(dur);
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		Intent video = new Intent(this, FullVideo.class);
		video.putExtra("name", name);
		startActivity(video);
		return false;

	}

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/.mobcast/mobcast_videos");
		// myDir.mkdirs();
		String fname = name;
		File file = new File(myDir, fname);

		title.setText(Dtitle);
		detail.setText(Ddetail);
		content.setText(Dsummary);

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
