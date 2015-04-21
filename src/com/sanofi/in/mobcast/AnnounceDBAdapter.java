package com.sanofi.in.mobcast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class AnnounceDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_DETAIL = "detail";
	public static final String KEY_FROM = "fro";
	public static final String KEY_TYPE = "type";
	public static final String KEY_LINK = "link";
	public static final String KEY_FILELINK = "fileLink";
	public static final String KEY_NAME = "name";
	public static final String KEY_ANNOUNCEID = "aid";
	public static final String KEY_SHARE = "shareKey";
	public static final String KEY_ENDTIME = "endTime"; //ADDED VIKALP END TIME

	public static final String KEY_contentExpiry = "contentExpiry";

	public static final String KEY_ENCRYPTION = "ename";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_DATE = "date";
	public static final String KEY_DAY = "day";
	public static final String KEY_CAPTION = "caption";
	public static final String KEY_TIME = "time";
	public static final String KEY_VENUE = "venue";
	public static final String KEY_EVENTID = "eid";
	public static final String KEY_TRAINID = "tid";
	public static final String KEY_NEWSID = "nid";

	public static final String KEY_calenderEnabled = "calenderEnabled";
	public static final String KEY_rsvpNeeded = "rsvpNeeded";

	public static final String KEY_TAGS = "tags";
	// ==============================================
	public static final String KEY_READ = "read_id";
	// ==============================================
	public static final String KEY_AWARDID = "awardId";
	public static final String KEY_IMAGEPATH = "imagePath";// For Award module
	public static final String KEY_FORMID = "formId";// for Feedback module
	public static final String KEY_QUESNO = "quesNo";// for Feedback Module
	public static final String KEY_QUES = "ques";// for Feedback Module
	public static final String KEY_OPTION1 = "option1"; // For Feedback Module
	public static final String KEY_OPTION2 = "option2"; // For Feedback Module
	public static final String KEY_OPTION3 = "option3"; // For Feedback Module
	public static final String KEY_OPTION4 = "option4"; // For Feedback Module

	public static final String KEY_RSVP = "rsvp";

	private static final String TAG = "AnnounceDBAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public static final String DATABASE_NAME = "Mobcast.db";
	public static final String SQLITE_ANNOUNCE = "Announce";
	public static final String SQLITE_EVENT = "Event";
	public static final String SQLITE_TRAINING = "Training";
	public static final String SQLITE_NEWS = "News";
	public static final String SQLITE_AWARD = "Award";
	public static final String SQLITE_FEEDBACK = "Feedback";
	public static final String SQLITE_DOCUMENT = "Document";
	public static final String SQLITE_LINKS = "Links";

	private static final int DATABASE_VERSION = 27;

	private final Context mCtx;

	private static final String DATABASE_CREATE_ANNOUNCE = "CREATE TABLE if not exists "
			+ SQLITE_ANNOUNCE
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_DETAIL
			+ ","
			+ KEY_FROM
			+ ","
			+ KEY_FILELINK
			+ ","
			+ KEY_contentExpiry
			+ ","
			+ KEY_TYPE
			+ ","
			+ KEY_NAME
			+ ","
			+ KEY_SUMMARY + "," +
			// -------------------------------------------------------------------
			KEY_READ + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
			// KEY_ANNOUNCEID + " , " + KEY_SHARE + " DEFAULT 'no'," + KEY_TAGS
			// + " DEFAULT 'notags'" + ");";
//			KEY_ANNOUNCEID + " , " + KEY_SHARE + " DEFAULT 'no' " + ");";
	KEY_ANNOUNCEID + " TEXT UNIQUE " + " , " + KEY_SHARE + " DEFAULT 'no' " + ");"; //ADDED VIKALP PULL SERVICE

	private static final String DATABASE_CREATE_EVENT = "CREATE TABLE if not exists "
			+ SQLITE_EVENT
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_DATE
			+ ","
			+ KEY_DAY
			+ ","
			+ KEY_contentExpiry
			+ ","
			+ KEY_TIME
			+ ","
			+ KEY_VENUE
			+ ", "
			+ KEY_rsvpNeeded
			+ ","
			+ KEY_calenderEnabled
			+ ", "
			+ KEY_RSVP
			+ ","
			+ KEY_ENDTIME //SA ADDED VIKALP EVENT END TIME
			+ "," //EA ADDED VIKALP EVENT END TIME
			+ KEY_SUMMARY
			+ ","
			+
			// -------------------------------------------------------------------
			KEY_READ
			+ " DEFAULT \'0\',"
			+ " Rtime TIMESTAMP  DEFAULT CURRENT_TIMESTAMP, " +
			// -------------------------------------------------------------------
//			KEY_EVENTID + " , " + KEY_SHARE + " DEFAULT 'no'" + ");";
	KEY_EVENTID +" TEXT UNIQUE"+ " , " + KEY_SHARE + " DEFAULT 'no'" + ");"; //ADDED VIKALP PULL SERVICE

	private static final String DATABASE_CREATE_TRAINING = "CREATE TABLE if not exists "
			+ SQLITE_TRAINING
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_NAME
			+ ","
			+ KEY_DETAIL
			+ ","
			+ KEY_FILELINK
			+ ","
			+ KEY_contentExpiry
			+ ","
			+ KEY_SUMMARY
			+ ","
			+ KEY_TYPE
			+ ","
			+ KEY_ENCRYPTION + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
			KEY_READ + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
