package com.sanofi.in.mobcast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

public class NewsAudio extends Activity implements OnClickListener,
		OnPreparedListener, MediaPlayerControl {

	TextView title, detail, from, summary, mtitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name, aid;
	Button share;
	int _id;
	ScrollView sv;
	ImageView btnplay, btnpause;
	Reports reports;
	File file,final_file;
	Long time = (long) 0, start = (long) 0, stop = (long) 0;
	// MediaPlayer mMediaPlayer;

	private MediaController mMediaController;
	private MediaPlayer mMediaPlayer;
	private Handler mHandler = new Handler();
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = NewsAudio.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsaudio);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		reports = new Reports(getApplicationContext(), "News");
		title = (TextView) findViewById(R.id.tvVtitle);
		detail = (TextView) findViewById(R.id.tvVDetail);
		from = (TextView) findViewById(R.id.tvVFrom);
		summary = (TextView) findViewById(R.id.tvVsummary);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		share = (Button) findViewById(R.id.iv6);

		onNewIntent(getIntent());

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reports.updateShare(aid);
				final Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("audio/mp3");
				String shareBody = Dfrom
						+ "\n ON: " + detail.getText() + "\n"
						+ summary.getText();
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title.getText());
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(final_file));
				startActivity(Intent.createChooser(shareIntent, "Share audio"));

			}
		});
		detail.setText(DateUtils.formatDate(Ddetail));
		share.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

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

		/*
		 * try{File file = new
		 * File(Environment.getExternalStorageDirectory().getAbsolutePath() +
		 * "/.mobcast/mobcast_audio/"+name); Uri external = Uri.fromFile(file);
		 * mMediaPlayer = MediaPlayer.create(audio.this,external);
		 * }catch(Exception e){e.printStackTrace();}
		 */

		// ============================================================================

		mMediaPlayer = new MediaPlayer();
		mMediaController = new MediaController(this);
		mMediaController.setMediaPlayer(NewsAudio.this);
		mMediaController.setAnchorView(findViewById(R.id.audioView));
		file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ Constants.APP_FOLDER_AUDIO);
		final_file = new File(file,name);
		
		final String audioFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.APP_FOLDER_AUDIO + name;
		try {
			mMediaPlayer.setDataSource(audioFile);
			mMediaPlayer.prepare();
		} catch (IOException e) {
			
			Download d = new Download(NewsAudio.this, AnnounceDBAdapter.SQLITE_NEWS, _id+"");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					
					try{mMediaPlayer.setDataSource(audioFile);
					mMediaPlayer.prepare();} catch (Exception e){Toast.makeText(getApplicationContext(), "Failed To download file", Toast.LENGTH_SHORT).show();}
				}
			});
			
		}
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mHandler.post(new Runnable() {
					public void run() {
						// mMediaController.show(3000);
						// mMediaPlayer.start();
					}
				});
			}
		});

		// ============================================================================

		btnplay = (ImageView) findViewById(R.id.play1);
		btnplay.setOnClickListener(playhandler);
		btnpause = (ImageView) findViewById(R.id.pause1);
		btnpause.setOnClickListener(pausehandler);

		title.setOnTouchListener(otl);
		from.setOnTouchListener(otl);
		detail.setOnTouchListener(otl);
		summary.setOnTouchListener(otl);
		sv.setOnTouchListener(otl);

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

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Toast.makeText(getApplicationContext(),"16. onDestroy()",
		// Toast.LENGTH_SHORT).show();
		time = (long) mMediaPlayer.getCurrentPosition();
		Log.e("time in mills", mMediaPlayer.getCurrentPosition() + "");
		long seconds = time / 1000;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		long hours = minutes / 60;
		minutes = minutes % 60;
		String timestr = hours + ":" + minutes + ":" + seconds;
		Log.e("time in hh:mm:ss", timestr);
		mMediaPlayer.stop();
		mMediaPlayer.release();
		reports.updateDuration(aid, timestr);
	}

	View.OnClickListener playhandler = new View.OnClickListener() {
		public void onClick(View v) {
			{
				 mMediaPlayer.start();
				btnpause.setVisibility(ImageView.VISIBLE);
				btnplay.setVisibility(ImageView.GONE);
				mMediaController.show(3000);
				mMediaPlayer.start();

			}
		}
	};

	View.OnClickListener pausehandler = new View.OnClickListener() {
		public void onClick(View v) {
			{
				 mMediaPlayer.pause();
				btnplay.setVisibility(ImageView.VISIBLE);
				btnpause.setVisibility(ImageView.GONE);
				mMediaController.show(3000);
				mMediaPlayer.pause();
			}
		}
	};

	@Override
	public void onNewIntent(Intent intent) {

		try {
			Dtitle = intent.getStringExtra("title");
			Ddetail = intent.getStringExtra("detail");
			Dfrom = intent.getStringExtra("link");
			Dsummary = intent.getStringExtra("summary");
			name = intent.getStringExtra("name");
			aid = intent.getStringExtra("id");
			String shareflag = intent.getStringExtra("shareKey");
			if (shareflag.trim().contentEquals("off"))
				share.setVisibility(Button.INVISIBLE);
			_id = Integer.parseInt(intent.getStringExtra("_id"));
		} catch (Exception e) {
		}
		// url = intent.getStringExtra("");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText("Go to source");
		from.setTextColor(Color.BLUE);
		summary.setText(Dsummary);

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "News");
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

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		int percentage = (mMediaPlayer.getCurrentPosition() * 100)
				/ mMediaPlayer.getDuration();
		return percentage;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			btnplay.setVisibility(ImageView.VISIBLE);
			btnpause.setVisibility(ImageView.GONE);
			stop = System.currentTimeMillis();
			time += stop - start;
		}
	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		mMediaPlayer.start();
		btnpause.setVisibility(ImageView.VISIBLE);
		btnplay.setVisibility(ImageView.GONE);
		start = System.currentTimeMillis();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mMediaController.show();

		return false;
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
			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsAudio.this);
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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsAudio.this);
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

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
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
