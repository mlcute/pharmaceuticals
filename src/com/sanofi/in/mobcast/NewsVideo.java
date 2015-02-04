package com.sanofi.in.mobcast;

import java.io.File;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
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
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

@SuppressLint("NewApi")
public class NewsVideo extends Activity implements OnClickListener,
		OnPreparedListener {

	TextView title, detail, from, summary, mtitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name, aid;
	int _id;
	Button share;
	ScrollView sv;
	SharedPreferences prefs;
	ImageView vfullscreenplay;
	Reports reports;
	File file;
	com.sanofi.in.mobcast.CustomVideoView vid;
	ImageView btn, play;
	long time = (long) 0, start = (long) 0, stop = (long) 0;
	MediaController mc;
	private MediaPlayer mMediaPlayer;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = NewsVideo.class.getSimpleName();
	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.nvideo);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		title = (TextView) findViewById(R.id.tvVtitle);
		detail = (TextView) findViewById(R.id.tvVDetail);
		from = (TextView) findViewById(R.id.tvVFrom);
		reports= new Reports(getApplicationContext(),"News");
		vfullscreenplay = (ImageView) findViewById(R.id.nfullscreenplay);
		summary = (TextView) findViewById(R.id.tvVsummary);
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		vid = (com.sanofi.in.mobcast.CustomVideoView) findViewById(R.id.vvType);
		sv = (ScrollView) findViewById(R.id.scrollView1);

		share = (Button) findViewById(R.id.iv6);
		
		vid.setOnPreparedListener(this);
		play = (ImageView) findViewById(R.id.vbackgroundplay);
		play.setOnClickListener(BackgroundclkListener);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			play.setAlpha(0.7f);
		}

		btn = (ImageView) findViewById(R.id.vbackground);
		btn.setOnClickListener(BackgroundclkListener);
		onNewIntent(getIntent());

		String roo1t = Environment.getExternalStorageDirectory().toString()
				+ Constants.APP_FOLDER_VIDEO + name;
		Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(roo1t,
				MediaStore.Images.Thumbnails.MINI_KIND);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
		vid.setBackgroundDrawable(bitmapDrawable);
		// btn.setBackgroundDrawable(bitmapDrawable);
		detail.setText(DateUtils.formatDate(Ddetail));

		btn.setImageDrawable(bitmapDrawable);
		vid.setVideoPath(roo1t);

		mc = new MediaController(this);
		mc.setAnchorView(vid);
		vid.setMediaController(mc);
		vid.requestFocus();

		//vid.setPlayPauseListener(ppl);

		title.setOnTouchListener(otl);
		from.setOnTouchListener(otl);
		detail.setOnTouchListener(otl);
		summary.setOnTouchListener(otl);
		sv.setOnTouchListener(otl);
		// sv.setOnTouchListener(otl);

		try {

			from.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					reports.updateLinkCLicked(aid);
					url = Dfrom;
					// Toast.makeText(getApplicationContext(), "Text Touch",
					// Toast.LENGTH_SHORT).show();
					if (url.contains("http")) {

						Intent i = new Intent(Intent.ACTION_VIEW, Uri
								.parse(url));
						startActivity(i);
					} else {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://" + url));
						startActivity(i);

					}

				}
			});
		} catch (Exception e) {
		}

		share.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

					if (android.os.Build.VERSION.SDK_INT >= 11) {
						v.setAlpha(0.5f);
					}

					// v.getBackground().setAlpha(45);

				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

					if (android.os.Build.VERSION.SDK_INT >= 11) {
						v.setAlpha(0.5f);
					}
					// v.getBackground().setAlpha(255);
				}

				return false;
			}
		});

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reports.updateShare(aid);
				final Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("video/mp4");
				String shareBody = Dfrom
						+ "\n ON: " + detail.getText() + "\n"
						+ summary.getText();
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title.getText());
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(file));
				startActivity(Intent.createChooser(shareIntent, "Share video"));

			}
		});
		vfullscreenplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vid.pause();
				time=vid.getCurrentPosition();
				Log.d("Paused at ",""+time);
				Intent i = new Intent(NewsVideo.this, VideoFullscreen.class);
				i.putExtra("name", name);
				Log.d("name", name);
				i.putExtra("StartAt", (int)time);
				onDestroy();
				startActivity(i);
			}
		});

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
	protected void onResume() {
		super.onResume();
		int st=0;
		try{
			st = prefs.getInt("StartAt", 0);
			System.out.println("St------->"+st);
			prefs.edit().remove("StartAt").commit();
			}catch(Exception e)
			{
				System.out.println(""+e);
			}
			vid.seekTo(st);
			System.out.println(""+st);
			vid.start();
	};

	CustomVideoView.PlayPauseListener ppl = new CustomVideoView.PlayPauseListener() {

		@Override
		public void onPlay() {
			System.out.println("Play!");
			start = System.currentTimeMillis();
			play.setVisibility(ImageView.GONE);

		}

		@Override
		public void onPause() {
			System.out.println("Pause!");
			stop = System.currentTimeMillis();
			play.setVisibility(ImageView.VISIBLE);
			time += stop - start;
		}
	};

	View.OnClickListener BackgroundclkListener = new View.OnClickListener() {
		public void onClick(View v) {
			btn.setVisibility(ImageView.GONE);
			vid.setVisibility(com.sanofi.in.mobcast.CustomVideoView.VISIBLE);
			vid.setPlayPauseListener(ppl);
			vid.start();
		}
	};

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
		Dfrom = intent.getStringExtra("link");
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");
		String shareflag = intent.getStringExtra("shareKey");
		if (shareflag.trim().contentEquals("off"))
			share.setVisibility(Button.INVISIBLE);
		aid = intent.getStringExtra("id");

		_id = Integer.parseInt(intent.getStringExtra("_id"));
		url = intent.getStringExtra("");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText("Go to source");
		from.setTextColor(Color.BLUE);
		summary.setText(Dsummary);

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
		// myDir.mkdirs();
		String fname = name;
		 file = new File(myDir, fname);
if(file.exists()){
		String roo1t = Environment.getExternalStorageDirectory().toString()
				+ Constants.APP_FOLDER_VIDEO + fname;
		vid.setVideoPath(roo1t);
		vid.setZOrderOnTop(false);
		vid.setMediaController(new MediaController(this) {
		});
		vid.requestFocus();
}
else{
	Download d = new Download (NewsVideo.this,AnnounceDBAdapter.SQLITE_NEWS,_id+"");
	d.execute("");
	d.setOnPostExecuteListener(new OnPostExecuteListener() {

		public void onPostExecute(String result) {
			String roo1t = Environment.getExternalStorageDirectory().toString()
					+ Constants.APP_FOLDER_VIDEO + name;
			detail.setText(DateUtils.formatDate(Ddetail));
			Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(roo1t,
					MediaStore.Images.Thumbnails.MINI_KIND);
			BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
			vid.setBackgroundDrawable(bitmapDrawable);
			btn.setImageDrawable(bitmapDrawable);
			vid.setVideoPath(roo1t);
			
			
			vid.setVideoPath(roo1t);
			vid.setZOrderOnTop(false);
			vid.setMediaController(new MediaController(NewsVideo.this) {
			});
			vid.requestFocus();
		}
	});
}
		// vid.start();
		AnnounceDBAdapter announce = new AnnounceDBAdapter(getApplicationContext());
		announce.open();
		announce.readrow(_id+"", "News");
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
			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsVideo.this);
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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsVideo.this);
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