//			KEY_TRAINID + " , " + KEY_SHARE + " DEFAULT 'no'" + ");";
			KEY_TRAINID + " TEXT UNIQUE " + " , " + KEY_SHARE + " DEFAULT 'no'" + ");";//ADDED VIKALP PULL SERVICE

	private static final String DATABASE_CREATE_NEWS = "CREATE TABLE if not exists "
			+ SQLITE_NEWS
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_NAME
			+ ","
			+ KEY_contentExpiry
			+ ","
			+ KEY_FILELINK
			+ ","
			+ KEY_TYPE
			+ ","
			+ KEY_DETAIL
			+ ","
			+ KEY_LINK
			+ ","
			+ KEY_SUMMARY
			+ "," +
			// -------------------------------------------------------------------
			KEY_READ + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
//			KEY_NEWSID + " , " + KEY_SHARE + " DEFAULT 'no'" + ");";
			KEY_NEWSID +" TEXT UNIQUE " + " , " + KEY_SHARE + " DEFAULT 'no'" + ");"; //ADDED VIKALP PULL SERVICE

	private static final String DATABASE_CREATE_AWARD = "CREATE TABLE if not exists "
			+ SQLITE_AWARD
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_FILELINK
			+ ","
			+ KEY_NAME
			+ ","
			+ KEY_contentExpiry
			+ ","
			+ KEY_DATE //ADDED VIKALP
			+ "," 	   //ADDED VIKALP
			+ KEY_DETAIL
			+ ","
			+
			// -------------------------------------------------------------------
			KEY_AWARDID
