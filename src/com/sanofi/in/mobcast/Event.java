package com.sanofi.in.mobcast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class Event extends Activity implements OnClickListener {

	String title, summary, date, venue, days, time, eid, month, pos;
	int year, mnth, hrs, min, day, shareflag = 0;
	long startTime, endTime;
	int allDayFlag = 1;
	String rsvpNeeded, calenderEnabled;
	Button yes, no, addToCalendar, share;
	LinearLayout optionspace;
	IntentFilter gcmFilter;
	Reports reports;
	TextView u1, u2, u3;
	LinearLayout rl2;
	int _id;
	ScrollView sv2;
	TextView Dtitle, Dsummary, Ddate, Dvenue, Dday, Dtime, Dmonth,
			Dsuperscript;
	private static final Random random = new Random();
	ConnectionDetector cd;

	boolean check = true, isInternetPresent;
	int uniqueID = 12345678;
	String message, reply;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = Event.class.getSimpleName();
	// SA ADDED VIKALP EVENT
	private TextView Dtimeam, Etime, Etimeam;
	private String endTTime;

	// EA ADDED VIKALP EVENT

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.events);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		vdate vd1 = new vdate("16-05-2013");
		vd1.getRDate();
		reports = new Reports(getApplicationContext(), "Event");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		yes = (Button) findViewById(R.id.bYes);
		yes.setOnTouchListener(otl);
		no = (Button) findViewById(R.id.bNo);
		no.setOnTouchListener(otl);
		addToCalendar = (Button) findViewById(R.id.bCalendar);
		share = (Button) findViewById(R.id.bShare);
		addToCalendar.setOnTouchListener(otl);
		rl2 = (LinearLayout) findViewById(R.id.rl2);
		rl2.setOnTouchListener(otl);
		Dtitle = (TextView) findViewById(R.id.tvTitle);
		Dtitle.setOnTouchListener(otl);
		Dsummary = (TextView) findViewById(R.id.tvSummary);
		Dsummary.setOnTouchListener(otl);
		Ddate = (TextView) findViewById(R.id.tvDate);
		Ddate.setOnTouchListener(otl);
		Dvenue = (TextView) findViewById(R.id.tvVenue);
		Dvenue.setOnTouchListener(otl);
		Dday = (TextView) findViewById(R.id.tvDay);
		Dday.setOnTouchListener(otl);
		Dtime = (TextView) findViewById(R.id.tvTime);
		// SA ADDED VIKALP EVENT
		Dtimeam = (TextView) findViewById(R.id.tvTimeam);
		Etime = (TextView) findViewById(R.id.tvEndTime);
		Etimeam = (TextView) findViewById(R.id.tvEndTimeam);
		// EA ADDED VIKALP EVENT
		Dtime.setOnTouchListener(otl);
		Dmonth = (TextView) findViewById(R.id.tvMonth);
		Dmonth.setOnTouchListener(otl);
		Dsuperscript = (TextView) findViewById(R.id.superscript);
		Dsuperscript.setOnTouchListener(otl);
		sv2 = (ScrollView) findViewById(R.id.sv);
		sv2.setOnTouchListener(otl);

		u1 = (TextView) findViewById(R.id.abt);
		u1.setOnTouchListener(otl);
		u2 = (TextView) findViewById(R.id.options);
		optionspace = (LinearLayout) findViewById(R.id.optionsspace);
		u2.setOnTouchListener(otl);
		u3 = (TextView) findViewById(R.id.details);
		u3.setOnTouchListener(otl);

		onNewIntent(getIntent());

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (shareflag == 0)
					Toast.makeText(getApplicationContext(), "Sharing Disabled",
							Toast.LENGTH_SHORT).show();
				else {

					reports.updateShare(eid);
					Intent share1 = new Intent(Intent.ACTION_SEND);
					share1.setType("text/plain");

					// share.setType("message/rfc822");

					String shareBody = "Event \n";

					// TextView Dtitle,Dsummary,Ddate,Dvenue,Dday,Dtime,Dmonth,
					// Dsuperscript;

					shareBody = shareBody + "TITLE: "
							+ Dtitle.getText().toString() + "\n";

					// shareBody=shareBody+(intent.getStringExtra(RecruitList.KEY_TITLE))+"\n";

					shareBody = shareBody + "VENUE:  "
							+ Dvenue.getText().toString() + "\n";

					// shareBody=shareBody+intent.getStringExtra(RecruitList.KEY_DESIGNATION)+"\n";

					shareBody = shareBody + "DATE: "
							+ Ddate.getText().toString()
							+ Dsuperscript.getText().toString() + " "
							+ Dmonth.getText().toString() + "\n";

					// shareBody=shareBody+intent.getStringExtra(RecruitList.KEY_MINEXP)+"\n";

					shareBody = shareBody + "TIME: "
							+ Dtime.getText().toString();

					// shareBody=shareBody+intent.getStringExtra(RecruitList.KEY_LOC)+"\n";

					share1.putExtra(Intent.EXTRA_TEXT, shareBody);
					share1.putExtra(android.content.Intent.EXTRA_SUBJECT,
							"Event");

					startActivity(Intent.createChooser(share1, "Share Event"));

				}
			}
		});

		cd = new ConnectionDetector(this);

		isInternetPresent = cd.isConnectingToInternet();

		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		addToCalendar.setOnClickListener(this);

	}

	private void addToCalendar() {
		// TODO Auto-generated method stub

		Log.e("time", time);
		if (time.substring(6, 8).equals("am"))
			hrs = Integer.parseInt(time.substring(0, 2));
		else
			hrs = Integer.parseInt(time.substring(0, 2)) + 12;
		min = Integer.parseInt(time.substring(3, 5));
		Log.e("hrs", hrs + "");
		Log.e("MINS", min + "");

		Log.e("time", time);
		mnth = Integer.parseInt(date);
		Log.e("month", mnth + "");
		day = Integer.parseInt(String.valueOf(Ddate.getText()));
		Log.e("day", day + "");

		GregorianCalendar beginCal = new GregorianCalendar(
				new GregorianCalendar().get(Calendar.YEAR), mnth - 1, day, hrs,
				min);

		GregorianCalendar endCal = new GregorianCalendar(
				new GregorianCalendar().get(Calendar.YEAR), mnth - 1, day,
				hrs + 1, min);

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.DESCRIPTION, summary);
		intent.putExtra(Events.EVENT_LOCATION, venue);
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				beginCal.getTimeInMillis());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				endCal.getTimeInMillis());
		intent.putExtra(Events.ALL_DAY, allDayFlag);
		intent.putExtra(Events.STATUS, 1);
		intent.putExtra(Events.VISIBLE, 0);
		intent.putExtra(Events.HAS_ALARM, 1);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bCalendar:
			addToCalendar();
			reports.updateAddToCalendar(eid);
			break;

		case R.id.bYes:
			reply = "1";
			reports.updateRSVP(eid, "yes");

			reply(reply);
			break;

		case R.id.bNo:
			reply = "0";
			reports.updateRSVP(eid, "no");
			reply(reply);
			break;

		}

	}

	// code to change event when sliding left or right
	// implementation of ontouch listener for reference please go to
	// http://stackoverflow.com/questions/7308836/resource-for-android-slight-left-right-slide-action-on-listview
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
					Log.v("slide", "left");

					return true;
				} else if (event.getX() - historicX > DELTA) {

					// FunctionDeleteRowWhenSlidingRight();
					Log.v("slide", "right");
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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(Event.this);
			announce.open();
			Cursor cursor = announce.getRowNextEvents(_id, "Event");
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
		finish();
	}

	void FunctionDeleteRowWhenSlidingRight() {
		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(Event.this);
			announce.open();
			Cursor cursor = announce.getRowPriviousEvents(_id, "Event");
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
		finish();
	}

	public void itemView(Cursor cursor, int direction) {
		// Get the cursor, positioned to the corresponding row in the result set

		// Get the state's capital from this row in the database.
		String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
		String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
		String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
		String venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"));
		String summary = cursor.getString(cursor
				.getColumnIndexOrThrow("summary"));
		String rsvp = cursor.getString(cursor.getColumnIndexOrThrow("rsvp"));
		String eid = cursor.getString(cursor.getColumnIndexOrThrow("eid"));
		Log.e("eid", eid + "");
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

		Intent show = new Intent(Event.this, Event.class);
		show.putExtra("title", title);
		show.putExtra("date", date);
		show.putExtra("rsvp", rsvp);
		show.putExtra("day", day);
		show.putExtra("time", time);
		show.putExtra("venue", venue);
		show.putExtra("summary", summary);
		show.putExtra("id", eid);
		show.putExtra("_id", _id);

		// show.putExtra("pos", position);
		startActivity(show);
		if (direction == 0)
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		else
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

		// Toast.makeText(getApplicationContext(),
		// title, Toast.LENGTH_SHORT).show();
	}

	// ontouch listener code ends

	// A BroadcastReceiver must override the onReceive() event.

	@Override
	public void onNewIntent(Intent intent) {

		title = intent.getStringExtra("title");
		date = intent.getStringExtra("date");
		days = intent.getStringExtra("day");
		calenderEnabled = intent.getStringExtra("calenderEnabled");
		rsvpNeeded = intent.getStringExtra("rsvpNeeded");

		if (intent.getStringExtra("shareKey").equals("on")) {
			shareflag++;
		} else
			share.setVisibility(Button.GONE);
		if (rsvpNeeded.contains("off")) {
			yes.setVisibility(Button.GONE);
			no.setVisibility(Button.GONE);
		}
		if (calenderEnabled.contains("off"))
			addToCalendar.setVisibility(Button.GONE);

		time = intent.getStringExtra("time");
		endTTime = intent.getStringExtra("endTime"); // ADDED VIKALP EVENT END
														// TIME

		if (TextUtils.isEmpty(endTTime)) {
				endTTime = "00:00 am";
		}
		String type = intent.getStringExtra("rsvp");
		if (type.contentEquals("3")) {
			// .setImageResource(R.drawable.pending);
		} else if (type.contentEquals("1")) {
			yes.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.yes1));
		} else {
			no.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.no1));
		}

		// Toast.makeText(Event.this,hh,Toast.LENGTH_LONG).show();

		venue = intent.getStringExtra("venue");
		summary = intent.getStringExtra("summary");
		eid = intent.getStringExtra("id");
		// pos = intent.getStringExtra("pos");
		days = days.toUpperCase();
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		days = days.substring(0, 3);
		int mon = Integer.parseInt(date.substring(0, 2));
		date = date.substring(3, 5);

		cd = new ConnectionDetector(this);
		isInternetPresent = cd.isConnectingToInternet();

		switch (Integer.parseInt(date)) {
		case 1:
			month = "January";
			break;
		case 2:
			month = "February";
			break;
		case 3:
			month = "March";
			break;
		case 4:
			month = "April";
			break;
		case 5:
			month = "May";
			break;
		case 6:
			month = "June";
			break;
		case 7:
			month = "July";
			break;
		case 8:
			month = "August";
			break;
		case 9:
			month = "September";
			break;
		case 10:
			month = "October";
			break;
		case 11:
			month = "November";
			break;
		case 12:
			month = "December";
			break;

		}
		// month="test";
		Spanned newdate;
		int x = mon % 10;

		switch (x) {
		case 1:
			newdate = Html.fromHtml("<sup>st</sup>");
			// Toast.makeText(this,newdate,Toast.LENGTH_LONG).show();
			break;
		case 2:
			newdate = Html.fromHtml("<sup>nd</sup>");
			// Toast.makeText(this,newdate,Toast.LENGTH_LONG).show();
			break;
		case 3:
			newdate = Html.fromHtml("<sup>rd</sup>");
			// Toast.makeText(this,newdate,Toast.LENGTH_LONG).show();
			break;
		default:
			newdate = Html.fromHtml("<sup>th</sup>");

			// Toast.makeText(this,newdate,Toast.LENGTH_LONG).show();
			break;
		}
		if ((mon == 11) || (mon == 12) || (mon == 13))
			newdate = Html.fromHtml("<sup>th</sup>");

		Dsuperscript.setText(newdate);

		if (title != null) {
			Dtitle.setText(title);
			Dsummary.setText(summary);
			// Dtime.setText(time);
			// SA ADDED VIKALP EVENT END TIME
			Dtime.setText(time.substring(0, 5));
			Dtimeam.setText(time.substring(6, 8));
			Etime.setText(endTTime.substring(0, 5));
			Etimeam.setText(endTTime.substring(6, 8));
			// EA ADDED VIKALP EVENT END TIME
			Dvenue.setText(venue);
			Ddate.setText(String.valueOf(mon));
			// Ddate.append(newdate);
			Dmonth.setText(month.substring(0, 3));
			Dday.setText(days);
		}

		// Button yes, no, addToCalendar, share;
		if ((yes.getVisibility() == Button.GONE)
				&& (addToCalendar.getVisibility() == Button.GONE)
				&& (share.getVisibility() == Button.GONE)) {
			optionspace.setVisibility(LinearLayout.GONE);
			u2.setVisibility(TextView.GONE);
		}

		AnnounceDBAdapter announce = new AnnounceDBAdapter(Event.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id + "", "Event");
		announce.close();
		reports.updateRead(eid);

		// SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try {
			Utilities.cancelLolliPopNotification(ApplicationLoader
					.getApplication());
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
		// EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
	}

	// When an activity is resumed, be sure to register any
	// broadcast receivers with the appropriate intent

	void reply(String reply) {

		AnnounceDBAdapter db = new AnnounceDBAdapter(this);
		db.open();

		db.eventRsvp(reply, eid);
		db.close();
		String type = reply;
		if (type.contentEquals("3")) {
			// .setImageResource(R.drawable.pending);
		} else if (type.contentEquals("1")) {
			yes.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.yes1));
			no.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.no0));
		} else {
			no.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.no1));
			yes.setBackgroundDrawable(getBaseContext().getResources()
					.getDrawable(R.drawable.yes0));
		}

	}

	// post request to server

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		FlurryAgent.onEndSession(this);
	}
}
