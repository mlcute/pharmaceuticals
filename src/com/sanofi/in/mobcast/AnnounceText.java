package com.sanofi.in.mobcast;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Utilities;

public class AnnounceText extends Activity {

	TextView title, detail, summary, by;
	String Dtitle, Ddetail, Dsummary;
	Button share;
	String aid;
	Reports reports;
	int _id;
	ScrollView sv;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = AnnounceText.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.atext);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		//NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		//mNotificationManager.cancelAll();
		reports = new Reports(getApplicationContext(), "Announce");
		title = (TextView) findViewById(R.id.tvATtitle);
		detail = (TextView) findViewById(R.id.tvATtime);
		summary = (TextView) findViewById(R.id.tvATsummary);
		by = (TextView) findViewById(R.id.tvATby);
		share = (Button) findViewById(R.id.iv6);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		onNewIntent(getIntent());
		share.setOnClickListener(myhandler1);
		share.setOnTouchListener(myhandler2);
		// =====================================================================
		detail.setText(DateUtils.formatDate(Ddetail));
		title.setOnTouchListener(otl);

		by.setOnTouchListener(otl);
		detail.setOnTouchListener(otl);
		summary.setOnTouchListener(otl);
		sv.setOnTouchListener(otl);
		// =====================================================================

	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(0.5f);}
				v.getBackground().setAlpha(45);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
				v.getBackground().setAlpha(255);
			}

			return false;
		}

	};

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			// Toast.makeText(AnnounceText.this, "Test",
			// Toast.LENGTH_SHORT).show();

			reports.updateShare(aid);

			// works for everyapp except facebook
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			// sharingIntent.setType("message/rfc822");
			
			Log.d("detail", detail.getText().toString());
			Log.d("summary", summary.getText().toString());
			Log.d("title", title.getText().toString());
			
			String shareBody = detail.getText() + "\n" + summary.getText();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					title.getText());
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));

			/*
			 * Intent sharingIntent = new
			 * Intent(android.content.Intent.ACTION_SEND);
			 * sharingIntent.setType("text/plain"); List activities =
			 * getPackageManager().queryIntentActivities(sharingIntent,0); //
			 * sharingIntent.setClassName(null,null);
			 * sharingIntent.setType("text/plain");
			 * sharingIntent.putExtra(android
			 * .content.Intent.EXTRA_SUBJECT,"hello");
			 * sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
			 * "facebook");
			 */

		}
	};

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dsummary = intent.getStringExtra("summary");
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		aid = intent.getStringExtra("id");

		Log.v("from", intent.getStringExtra("from"));
		title.setText(Dtitle);
		detail.setText(Ddetail);

		summary.setText(Dsummary);

		by.setText("By : " + intent.getStringExtra("from"));
		Log.d("share", intent.getStringExtra("share"));
		if (intent.getStringExtra("share").equals("off"))
			share.setVisibility(Button.INVISIBLE);

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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(
					AnnounceText.this);
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
					AnnounceText.this);
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

		// Get the state's capital from this row in the database.
		String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		String detail = cursor
				.getString(cursor.getColumnIndexOrThrow("detail"));
		String from = cursor.getString(cursor.getColumnIndexOrThrow("fro"));
		String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		String summary = cursor.getString(cursor
				.getColumnIndexOrThrow("summary"));
		String aid = cursor.getString(cursor.getColumnIndexOrThrow("aid"));
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

		Intent show;
		if (type.contentEquals("image")) {
			show = new Intent(getBaseContext(), Announcements.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			finish();
		} else if (type.contentEquals("video")) {

			show = new Intent(AnnounceText.this, AnnounceVideo.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();
			startActivity(show);

			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
		} else if (type.equals("audio"))

		{

			show = new Intent(AnnounceText.this, audio.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancelAll();
			startActivity(show);

			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);

		}// end of audio
		else {
			show = new Intent(getBaseContext(), AnnounceText.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			finish();
		}

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
