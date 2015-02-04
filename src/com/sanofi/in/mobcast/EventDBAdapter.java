package com.sanofi.in.mobcast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EventDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DATE = "date";
	public static final String KEY_DAY = "day";
	public static final String KEY_TIME = "time";
	public static final String KEY_VENUE = "venue";
	public static final String KEY_EVENTID = "eid";
	public static final String KEY_SUMMARY = "summary";

	private static final String TAG = "EventDBAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "Mobcast";
	private static final String SQLITE_TABLE = "Event";
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static final String DATABASE_CREATE = "CREATE TABLE if not exists "
			+ SQLITE_TABLE + " (" + KEY_ROWID
			+ " integer PRIMARY KEY autoincrement," + KEY_TITLE + ","
			+ KEY_DATE + "," + KEY_DAY + "," + KEY_TIME + "," + KEY_VENUE + ","
			+ KEY_SUMMARY + "," + KEY_EVENTID + ");";

	// +
	// " UNIQUE (" + KEY_TITLE +"));"

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.w(TAG, DATABASE_CREATE);
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
			onCreate(db);
		}
	}

	public EventDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public EventDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public long createEvent(String title, String date, String day, String time,
			String venue, String summary, String id) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_VENUE, venue);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_EVENTID, id);

		return mDb.insert("Event", null, initialValues);
	}

	public void createFlag(String going, String pos) {
		mDb.execSQL("UPDATE Event SET going = 'yes' WHERE _id='" + pos + "';");
	}

	public boolean deleteAllCountries() {

		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_TABLE, null, null);
		Log.w(TAG, Integer.toString(doneDelete));
		return doneDelete > 0;

	}

	public Cursor fetchCountriesByName(String inputText) throws SQLException {
		Log.w(TAG, inputText);
		Cursor mCursor = null;
		if (inputText == null || inputText.length() == 0) {
			mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_DATE, KEY_DAY, KEY_TITLE, KEY_TIME, KEY_VENUE,
					KEY_SUMMARY, KEY_EVENTID }, null, null, null, null, null);

		} else {
			mCursor = mDb.query(true, SQLITE_TABLE, new String[] { KEY_ROWID,
					KEY_TITLE, KEY_DATE, KEY_DAY, KEY_TIME, KEY_VENUE,
					KEY_SUMMARY }, KEY_TIME + " like '%" + inputText + "%'",
					null, null, null, null, null);
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchAllCountries() {

		Cursor mCursor = mDb.query(SQLITE_TABLE, new String[] { KEY_ROWID,
				KEY_DATE, KEY_DAY, KEY_TITLE, KEY_TIME, KEY_VENUE, KEY_SUMMARY,
				KEY_EVENTID }, null, null, null, null, KEY_ROWID + " DESC");

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void insertSomeCountries() {

		createEvent("Happy Sankrant", "14th January", "Monday", "4:00 PM",
				"Juhu Beach", "Kite flying festival", "1");

		// createCountry("ASM","American Samoa","Oceania","Polynesia");
		// createCountry("AND","Andorra","Europe","Southern Europe");
		// createCountry("AGO","Angola","Africa","Central Africa");
		// createCountry("AIA","Anguilla","North America","Caribbean");

	}

}
