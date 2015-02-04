package com.sanofi.in.mobcast;

import java.util.HashMap;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Utilities;

public class NewsText extends Activity {

	TextView link, title, detail, summary;
	ScrollView sv1;
	String Dtitle, Ddetail, Dsummary, url, caption, tid;
	int _id;
	Button share;
	String linktext;
	Reports reports;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = NewsText.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newstext);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		title = (TextView) findViewById(R.id.tvNTtitle);
		reports= new Reports(getApplicationContext(),"News");
		summary = (TextView) findViewById(R.id.tvNTsummary);
		link = (TextView) findViewById(R.id.tvLink);
		detail = (TextView) findViewById(R.id.tvNTtime);
		sv1 = (ScrollView) findViewById(R.id.sv2);
		sv1.setOnTouchListener(otl);
		share = (Button) findViewById(R.id.iv6);
		share.setOnClickListener(myhandler1);
		onNewIntent(getIntent());
		detail.setText(DateUtils.formatDate(Ddetail));
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			reports.updateShare(tid);
			// works for everyapp except facebook only link works on facebook
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			// sharingIntent.setType("message/rfc822");
			String shareBody = url
					+ "\n ON: " + detail.getText() + "\n" + summary.getText();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					title.getText());
			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));

		}
	};

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
		Log.v("scrollView", "Left swipe");

		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsText.this);
			announce.open();
			Cursor cursor = announce.getRowNextNews(_id);

			cursor.moveToFirst();

			itemView(cursor, 0);

			announce.close();
		} catch (Exception e) {
		}
		finish();

	}

	void FunctionDeleteRowWhenSlidingRight() {
		Log.v("scrollView", "Right swipe");
		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsText.this);
			announce.open();
			Cursor cursor = announce.getRowPriviousNews(_id);

			cursor.moveToFirst();

			itemView(cursor, 1);

			announce.close();
		} catch (Exception e) {
		}
		finish();

	}

	public void itemView(Cursor cursor, int direction) {
		// Get the cursor, positioned to the corresponding row in the result set

		// Get the state's capital from this row in the database.
		String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		String detail = cursor
				.getString(cursor.getColumnIndexOrThrow("detail"));
		String summary = cursor.getString(cursor
				.getColumnIndexOrThrow("summary"));
		String tid = cursor.getString(cursor.getColumnIndexOrThrow("nid"));
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		String link = cursor.getString(cursor.getColumnIndexOrThrow("link"));
		linktext = cursor.getString(cursor.getColumnIndexOrThrow("link"));
		String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));



		Intent show;
		if (type.contentEquals("video")) {
			show = new Intent(NewsText.this, NewsVideo.class);
		} else
			show = new Intent(NewsText.this, NewsText.class);
		show.putExtra("title", title);
		show.putExtra("detail", detail);
		show.putExtra("name", name);
		show.putExtra("link", link);
		show.putExtra("summary", summary);
		show.putExtra("id", tid);
		show.putExtra("_id", _id);
		startActivity(show);
		if (direction == 0)
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		else
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		// Toast.makeText(getApplicationContext(),
		// title, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dsummary = intent.getStringExtra("summary");
		// show.putExtra("shareKey", share);

		url = intent.getStringExtra("link");
		tid = intent.getStringExtra("id");
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		// Log.v("link", linktext);
		link.setText("Go to source");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		summary.setText(Dsummary);
		String shareflag = intent.getStringExtra("shareKey");
		if (shareflag.trim().contentEquals("off"))
			share.setVisibility(Button.INVISIBLE);
		try {

			link.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					reports.updateLinkCLicked(tid);

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

		Pattern pattern = Pattern.compile("\\d{5}([\\-]\\d{4})?");
		Linkify.addLinks(link, pattern, url);
		
		AnnounceDBAdapter announce = new AnnounceDBAdapter(getApplicationContext());
		announce.open();
		announce.readrow(_id+"", "News");
		announce.close();
		reports.updateRead(tid);
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
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
