package com.mobcast.receiver;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.Reports;

public class EventCalendarReceiver extends WakefulBroadcastReceiver {

	private Context mContext;
	private Intent mIntent;
	private String mId;
	private String dbID;

	private static final String TAG = EventCalendarReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context mContext, Intent mIntent) {
		// TODO Auto-generated method stub
		this.mContext = mContext;
		this.mIntent = mIntent;
		mId = mIntent.getExtras().getString("id");
		dbID = mIntent.getExtras().getString("dbID");
		Utilities.cancelNotification(mContext);
		Reports reports = new Reports(mContext, "Event");
		addToCalendar();
		reports.updateRead(mId);
		reports.updateAddToCalendar(mId);
	}

	@SuppressLint("InlinedApi")
	public void addToCalendar() {
		String title, summary, date, venue, time, dayOfWeek;
		int mnth, hrs, min, day;
		int allDayFlag = 1;
		Cursor cursor = null;

		try {
			AnnounceDBAdapter db = new AnnounceDBAdapter(mContext);
			db.open();
			db.readrow(dbID + "", "Event");
			cursor = (Cursor) db
					.fetchItem(AnnounceDBAdapter.SQLITE_EVENT, dbID);

			if (cursor != null)
				cursor.moveToFirst();

			Log.i(TAG, dbID);
			title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
			date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
			time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
			venue = cursor.getString(cursor.getColumnIndexOrThrow("venue"));
			summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));

			Log.e("time", time);
			if (time.substring(6, 8).equals("am"))
				hrs = Integer.parseInt(time.substring(0, 2));
			else
				hrs = Integer.parseInt(time.substring(0, 2)) + 12;
			min = Integer.parseInt(time.substring(3, 5));
			Log.e("hrs", hrs + "");
			Log.e("MINS", min + "");

			Log.e("time", time);
			mnth = Integer.parseInt(date.substring(3, 5));
			day = Integer.parseInt(date.substring(0, 2));
			Log.e("month", mnth + "");
			Log.e("day", day + "");

			GregorianCalendar beginCal = new GregorianCalendar(
					new GregorianCalendar().get(Calendar.YEAR), mnth - 1, day,
					hrs, min);

			GregorianCalendar endCal = new GregorianCalendar(
					new GregorianCalendar().get(Calendar.YEAR), mnth - 1, day,
					hrs + 1, min);

			db.close();
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
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);

			Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			mContext.sendBroadcast(it);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
}
