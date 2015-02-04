package com.sanofi.in.mobcast;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Download.OnPostExecuteListener;
import com.mobcast.util.Utilities;

@SuppressLint("NewApi")
public class AnnounceVideo extends Activity implements OnClickListener,
		OnPreparedListener {

	TextView title, detail, from, summary, mtitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name, aid;
	int _id;
	Button share;
	ScrollView sv;
	Reports reports;
	com.sanofi.in.mobcast.CustomVideoView vid;
	ImageView vfullscreenplay;
	ImageView btn, play;
	long time = (long) 0, start = (long) 0, stop = (long) 0;
	SharedPreferences prefs;
	MediaController mc;
	private MediaPlayer mMediaPlayer;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	File file;
	int currentPlaybackTime = -1;
	private static final String TAG = AnnounceVideo.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avideo);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		title = (TextView) findViewById(R.id.tvVtitle);
		detail = (TextView) findViewById(R.id.tvVDetail);
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		from = (TextView) findViewById(R.id.tvVFrom);
		reports = new Reports(getApplicationContext(), "Announce");
		summary = (TextView) findViewById(R.id.tvVsummary);
		vid = (com.sanofi.in.mobcast.CustomVideoView) findViewById(R.id.vvType);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		vfullscreenplay = (ImageView) findViewById(R.id.vfullscreenplay);
		share = (Button) findViewById(R.id.iv6);
		vid.setOnPreparedListener(this);
		btn = (ImageView) findViewById(R.id.vbackground);
		play = (ImageView) findViewById(R.id.vbackgroundplay);
		play.setOnClickListener(BackgroundclkListener);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			play.setAlpha(0.7f);
		}
		btn.setOnClickListener(BackgroundclkListener);
		onNewIntent(getIntent());
		try {
			String roo1t = Environment.getExternalStorageDirectory().toString()
					+ Constants.APP_FOLDER_VIDEO + name;
			detail.setText(DateUtils.formatDate(Ddetail));
			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(roo1t,
					MediaStore.Images.Thumbnails.MINI_KIND);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
			vid.setBackgroundDrawable(bitmapDrawable);
			btn.setImageDrawable(bitmapDrawable);
			vid.setVideoPath(roo1t);

		} catch (Exception e) {
			Log.e("error upper", "video file not downloaded");
		}
		// btn.setBackgroundDrawable(bitmapDrawable);
		share.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					// if(android.os.Build.VERSION.SDK_INT>=11){
					// v.setAlpha(0.5f);}
					v.getBackground().setAlpha(45);
				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

					// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
					v.getBackground().setAlpha(255);
				}

				return false;
			}
		});

		vfullscreenplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vid.pause();
				time = vid.getCurrentPosition();
				Log.d("Paused at ", "" + time);
				Intent i = new Intent(AnnounceVideo.this, VideoFullscreen.class);
				i.putExtra("name", name);
				Log.d("name", name);
				i.putExtra("StartAt", (int) time);
				onDestroy();
				startActivity(i);
			}
		});

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("video/mp4");
				String shareBody = Dfrom + "\n ON: " + detail.getText() + "\n"
						+ summary.getText();
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title.getText());
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				shareIntent.putExtra(android.content.Intent.EXTRA_STREAM,
						Uri.fromFile(file));
				reports.updateShare(aid);
				if (vid.isPlaying()) {
					vid.pause();
				}
				currentPlaybackTime = vid.getCurrentPosition();
				startActivity(Intent.createChooser(shareIntent, "Share video"));

			}
		});

		mc = new MediaController(this);
		mc.setAnchorView(vid);
		vid.setMediaController(mc);
		vid.requestFocus();
		// vid.setPlayPauseListener(ppl);
		play.setVisibility(ImageView.VISIBLE);
		title.setOnTouchListener(otl);
		from.setOnTouchListener(otl);
		detail.setOnTouchListener(otl);
		summary.setOnTouchListener(otl);
		sv.setOnTouchListener(otl);
		// sv.setOnTouchListener(otl);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d("Orientation ", "" + newConfig.orientation);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			vid.pause();
			Intent i = new Intent(AnnounceVideo.this, VideoFullscreen.class);
			i.putExtra("name", name);
			i.putExtra("StartAt", (int) time);
			Log.d("StartAt", "" + time);
			Log.d("name", name);
			onDestroy();
			startActivity(i);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Toast.makeText(getApplicationContext(),"16. onDestroy()",
		// Toast.LENGTH_SHORT).show();
		Log.e("time in mills", time + "");
		long seconds = time / 1000;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		long hours = minutes / 60;
		minutes = minutes % 60;
		String timestr = hours + ":" + minutes + ":" + seconds;
		Log.e("time in hh:mm:ss", timestr);
		reports.updateDuration(aid, timestr);
	}

	CustomVideoView.PlayPauseListener ppl = new CustomVideoView.PlayPauseListener() {

		@Override
		public void onPlay() {
			System.out.println("Play!");
			Log.d("Start", "" + start);
			start = System.currentTimeMillis();
			play.setVisibility(ImageView.GONE);
		}

		@Override
		public void onPause() {
			System.out.println("Pause!");
			stop = System.currentTimeMillis();
			Log.d("Stop", "" + stop);
			play.setVisibility(ImageView.VISIBLE);
			time += stop - start;
			// currentTime = (int) stop;
		}
	};

	View.OnClickListener BackgroundclkListener = new View.OnClickListener() {
		public void onClick(View v) {
			int st = 0;
			// btn.setVisibility(ImageView.GONE);
			play.setVisibility(ImageView.GONE);
			vid.setVisibility(com.sanofi.in.mobcast.CustomVideoView.VISIBLE);
			vid.setPlayPauseListener(ppl);
			System.out.println("" + st);
			vid.start();
		}
	};

	protected void onResume() {
		super.onResume();
		int st = 0;
		try {
			st = prefs.getInt("StartAt", 0);
			System.out.println("St------->" + st);
			prefs.edit().remove("StartAt").commit();
		} catch (Exception e) {
			System.out.println("" + e);
		}
		if (currentPlaybackTime > -1) {
			vid.seekTo(currentPlaybackTime);
			currentPlaybackTime = -1;
		} else {
			vid.seekTo(0);
		}
		vid.resume();
	}

	@Override
	public void onStart() {
		super.onStart();
		vid.setBackgroundColor(0x00000000);
		vid.setOnClickListener(handler1);
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	View.OnClickListener handler1 = new View.OnClickListener() {
		public void onClick(View v) {

			vid.start();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dfrom = intent.getStringExtra("from");
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");
		aid = intent.getStringExtra("id");

		_id = Integer.parseInt(intent.getStringExtra("_id"));
		url = intent.getStringExtra("");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText("By : " + Dfrom);
		summary.setText(Dsummary);
		if (intent.getStringExtra("share").equals("off"))
			share.setVisibility(Button.INVISIBLE);
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
		// myDir.mkdirs();
		String fname = name;
		file = new File(myDir, fname);
		if (file.exists()) {

			String roo1t = Environment.getExternalStorageDirectory().toString()
					+ Constants.APP_FOLDER_VIDEO + name;
			vid.setVideoPath(roo1t);
			vid.setZOrderOnTop(false);
			vid.setMediaController(new MediaController(this) {
			});
			vid.requestFocus();
		} else {
			if (Utilities.isInternetConnected()) {
				Download d = new Download(AnnounceVideo.this,
						AnnounceDBAdapter.SQLITE_ANNOUNCE, _id + "");
				d.execute("");
				d.setOnPostExecuteListener(new OnPostExecuteListener() {

					public void onPostExecute(String result) {
						String roo1t = Environment
								.getExternalStorageDirectory().toString()
								+ Constants.APP_FOLDER_VIDEO + name;
						detail.setText(DateUtils.formatDate(Ddetail));
						Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
								roo1t, MediaStore.Images.Thumbnails.MINI_KIND);
						BitmapDrawable bitmapDrawable = new BitmapDrawable(
								thumbnail);
						vid.setBackgroundDrawable(bitmapDrawable);
						btn.setImageDrawable(bitmapDrawable);
						vid.setVideoPath(roo1t);

						vid.setVideoPath(roo1t);
						vid.setZOrderOnTop(false);
						vid.setMediaController(new MediaController(
								AnnounceVideo.this) {
						});
						vid.requestFocus();
					}
				});
			} else {
				Toast.makeText(AnnounceVideo.this,
						"Please check your internet connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		// vid.start();
		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "Announce");
		announce.close();
		reports.updateRead(aid);
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
	}

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		// vid.start();
		Log.v("onPrepared", "called");
	}

	OnTouchListener otl = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				historicX = event.getX();
				historicY = event.getY();
				break;

			case MotionEvent.ACTION_UP:
				if (event.getX() - historicX < -DELTA) {
					// FunctionDeleteRowWhenSlidingLeft();
					return true;
				} else if (event.getX() - historicX > DELTA) {
					// FunctionDeleteRowWhenSlidingRight();
					return true;
				}
				break;
			default:
				return false;
			}
			return false;
		}
	};

	void FunctionDeleteRowWhenSlidingLeft() {
		try {
			// Toast.makeText(getBaseContext(), "left",
			// Toast.LENGTH_LONG).show();
			AnnounceDBAdapter announce = new AnnounceDBAdapter(
					AnnounceVideo.this);
			announce.open();
			Cursor cursor = announce.getRowNext(_id, "Announce");
			Log.v("Left slide cursor has ", cursor.getCount() + "");
			cursor.moveToFirst();

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));

			Log.v("TITLE", title);
			Log.v("Summary", summary);
			itemView(cursor, 0);

			announce.close();
		} catch (Exception e) {
		}
	}

	void FunctionDeleteRowWhenSlidingRight() {
		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(
					AnnounceVideo.this);
			announce.open();
			Cursor cursor = announce.getRowPrivious(_id, "Announce");
			Log.v("Right slide cursor has ", cursor.getCount() + "");
			cursor.moveToFirst();

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));

			Log.v("TITLE", title);
			Log.v("Summary", summary);
			itemView(cursor, 1);

			announce.close();
		} catch (Exception e) {
		}
	}

	public void itemView(Cursor cursor, int direction) {

	}

	/*
	 * Flurry Analytics
	 */

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