//			+ " DEFAULT \'0\',"
			+ " TEXT UNIQUE ," //ADDED VIKALP PULL SERVICE
			+ KEY_READ
			+ " DEFAULT \'0\',"
			+
			// -------------------------------------------------------------------
			KEY_SUMMARY
			+ ","
			+ KEY_IMAGEPATH
			+ " , "
			+ KEY_SHARE
			+ " DEFAULT 'no'" + ");";

	/*
	 * private static final String DATABASE_CREATE_FEEDBACK =
	 * "CREATE TABLE if not exists " + SQLITE_FEEDBACK + "(" + KEY_ROWID +
	 * " integer PRIMARY KEY autoincrement," + KEY_FORMID + "," + ","+ KEY_TYPE
	 * + "," + KEY_TITLE + "," + "," + KEY_DETAIL + "," + KEY_SUMMARY + "," +
	 * KEY_QUESNO + "," + KEY_QUES + "," + KEY_OPTION1 + "," + KEY_OPTION2 + ","
	 * + KEY_OPTION3 + "," + KEY_OPTION4 +");";
	 */

	private static final String DATABASE_CREATE_FEEDBACK = "CREATE TABLE if not exists "
			+ SQLITE_FEEDBACK
			+ " ( "
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ " feedbackNo, feedbackTitle, feedbackDescription, feedbackAttempt,"
			+
			// -------------------------------------------------------------------
			KEY_READ
			+ " DEFAULT \'0\',"

			+ " feedbackTotalQuestions, feedbackDate, questionNo"
			+ ","
			+ KEY_contentExpiry
			+ " ,questionType, questionTitle, optionA, optionB, optionC, optionD, answerLimit, answer  DEFAULT \'\', attempts default\'0\' );";

	private static final String DATABASE_CREATE_UPLOAD = "Create Table if not exists Images ( "
			+ KEY_ROWID
			+ " integer primary key autoincrement, address, name, size);";

	private static final String DATABASE_CREATE_DOCUMENT = "CREATE TABLE if not exists "
			+ SQLITE_DOCUMENT
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement,"
			+ KEY_TITLE
			+ ","
			+ KEY_NAME
			+ ","
			+ KEY_DETAIL
			+ ","
			+ KEY_SUMMARY
			+ ","
			+ KEY_TYPE
			+ "," + KEY_ENCRYPTION + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
			KEY_READ + " DEFAULT \'0\'," +
			// -------------------------------------------------------------------
			KEY_TRAINID + ");";

	private static final String DATABASE_CREATE_LINKS = "CREATE TABLE if not exists "
			+ SQLITE_LINKS
			+ " ("
			+ KEY_ROWID
			+ " integer PRIMARY KEY autoincrement," + KEY_FILELINK + " text);";

	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			Log.v("Database", "table creation");
			db.execSQL(DATABASE_CREATE_ANNOUNCE);
			db.execSQL(DATABASE_CREATE_EVENT);
			db.execSQL(DATABASE_CREATE_TRAINING);
			db.execSQL(DATABASE_CREATE_NEWS);
			db.execSQL(DATABASE_CREATE_AWARD);
			db.execSQL(DATABASE_CREATE_FEEDBACK);
			db.execSQL(DATABASE_CREATE_DOCUMENT);
			db.execSQL(DATABASE_CREATE_UPLOAD);
			db.execSQL(DATABASE_CREATE_LINKS);
		}

		public static void deleteFolder(File folder) {
			File[] files = folder.listFiles();
			if (files != null) { // some JVMs return null for empty dirs
				for (File f : files) {
					if (f.isDirectory()) {
						deleteFolder(f);
					} else {
						f.delete();
					}
				}
			}
			folder.delete();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() +Constants.APP_FOLDER);
			deleteFolder(folder);

			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_EVENT);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_ANNOUNCE);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TRAINING);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_NEWS);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_AWARD);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_FEEDBACK);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_DOCUMENT);
			db.execSQL("DROP TABLE IF EXISTS " + SQLITE_LINKS);
			db.execSQL("DROP TABLE IF EXISTS Images");
			onCreate(db);
			// db.execSQL("Alter TABLE  " + SQLITE_EVENT+
			// " add column "+KEY_calenderEnabled+" default 'off' ;");
			// db.execSQL("Alter TABLE  " + SQLITE_EVENT+
			// " add column "+KEY_rsvpNeeded+" default 'off' ;");
			//SA VIKALP PULL SERVICE
			try {
				ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());//ADDED VIKALP PULL SERVICE NOTFICATION BULK STOP
				if (!ApplicationLoader.getPreferences().isPullAlarmService()) {
					ApplicationLoader.setAlarm();
				}
				if(TextUtils.isEmpty(ApplicationLoader.getPreferences().getInstallationDate())){
					ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());
				}
				Utilities.clearPreferences();
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
			//EA VIKALP PULL SERVICE
		}
	}

	public AnnounceDBAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public AnnounceDBAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	// for image upload module
	// _id, address, name, size

	public long createImage(String address) {

		String name = "";
		String size = "";

		try {
			File file = new File(address);
			long length = file.length();
			length = length / 1024;
			// System.out.println("File Path : " + file.getPath() +
			// ", File size : " + length +" KB");
			size = length + " Kb";
			Log.d("size", size);
			name = file.getName();

			Log.d("name", name);

		} catch (Exception e) {
			Log.e("Create Image", ("File not found : " + e.getMessage() + e));
		}

		ContentValues initialValues = new ContentValues();
		initialValues.put("address", address);
		initialValues.put("name", name);
		initialValues.put("size", size);

		return mDb.insert("Images", null, initialValues);
	}

	public void deleteImage(int _id) {
		String str = "Delete FROM Images where _id = \'" + _id + "\';";
		mDb.rawQuery(str, null);

	}

	public static void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) { // some JVMs return null for empty dirs
			for (File f : files) {
				if (f.isDirectory()) {
					deleteFolder(f);
				} else {
					f.delete();
				}
			}
		}
		folder.delete();
	}

	public void remotewipe(Context context) {
		Log.d("remotewipe", "called");
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/.mobcast/");
		deleteFolder(folder);
		try {
			// db.execSQL("delete from "+ TABLE_NAME);
			mDb.execSQL("Delete FROM " + SQLITE_EVENT);
			mDb.execSQL("Delete FROM " + SQLITE_ANNOUNCE);
			mDb.execSQL("Delete FROM " + SQLITE_TRAINING);
			mDb.execSQL("Delete FROM " + SQLITE_NEWS);
			mDb.execSQL("Delete FROM " + SQLITE_AWARD);
			mDb.execSQL("Delete FROM " + SQLITE_FEEDBACK);
			mDb.execSQL("Delete FROM " + SQLITE_DOCUMENT);
			mDb.execSQL("Delete FROM " + SQLITE_LINKS);
			mDb.execSQL("Delete FROM Images");
			Log.d("remotewipe", "finished");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void remoteWipeFolder(Context ctx){
		Log.d("remoteWipeFolder", "called");
		try{
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER);
			deleteFolder(folder);
		}catch(Exception e){
			Log.i("remoteWipeFolder", e.toString());
		}
	}
	
	// function to delete expired content and get count of unread mobcasts
	public int getUnreadCount(String TableName) {

		// mDb.rawQuery(str, null);
		/*
		 * String countQuery = "SELECT  * FROM " + TABLE_NAME; SQLiteDatabase db
		 * = this.getReadableDatabase(); Cursor cursor = db.rawQuery(countQuery,
		 * null); int cnt = cursor.getCount(); cursor.close(); return cnt;
		 */
		int cnt = 0;
		if (!TableName.equals("Feedback")) {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String currentDateandTime = sdf.format(Calendar.getInstance()
					.getTime());

			String countQuery1 = "Delete FROM " + TableName + " where "
					+ KEY_contentExpiry + " < '" + currentDateandTime + "';";
			Cursor cursor1 = mDb.rawQuery(countQuery1, null);
			Log.e("courser delete count " + TableName, cursor1.getCount() + "");
			cursor1.close();

			String countQuery = "SELECT  * FROM " + TableName + " where "
					+ KEY_READ + " = '0';";
			Cursor cursor = mDb.rawQuery(countQuery, null);
			cnt = cursor.getCount();
			cursor.close();

		}

		else {

			String countQuery = "SELECT * FROM feedback where " + KEY_READ
					+ " = '0' GROUP BY feedbackNo;";
			Cursor cursor = mDb.rawQuery(countQuery, null);
			cnt = cursor.getCount();
			cursor.close();

		}

		return cnt;
	}

	public Cursor fetchAllImages() {
		String str = "SELECT * FROM Images ORDER BY _id DESC;";

		Cursor mCursor = mDb.rawQuery(str, null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		Log.e("Images New List", mCursor.getCount() + "");
		return mCursor;

	}

	// image upload module closes here

	public long addFeedback(FeedbackDBHandler feedback, String ContentExpiry) {
		ContentValues initialValues = new ContentValues();
		initialValues.put("feedbackNo", feedback.getFeedbackNo());
		initialValues.put("feedbackTitle", feedback.getFeedbacktitle());
		initialValues.put("feedbackDescription",
				feedback.getFeedbackDescription());
		initialValues.put("feedbackAttempt", feedback.getFeedbackAttempt());
		initialValues.put("feedbackTotalQuestions",
				feedback.getFeedbackTotalQuestions());
		initialValues.put("feedbackDate", feedback.getFeedbackDate());
		initialValues.put("questionNo", feedback.getQuestionNo());
		initialValues.put("questionType", feedback.getQuestionType());
		initialValues.put("questionTitle", feedback.getQuestionTitle());
		initialValues.put("optionA", feedback.getOptionA());
		initialValues.put("optionB", feedback.getOptionB());
		initialValues.put("optionC", feedback.getOptionC());
		initialValues.put(KEY_contentExpiry, ContentExpiry);
		initialValues.put("optionD", feedback.getOptionD());
		initialValues.put("answerLimit", feedback.getAnswerLimit());

		long value = mDb.insert("Feedback", null, initialValues);

		Log.v("CreateFeedback", "Returned value is: " + value);
		return value;
		// return 0;
	}

	public void unreadrow(String id, String TableName) {
		try {

			if (TableName != AnnounceDBAdapter.SQLITE_FEEDBACK) {
				mDb.execSQL("UPDATE " + TableName + " SET " + KEY_READ
						+ " = '0' WHERE _id= '" + Integer.parseInt(id) + "' ;");
			} else {
				String que = "UPDATE " + TableName + " SET " + KEY_READ
						+ " = '0' WHERE feedbackNo = '" + Integer.parseInt(id)
						+ "' ;";
				Log.d("query", que);
				mDb.execSQL(que);
			}
			Log.v("Set unread for " + TableName, "row id " + id);

		} catch (Exception e) {
			Log.v("Error : ", e.getMessage());
			Log.v("isread", "ERROR");
		}

	}

	public void finishedDownload(String id, String TableName, String ename) {
		try {

			mDb.execSQL("UPDATE " + TableName + " SET " + KEY_FILELINK
					+ " = '0' WHERE _id= '" + Integer.parseInt(id) + "' ;");

			if (TableName.contains("training")) {
				mDb.execSQL("UPDATE " + TableName + " SET " + KEY_ENCRYPTION
						+ " = '0' WHERE _id= '" + Integer.parseInt(id) + "' ;");
			}
			Log.v("Set link 0 for " + TableName, "row id " + id);

		} catch (Exception e) {
			Log.v("Error : ", e.getMessage());
			Log.v("isread", "ERROR");
		}

	}

	public void readrow(String id, String TableName) {
		try {

			if (TableName != AnnounceDBAdapter.SQLITE_FEEDBACK)
				mDb.execSQL("UPDATE " + TableName + " SET " + KEY_READ
						+ " = '1' WHERE _id= '" + Integer.parseInt(id) + "' ;");
			else {
				String que = "UPDATE " + TableName + " SET " + KEY_READ
						+ " = '1' WHERE feedbackNo = '" + Integer.parseInt(id)
						+ "' ;";
				mDb.execSQL(que);
				Log.d("query", que);
				// ContentValues initialValues = new ContentValues();
				// initialValues.put(KEY_READ, "1");

				// mDb.update(SQLITE_FEEDBACK,initialValues,
				// "where feedbackNo = "+id, null);

			}
			// Log.v("Set read for "+TableName, "row id "+id);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public int isReadFeedback(String id) {
		int i = 0;
		try {

			Cursor cursor = mDb.query(SQLITE_FEEDBACK,
					new String[] { KEY_READ }, "feedbackNo=?",
					new String[] { id }, null, null, null);
			Log.d("Cursor read id:", String.valueOf(cursor));
			if (cursor != null)
				cursor.moveToFirst();

			i = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_READ));
			Log.d("Read or not", String.valueOf(i));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return i;
	}

	public void deleterow(String id, String TableName) {
		try {

			if (TableName != AnnounceDBAdapter.SQLITE_FEEDBACK) {
				mDb.execSQL("DELETE from " + TableName + " WHERE _id="
						+ Integer.parseInt(id));
			} else {
				mDb.execSQL("DELETE FROM " + SQLITE_FEEDBACK
						+ " WHERE feedbackNo = '" + id + "';");
			}
			Log.v("Deleted for " + TableName, "row id " + id);

		} catch (Exception e) {
			Log.v("Error : ", e.getMessage());
			Log.v("isread", "ERROR");
		}

	}

	public long createLink(String id, String link) {

		Log.d("Link", "method creating link");
		ContentValues cv = new ContentValues();
		cv.put("ID", id);
		cv.put("Link", link);
		return mDb.insert(SQLITE_LINKS, null, cv);

	}

	public long createAnnouncement(String title, String detail, String from,
			String type, String name, String summary, String id, String Social,
			String contentExpiry, String fileLink) {

		from = from.replaceAll("\\\\", "");
		title = title.replaceAll("\\\\", "");
		summary = summary.replaceAll("\\\\", "");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_FROM, from);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_contentExpiry, contentExpiry);
		initialValues.put(KEY_FILELINK, fileLink);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_ANNOUNCEID, id);
		initialValues.put(KEY_SHARE, Social);

		System.out.println("title  ===> " + title);
		System.out.println("fro  ===> " + from);
		System.out.println("summary  ===> " + summary);
		System.out.println("detail  ===> " + detail);
		System.out.println("type  ===> " + type);
		System.out.println("id  ===> " + id);
		System.out.println("social  ===> " + Social);

		System.out.println("name  ===> " + name);
		System.out.println("link  ===> " + fileLink);

		return mDb.insert("Announce", null, initialValues);
	}

	public long createAward(String title, String name, String detail, String rdate,
			String award_id, String summary, String imagePath, String Social,
			String contentExpiry, String fileLink) {
		name = name.replaceAll("\\\\", "");
		title = title.replaceAll("\\\\", "");
		summary = summary.replaceAll("\\\\", "");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_DATE, rdate); //ADDED VIKALP AWARD RDATE
		initialValues.put(KEY_AWARDID, award_id);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_FILELINK, fileLink);
		initialValues.put(KEY_contentExpiry, contentExpiry);
		initialValues.put(KEY_IMAGEPATH, imagePath);
		initialValues.put(KEY_SHARE, Social);
		Log.v("create award " + KEY_AWARDID, title + "\n" + name + "\n"
				+ detail + "\n" + rdate  + "\n" + summary + "\n" + imagePath); //ADDED VIKALP AWARD RDATE
		long value = mDb.insert("Award", null, initialValues);
		Log.v("CreateAward", "Returned value is: " + value);
		return value;

	}

	public long createFeedback(String formId, String type, String title,
			String detail, String summary, String quesNo, String ques,
			String option1, String option2, String option3, String option4) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_FORMID, formId);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_QUESNO, quesNo);
		initialValues.put(KEY_QUES, ques);
		initialValues.put(KEY_OPTION1, option1);
		initialValues.put(KEY_OPTION2, option2);
		initialValues.put(KEY_OPTION3, option3);
		initialValues.put(KEY_OPTION4, option4);
		long value = mDb.insert("Feedback", null, initialValues);
		Log.v("CreateFeedback", "Returned value is: " + value);
		return value;

	}

	public void eventRsvp(String res, String pos) {
		int posi = Integer.parseInt(pos);
		mDb.execSQL("UPDATE Event SET rsvp = '" + res + "' WHERE eid = '"
				+ posi + "';");
	}

	// TODO
	public void eventDetails(String details, String pos) {
		int posi = Integer.parseInt(pos);
		mDb.execSQL("UPDATE Event SET Rtime = '" + details + "' WHERE eid = '"
				+ posi + "';");
	}

	public long createEvent(String title, String date, String day, String time,
			String venue, String summary, String id, String Social,
			String contentExpiry, String rsvpEnabled, String key_calederEnabled,String endTime) {
		// TODO
		venue = venue.replaceAll("\\\\", "");
		title = title.replaceAll("\\\\", "");
		summary = summary.replaceAll("\\\\", "");

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_DATE, date);
		initialValues.put(KEY_DAY, day);
		initialValues.put(KEY_TIME, time);
		initialValues.put(KEY_VENUE, venue);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_EVENTID, id);
		initialValues.put(KEY_RSVP, "3");
		initialValues.put(KEY_rsvpNeeded, rsvpEnabled);
		initialValues.put(KEY_calenderEnabled, key_calederEnabled);
		initialValues.put(KEY_contentExpiry, contentExpiry);
		initialValues.put(KEY_SHARE, Social);
		initialValues.put(KEY_ENDTIME, endTime); //ADDED VIKALP EVENT END TIME
		return mDb.insert("Event", null, initialValues);
	}

	public long createTraining(String title, String detail, String name,
			String summary, String type, String ename, String id,
			String Social, String contentExpiry, String fileLink) {

		name = name.replaceAll("\\\\", "");
		title = title.replaceAll("\\\\", "");
		summary = summary.replaceAll("\\\\", "");

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_ENCRYPTION, ename);
		initialValues.put(KEY_FILELINK, fileLink);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_contentExpiry, contentExpiry);
		initialValues.put(KEY_SHARE, Social);
		initialValues.put(KEY_TRAINID, id);
		return mDb.insert("Training", null, initialValues);
	}

	public long createDocument(String title, String detail, String name,
			String summary, String type, String ename, String id) {
		Log.v("document database", "in createDocument");
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_ENCRYPTION, ename);
		initialValues.put(KEY_TRAINID, id);
		return mDb.insert("Document", null, initialValues);
	}

	public long createNews(String title, String detail, String name,
			String link, String type, String summary, String id, String Social,
			String contentExpiry, String fileLink) {

		title = title.replaceAll("\\\\", "");
		summary = summary.replaceAll("\\\\", "");

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_DETAIL, detail);
		initialValues.put(KEY_LINK, link);
		initialValues.put(KEY_SUMMARY, summary);
		initialValues.put(KEY_FILELINK, fileLink);
		initialValues.put(KEY_contentExpiry, contentExpiry);
		initialValues.put(KEY_TYPE, type);
		initialValues.put(KEY_NEWSID, id);
		initialValues.put(KEY_SHARE, Social);
		return mDb.insert("News", null, initialValues);
	}

	public void createFlag(String going, String pos) {
		mDb.execSQL("UPDATE Event SET going = 'yes' WHERE _id = '" + pos + "';");
	}

	public boolean deleteAllCountries() {

		int doneDelete = 0;
		doneDelete = mDb.delete(SQLITE_EVENT, null, null);
		Log.w(TAG, Integer.toString(doneDelete));
		return doneDelete > 0;

	}

	// for announcements
	public Cursor getRowPrivious(int _id, String TableName) {
		try {
			Cursor cursor =

			mDb.query(TableName, new String[] { KEY_ROWID, KEY_DETAIL,
					KEY_FROM, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
					KEY_ANNOUNCEID, KEY_READ }, " _id >'" + _id + "'", null,
					null, null, KEY_ROWID);

			return cursor;
		} catch (Exception e) {
			Log.v("get row next", "wrong query");
		}
		return null;
	}

	public Cursor getRowNext(int _id, String TableName) {
		try {
			Cursor cursor =

			mDb.query(TableName, new String[] { KEY_ROWID, KEY_DETAIL,
					KEY_FROM, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
					KEY_ANNOUNCEID, KEY_READ }, " _id <'" + _id + "'", null,
					null, null, KEY_ROWID + " DESC");

			return cursor;
		} catch (Exception e) {
			Log.v("get row next", "wrong query");
		}
		return null;

	}

	public Cursor getRowPriviousDocuments(int _id) {
		try {
			Cursor mCursor = mDb.query(SQLITE_DOCUMENT, new String[] {
					KEY_ROWID, KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE,
					KEY_TRAINID, KEY_NAME, KEY_READ }, " _id >'" + _id + "'",
					null, null, null, KEY_ROWID);

			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Cursor getRowNextDocuments(int _id) {
		try {
			Cursor mCursor = mDb.query(SQLITE_DOCUMENT, new String[] {
					KEY_ROWID, KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE,
					KEY_TRAINID, KEY_NAME, KEY_READ }, " _id <'" + _id + "'",
					null, null, null, KEY_ROWID);

			if (mCursor != null) {
				mCursor.moveToFirst();
			}
			return mCursor;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// for events
	public Cursor getRowPriviousEvents(int _id, String TableName) {
		try {// Cursor cursor=

			// mDb.query(TableName, new String[] {KEY_ROWID, KEY_DETAIL ,
			// KEY_FROM ,
			// KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY , KEY_ANNOUNCEID,
			// KEY_READ},
			// " _id >'"+_id+"'", null, null, null, KEY_ROWID );

			Cursor cursor = mDb.query(TableName, new String[] { KEY_ROWID,
					KEY_DATE, KEY_DAY, KEY_TITLE, KEY_TIME, KEY_VENUE,
					KEY_SUMMARY, KEY_EVENTID, KEY_RSVP, KEY_READ, "Rtime" },
					" _id >'" + _id + "'", null, null, null, KEY_ROWID);

			if (cursor != null) {
				cursor.moveToFirst();
			}
			return cursor;

		} catch (Exception e) {
			Log.v("get row next", "wrong query");
		}
		return null;
	}

	public Cursor getRowNextEvents(int _id, String TableName) {
		try {// Cursor cursor=

			// mDb.query(TableName, new String[] {KEY_ROWID, KEY_DETAIL ,
			// KEY_FROM ,
			// KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY , KEY_ANNOUNCEID,
			// KEY_READ},
			// " _id <'"+_id+"'", null, null, null, KEY_ROWID + " DESC");

			Cursor cursor = mDb.query(SQLITE_EVENT, new String[] { KEY_ROWID,
					KEY_DATE, KEY_DAY, KEY_TITLE, KEY_TIME, KEY_VENUE,
					KEY_SUMMARY, KEY_EVENTID, KEY_RSVP, KEY_READ, "Rtime" },
					" _id <'" + _id + "'", null, null, null, KEY_ROWID
							+ " DESC");

			if (cursor != null) {
				cursor.moveToFirst();
			}
			return cursor;

		} catch (Exception e) {
			Log.v("get row next", "wrong query");
		}
		return null;

	}

	public Cursor getRowPriviousNews(int _id) {
		Cursor mCursor = mDb.query(SQLITE_NEWS, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
				KEY_LINK, KEY_READ, KEY_NEWSID }, " _id >'" + _id + "'", null,
				null, null, KEY_ROWID);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getRowNextNews(int _id) {

		Cursor mCursor = mDb.query(SQLITE_NEWS, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
				KEY_LINK, KEY_READ, KEY_NEWSID }, " _id <'" + _id + "'", null,
				null, null, KEY_ROWID + " DESC");

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor getRowPriviousAward(int _id) {
		Cursor mCursor = mDb.query(SQLITE_AWARD, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_NAME, KEY_DETAIL, KEY_AWARDID, KEY_SUMMARY,
				KEY_READ, KEY_IMAGEPATH }, " _id >'" + _id + "'", null, null,
				null, KEY_ROWID);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public Cursor getRowNextAward(int _id) {
		Cursor mCursor = mDb.query(SQLITE_AWARD, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_NAME, KEY_DETAIL, KEY_AWARDID, KEY_SUMMARY,
				KEY_READ, KEY_IMAGEPATH }, " _id <'" + _id + "'", null, null,
				null, KEY_ROWID + " DESC");
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public boolean isLastFeedbackQuestion(int _id, int feedbackId) {

		String str = "SELECT _id FROM feedback where feedbackNo = \'"
				+ feedbackId + "\';";
		Cursor c = mDb.rawQuery(str, null);

		if (c != null) {
			c.moveToLast();
		}

		if (_id == Integer
				.parseInt(c.getString(c.getColumnIndexOrThrow("_id"))))
			return true;
		else
			return false;

	}

	public boolean isFirstFeedbackQuestion(int _id, int feedbackId) {
		String str = "SELECT _id FROM feedback where feedbackNo = \'"
				+ feedbackId + "\';";
		Cursor c = mDb.rawQuery(str, null);

		if (c != null) {
			c.moveToFirst();
		}

		if (_id == Integer
				.parseInt(c.getString(c.getColumnIndexOrThrow("_id"))))
			return true;
		else
			return false;

	}

	public Cursor back(int _id) {
		String str = "SELECT * FROM feedback where _id = \'" + _id + "\';";
		Log.e("query", str);
		Cursor mCursor = mDb.rawQuery(str, null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		Log.e("Feedback New List", mCursor.getCount() + "");
		return mCursor;
	}

	public void deleteFeedback(int feedbackNo) {
		mDb.delete("feedback", "feedbackNo=\'" + feedbackNo + "\'", null);
	}

	public Cursor fetchAllFeedbackForAnswer(String feedbackNo) {
		String str = "SELECT * FROM feedback where feedbackNo = \'"
				+ feedbackNo + "\';";

		Cursor mCursor = mDb.rawQuery(str, null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		Log.e("Feedback New List", mCursor.getCount() + "");
		return mCursor;

	}

	public String getlink(String Tablename, String _id) {
		Cursor c = mDb.rawQuery("select * from " + Tablename
				+ " where _id = \'" + _id + "\' ;", null);
		if (c != null) {
			c.moveToFirst();
			return c.getString((c.getColumnIndexOrThrow(KEY_FILELINK)));
		}
		return "null";
	}

	public Cursor getrow(String Tablename, String _id) {
		Cursor c = mDb.rawQuery("select * from " + Tablename
				+ " where _id = \'" + _id + "\' ;", null);
		if (c != null) {
			c.moveToFirst();
			return c;
		}
		return null;
	}

	public Cursor fetchAllFeedbackGroup(String feedbackno) {

		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + SQLITE_FEEDBACK
				+ " WHERE feedbackNo = '" + feedbackno + "';", null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		Log.e("Feedback New List", mCursor.getCount() + "");
		return mCursor;
	}

	public Cursor fetchAllFeedbackForListView() {

//		SU VIKALP FEEDBACK DATE ORDER
//		Cursor mCursor = mDb.rawQuery(
//				"SELECT * FROM feedback GROUP BY feedbackNo ORDER BY "
//						+ "feedbackNo" + " DESC;", null);
//		
		Cursor mCursor = mDb.rawQuery(
				"SELECT * FROM feedback GROUP BY feedbackNo ORDER BY "
						+ "feedbackDate" + " DESC" + " ,"+ "feedbackNo" + " DESC;", null);
//		EU VIKALP FEEDBACK DATE ORDER

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		Log.e("Feedback New List", mCursor.getCount() + "");
		return mCursor;
	}

	public Cursor fetchAll(String Tablename, String _id) {

		Cursor mCursor = mDb.rawQuery("SELECT * FROM " + Tablename
				+ " where _id = \'" + _id + "\' ;", null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}

		return mCursor;
	}

	// for subjective and rating
	public void answerFeedbackSubjective(String answer, int _id) {
		// Log.v("query",
		// "UPDATE feedback SET answer=\'"+answer+"\' WHERE _id=\'"+_id+"\';");
		// mDb.rawQuery("UPDATE feedback SET answer=\'"+answer+"\' WHERE _id=\'"+_id+"\';",
		// null);

		ContentValues updateCountry = new ContentValues();
		updateCountry.put("answer", answer);
		mDb.update("feedback", updateCountry, "_id=\'" + _id + "\'", null);

	}

	public void updateFeedbackCount(int feedbackNo) {
		// Log.v("query",
		// "UPDATE feedback SET answer=\'"+answer+"\' WHERE _id=\'"+_id+"\';");
		// mDb.rawQuery("UPDATE feedback SET answer=\'"+answer+"\' WHERE _id=\'"+_id+"\';",
		// null);

		Cursor c = mDb.rawQuery("select * from feedback where feedbackNo=\'"
				+ feedbackNo + "\' ;", null);

		if (c != null) {
			c.moveToFirst();
		}
		Log.v("Cursor count", "" + c.getCount());
		int attempts = Integer.parseInt(c.getString(c
				.getColumnIndexOrThrow("attempts")));
		Log.v("attempts", "" + attempts);
		attempts++;
		ContentValues updateCountry = new ContentValues();
		updateCountry.put("attempts", attempts);
		Log.v("rows affected",
				""
						+ mDb.update("feedback", updateCountry, "feedbackNo=\'"
								+ feedbackNo + "\'", null));

	}

	public Cursor fetchItem(String tablename, String _id) {
		System.out.print("inside announce db adapter ");
		Log.d("_id in announcedbadapter", _id);
		/*
		 * Cursor mCursor = mDb.rawQuery("Select * from " + tablename +
		 * " where _id = '" + _id + "';", null);
		 */
		Cursor mCursor = mDb.query(tablename, null, KEY_ROWID + " = ?",
				new String[] { _id }, null, null, null);
		return mCursor;
	}

	public Cursor fetchAllAnnouncements() {

		// SU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_ANNOUNCE, new String[] { KEY_ROWID,
		// KEY_DETAIL, KEY_FROM, KEY_TITLE, KEY_TYPE, KEY_NAME,
		// KEY_SUMMARY, KEY_ANNOUNCEID, KEY_READ, KEY_SHARE,
		// KEY_contentExpiry, KEY_FILELINK }, null, null, null, null,
		// KEY_ROWID + " DESC");

		Cursor mCursor = mDb.query(SQLITE_ANNOUNCE, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_FROM, KEY_TITLE, KEY_TYPE, KEY_NAME,
				KEY_SUMMARY, KEY_ANNOUNCEID, KEY_READ, KEY_SHARE,
				KEY_contentExpiry, KEY_FILELINK }, null, null, null, null,
				KEY_DETAIL + " DESC" + " ," + KEY_ROWID + " DESC");
		// EU VIKALP
		if (mCursor != null) {
			mCursor.moveToFirst();

		}
		return mCursor;
	}
	
	public void expireAnnouncements() {
		try {
			// mDb.delete(SQLITE_ANNOUNCE, KEY_contentExpiry, new
			// String[]{Utilities.getTodayDate()});
			mDb.delete(SQLITE_ANNOUNCE,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor fetchAllNews() {
		Log.v("news", "in fetch all news");
		// SU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_NEWS, new String[] { KEY_ROWID,
		// KEY_DETAIL, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
		// KEY_LINK, KEY_READ, KEY_NEWSID, KEY_SHARE, KEY_contentExpiry,
		// KEY_FILELINK }, null, null, null, null, KEY_ROWID + " DESC");

		Cursor mCursor = mDb.query(SQLITE_NEWS, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_TITLE, KEY_TYPE, KEY_NAME, KEY_SUMMARY,
				KEY_LINK, KEY_READ, KEY_NEWSID, KEY_SHARE, KEY_contentExpiry,
				KEY_FILELINK }, null, null, null, null, KEY_DETAIL + " DESC"
				+ " ," + KEY_ROWID + " DESC");

		// EU VIKALP
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void expireNews() {
		try {
			mDb.delete(SQLITE_NEWS,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor fetchAllEvents() {

		Cursor mCursor = mDb.query(SQLITE_EVENT, new String[] { KEY_ROWID,
				KEY_DATE, KEY_DAY, KEY_TITLE, KEY_TIME, KEY_VENUE, KEY_SUMMARY,
				KEY_EVENTID, KEY_RSVP, KEY_SHARE, KEY_READ, "Rtime",
				KEY_contentExpiry, KEY_rsvpNeeded, KEY_calenderEnabled }, null,
				null, null, null, KEY_ROWID + " DESC");

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void expireEvents() {
		try {
			mDb.delete(SQLITE_EVENT,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor fetchAllTraining() {

		// EU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_TRAINING, new String[] { KEY_ROWID,
		// KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE, KEY_TRAINID,
		// KEY_READ, KEY_NAME, KEY_ENCRYPTION, KEY_SHARE,
		// KEY_contentExpiry }, null, null, null, null, KEY_ROWID
		// + " DESC");

		Cursor mCursor = mDb.query(SQLITE_TRAINING, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE, KEY_TRAINID,
				KEY_READ, KEY_NAME, KEY_ENCRYPTION, KEY_SHARE,
				KEY_contentExpiry }, null, null, null, null, KEY_DETAIL
				+ " DESC" + " ," + KEY_ROWID + " DESC");
		// SU VIKALP
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public void expireTraining() {
		try {
			mDb.delete(SQLITE_TRAINING,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor Search(String table_name, String filter) {
		Log.v("inside", "AnnounceDBAdapter");
		Cursor mCursor = null;

		if (table_name.equals("Feedback")) {
			mCursor = mDb.rawQuery("Select * from '" + table_name + "' where "
					+ "feedbackTitle" + " like '%" + filter + "%'  or "
					+ "feedbackDescription" + " like '%" + filter
					+ "%' GROUP BY feedbackNo order by " + KEY_ROWID
					+ " DESC ;", null);
		} else if (table_name.equals("Announce")) { // SA VIKALP
			mCursor = mDb.rawQuery("Select * from '" + table_name + "' where "
					+ KEY_TITLE + " like '%" + filter + "%' or " + KEY_FROM
					+ " like '%" + filter + "%' or " + KEY_SUMMARY + " like '%"
					+ filter + "%' order by " + KEY_ROWID + " DESC ;", null);
		} else { // EA VIKALP
			mCursor = mDb.rawQuery("Select * from '" + table_name + "' where "
					+ KEY_TITLE + " like '%" + filter + "%'  or " + KEY_SUMMARY
					+ " like '%" + filter + "%' order by " + KEY_ROWID
					+ " DESC ;", null);
		}
		Log.v("mcursor has", mCursor.getCount() + " elements");
		Log.v("exitting", "AnnounceDBAdapter");
		return mCursor;
	}

	public Cursor fetchAllDocument() {

		// SU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_DOCUMENT, new String[] { KEY_ROWID,
		// KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE, KEY_TRAINID,
		// KEY_NAME, KEY_READ, KEY_ENCRYPTION }, null, null, null, null,
		// KEY_ROWID + " DESC");

		Cursor mCursor = mDb.query(SQLITE_DOCUMENT, new String[] { KEY_ROWID,
				KEY_DETAIL, KEY_TITLE, KEY_SUMMARY, KEY_TYPE, KEY_TRAINID,
				KEY_NAME, KEY_READ, KEY_ENCRYPTION }, null, null, null, null,
				KEY_DETAIL + " DESC" + " ," + KEY_ROWID + " DESC");
		// EU VIKALP
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchAllAwards() {
		Log.v("award", "in fetch all awards");

		// SU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_AWARD, new String[] { KEY_ROWID,
		// KEY_TITLE, KEY_NAME, KEY_DETAIL, KEY_AWARDID, KEY_SUMMARY,
		// KEY_READ, KEY_IMAGEPATH, KEY_SHARE, KEY_contentExpiry,
		// KEY_FILELINK }, null, null, null, null, KEY_ROWID + " DESC");

		Cursor mCursor = mDb.query(SQLITE_AWARD, new String[] { KEY_ROWID,
				KEY_TITLE, KEY_NAME, KEY_DETAIL, KEY_AWARDID, KEY_SUMMARY,
				KEY_READ, KEY_IMAGEPATH, KEY_SHARE, KEY_contentExpiry,KEY_DATE,
				KEY_FILELINK }, null, null, null, null, KEY_DATE + " DESC"
				+ " ," + KEY_ROWID + " DESC");
		// EU VIKALP
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public void expireAwards() {
		try {
			mDb.delete(SQLITE_AWARD,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor fetchAllFeedback() {
		Log.v("feedback", "in fetch all feedback");
		// SU VIKALP
		// Cursor mCursor = mDb.query(SQLITE_FEEDBACK, new String[] { KEY_ROWID,
		// KEY_FORMID, KEY_TYPE, KEY_TITLE, KEY_DETAIL, KEY_SUMMARY,
		// KEY_QUESNO, KEY_QUES, KEY_OPTION1, KEY_OPTION2, KEY_OPTION3,
		// KEY_OPTION4, KEY_contentExpiry }, null, null, null, null,
		// KEY_ROWID + " DESC");

		Cursor mCursor = mDb.query(SQLITE_FEEDBACK, new String[] { KEY_ROWID,
				KEY_FORMID, KEY_TYPE, KEY_TITLE, KEY_DETAIL, KEY_SUMMARY,
				KEY_QUESNO, KEY_QUES, KEY_OPTION1, KEY_OPTION2, KEY_OPTION3,
				KEY_OPTION4, KEY_contentExpiry }, null, null, null, null,
				KEY_DETAIL + " DESC" + " ," + KEY_ROWID + " DESC");
		// EU VIKALP
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public void expireFeedback() {
		try {
			mDb.delete(SQLITE_FEEDBACK,
					"contentExpiry=\'" + Utilities.getTodayDate() + "\'", null);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public Cursor fetchFeedback(int i) {
		// TODO Auto-generated method stub
		Log.v("feedback", "Fetching feedback by Id");
		Cursor mCursor = mDb.query(SQLITE_FEEDBACK, null, KEY_ROWID + " =?",
				new String[] { String.valueOf(i) }, null, null, null);
		if (mCursor != null)
			mCursor.moveToFirst();
		return mCursor;
	}

	public Cursor fetchLink(String id) {

		Log.v("link", "Fetching link");
		Cursor mCursor = mDb.query(SQLITE_LINKS, new String[] { KEY_FILELINK },
				id + "=?", new String[] { id }, null, null, null);

		if (mCursor != null)
			mCursor.moveToFirst();

		return mCursor;
	}

}
