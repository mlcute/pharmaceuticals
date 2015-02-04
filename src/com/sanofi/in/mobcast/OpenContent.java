package com.sanofi.in.mobcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sanofi.in.mobcast.R;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class OpenContent {

	Context context;
	AnnounceDBAdapter adb;
	String Tablename;
	String _id;

	public OpenContent(Context c, String tablename, String _id) {
		this.Tablename = tablename;
		this.context = c;
		this._id = _id;
		Log.d("_id", _id);
		adb = new AnnounceDBAdapter(c);

	}

	public Intent itemView() {

		int position = Integer.parseInt(_id);
		Log.d("ID selected is: ", _id);

		Cursor cursor = null, cursor1 = null;
		adb.open();
		if (Tablename != AnnounceDBAdapter.SQLITE_FEEDBACK) {
			cursor = (Cursor) adb.fetchItem(Tablename, _id);
			Log.d("Cursor count 1: ", String.valueOf(cursor.getCount()));
			/*
			 * cursor1 = (Cursor)adb.fetchLink(_id); Log.d("Cursor1 id",
			 * String.valueOf(cursor1)+" "+_id);
			 */
		} else {
			cursor = (Cursor) adb.fetchAllFeedbackGroup(_id);
			Log.d("Cursor count 2: ", String.valueOf(cursor.getCount()));
		}

		if (cursor != null) {
			cursor.moveToFirst();

			String share = "off";

			if (Tablename.equals("Announce")) {
				// Get the cursor, positioned to the corresponding row in the
				// result
				// set

				// Toast.makeText(getBaseContext(), ""+position,
				// Toast.LENGTH_LONG).show();
				Log.v("Position in listview", "" + position);

				// Get the state's capital from this row in the database.
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				// SU VIKALP DATE ORDER ISSUE
				// String detail = cursor.getString(cursor
				// .getColumnIndexOrThrow("detail"));
				String detail = Utilities.convertDateToIndian(cursor
						.getString(cursor.getColumnIndexOrThrow("detail")));
				// EU VIKALP DATE ORDER ISSUE
				String from = cursor.getString(cursor
						.getColumnIndexOrThrow("fro"));
				String type = cursor.getString(cursor
						.getColumnIndexOrThrow("type"));
				share = cursor.getString(cursor
						.getColumnIndexOrThrow("shareKey"));
				String link = cursor.getString(cursor
						.getColumnIndexOrThrow("fileLink"));

				String name = cursor.getString(cursor
						.getColumnIndexOrThrow("name"));
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("summary"));
				String aid = cursor.getString(cursor
						.getColumnIndexOrThrow("aid"));
				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));
				cursor.close();
				adb.close();

				Intent show;
				if (type.contentEquals("image")) {
					show = new Intent(context, Announcements.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("summary", summary);
					show.putExtra("share", share);
					show.putExtra("id", aid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				} else if (type.contentEquals("video")) {
					show = new Intent(context, AnnounceVideo.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("share", share);
					show.putExtra("summary", summary);
					show.putExtra("id", aid);
					show.putExtra("_id", _id);
					// File file = new
					// File(Environment.getExternalStorageDirectory().getAbsolutePath()
					// + "/.mobcast/mobcast_videos/"+name);
					// show = new Intent(Intent.ACTION_VIEW);
					// show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					// show.setAction(Intent.ACTION_VIEW);
					// show.setDataAndType(Uri.fromFile(file), "video/*");

					// context.startActivity(show);
					return show;
				} else if (type.equals("audio"))

				{

					/*
					 * File file = new
					 * File(Environment.getExternalStorageDirectory()
					 * .getAbsolutePath() + "/.mobcast/mobcast_audio/"+name);
					 * show = new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file), "audio/mp3");
					 * NotificationManager mNotificationManager =
					 * (NotificationManager)
					 * getSystemService(Context.NOTIFICATION_SERVICE);
					 * mNotificationManager.cancelAll();
					 * context.startActivity(show);
					 */

					show = new Intent(context, audio.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("from", from);
					show.putExtra("share", share);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("summary", summary);
					show.putExtra("id", aid);
					show.putExtra("_id", _id);

					// context.startActivity(show);
					return show;

				}// end of audio
				else {
					show = new Intent(context, AnnounceText.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("share", share);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", aid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				}

			}
			// end of table announce

			else if (Tablename.equals("Event")) {

				// Get the cursor, positioned to the corresponding row in the
				// result
				// set

				String endTime = "";
				// Get the state's capital from this row in the database.
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				String date = cursor.getString(cursor
						.getColumnIndexOrThrow("date"));
				String day = cursor.getString(cursor
						.getColumnIndexOrThrow("day"));
				String time = cursor.getString(cursor
						.getColumnIndexOrThrow("time"));
				String venue = cursor.getString(cursor
						.getColumnIndexOrThrow("venue"));
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("summary"));
				String rsvp = cursor.getString(cursor
						.getColumnIndexOrThrow("rsvp"));
				String eid = cursor.getString(cursor
						.getColumnIndexOrThrow("eid"));
				share = cursor.getString(cursor
						.getColumnIndexOrThrow("shareKey"));
				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));
				String calenderEnabled = cursor.getString(cursor
						.getColumnIndexOrThrow("calenderEnabled"));
				String rsvpNeeded = cursor.getString(cursor
						.getColumnIndexOrThrow("rsvpNeeded"));
				// SA ADDED VIKALP EVENT END TIME
				endTime = cursor.getString(cursor
						.getColumnIndexOrThrow("endTime"));
				if(TextUtils.isEmpty(endTime)){
					endTime = "";
				}
				// EA ADDED VIKALP EVENT END TIME

				cursor.close();
				adb.close();
				Log.i("title", title);
				Log.i("date", date);
				Log.i("day", day);
				Log.i("time", time);
				Log.i("venue", venue);
				Log.i("summary", summary);
				Log.i("rsvp", rsvp);
				Log.i("eid", eid);
				Log.i("endTime", endTime);// ADDED VIKALP EVENT END TIME
				Log.i("share", share);
				Log.i("_id", _id);
				Log.i("calenderEnabled", calenderEnabled);
				Log.i("rsvpNeeded", rsvpNeeded);

				Intent show = new Intent(context, Event.class);
				show.putExtra("title", title);
				show.putExtra("date", date);
				show.putExtra("day", day);
				show.putExtra("time", time);
				show.putExtra("rsvp", rsvp);
				show.putExtra("venue", venue);
				show.putExtra("summary", summary);
				show.putExtra("shareKey", share);
				show.putExtra("id", eid);
				show.putExtra("_id", _id);
				show.putExtra("calenderEnabled", calenderEnabled);
				show.putExtra("rsvpNeeded", rsvpNeeded);
				show.putExtra("endTime", endTime); // ADDED VIKALP EVENT END
													// TIME

				show.putExtra("pos", position + "");
				// context.startActivity(show);
				return show;

				// Toast.makeText(getApplicationContext(),
				// title, Toast.LENGTH_SHORT).show();

			}

			// end of events

			else if (Tablename.equals("News")) {

				// Get the cursor, positioned to the corresponding row in the
				// result
				// set

				// Get the state's capital from this row in the database.
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				// SU VIKALP DATE ORDER ISSUE
				// String detail = cursor.getString(cursor
				// .getColumnIndexOrThrow("detail"));
				String detail = Utilities.convertDateToIndian(cursor
						.getString(cursor.getColumnIndexOrThrow("detail")));
				// EU VIKALP DATE ORDER ISSUE
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("summary"));
				String tid = cursor.getString(cursor
						.getColumnIndexOrThrow("nid"));
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow("name"));
				String link = cursor.getString(cursor
						.getColumnIndexOrThrow("link"));
				String type = cursor.getString(cursor
						.getColumnIndexOrThrow("type"));
				share = cursor.getString(cursor
						.getColumnIndexOrThrow("shareKey"));
				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));

				cursor.close();
				adb.close();
				Intent show;
				if (type.contentEquals("video")) {
					show = new Intent(context, NewsVideo.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("summary", summary);
					show.putExtra("shareKey", share);
					show.putExtra("id", tid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				}

				else if (type.contentEquals("image")) {
					show = new Intent(context, NewsImage.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("shareKey", share);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				} else if (type.contentEquals("audio")) {
					show = new Intent(context, NewsAudio.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("shareKey", share);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				}

				else {
					show = new Intent(context, NewsText.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					show.putExtra("name", name);
					show.putExtra("link", link);
					show.putExtra("summary", summary);
					show.putExtra("shareKey", share);
					show.putExtra("id", tid);
					show.putExtra("_id", _id);
					// context.startActivity(show);
					return show;
				}

				// Toast.makeText(getApplicationContext(),
				// title, Toast.LENGTH_SHORT).show();

			}

			// end of news

			else if (Tablename.equals("Award")) {
				// Get the cursor, positioned to the corresponding row in the
				// result
				// set

				// Get the state's capital from this row in the database.
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				// SU VIKALP DATE ORDER ISSUE
				// String detail = cursor.getString(cursor
				// .getColumnIndexOrThrow("detail"));
				String detail = Utilities.convertDateToIndian(cursor
						.getString(cursor.getColumnIndexOrThrow("detail")));
				// EU VIKALP DATE ORDER ISSUE
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("summary"));

				String name = cursor.getString(cursor
						.getColumnIndexOrThrow("name"));
				String imgPath = cursor.getString(cursor
						.getColumnIndexOrThrow("imagePath"));

				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));
				share = cursor.getString(cursor
						.getColumnIndexOrThrow("shareKey"));

				String aid = cursor.getString(cursor
						.getColumnIndexOrThrow("awardId"));

				if (Integer.parseInt(cursor.getString(cursor
						.getColumnIndexOrThrow("read_id"))) == 0) {
					// to set this announcement as read (setting read_id in
					// database
					// row as 1)
					AnnounceDBAdapter announce = new AnnounceDBAdapter(context);
					announce.open();
					announce.readrow(cursor.getString(cursor
							.getColumnIndexOrThrow("_id")), "Award");
					announce.close();

				}
				cursor.close();
				adb.close();
				Intent i = new Intent(context, Award.class);

				i.putExtra("title", title);
				i.putExtra("detail", detail);
				i.putExtra("summary", summary);
				i.putExtra("imagePath", imgPath);
				i.putExtra("name", name);
				i.putExtra("shareKey", share);
				i.putExtra("aid", aid);
				i.putExtra("_id", _id);
				// context.startActivity(i);
				return i;

			}

			else if (Tablename.equals("Feedback")) {
				Log.d("Cursor: ", String.valueOf(cursor.getCount()));

				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("feedbackTitle"));
				// SU ADDED FEEDBACK DATE SORT ORDER
				// String detail = cursor.getString(cursor
				// .getColumnIndexOrThrow("feedbackDate"));

				String detail = Utilities
						.convertDateToIndian(cursor.getString(cursor
								.getColumnIndexOrThrow("feedbackDate")));
				// EU ADDED FEEDBACK DATE SORT ORDER
				String feedtotalquestions = cursor.getString(cursor
						.getColumnIndexOrThrow("feedbackTotalQuestions"));
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("feedbackDescription"));
				String feedbackNo = cursor.getString(cursor
						.getColumnIndexOrThrow("feedbackNo"));

				cursor.close();
				Intent show;
				show = new Intent(context, FeedbackView.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				System.out.println("feedbacknewlist-->>>" + feedtotalquestions);
				System.out.println("Title feedbacknewlist----->" + title);
				// show.putExtra("from", from);
				// show.putExtra("Questions_no",""+total_questions );
				// Log.v("final", ""+R.id.feedbac);
				show.putExtra("feedbackNo", feedbackNo);

				show.putExtra("summary", summary);

				return (show);

			}

			// end of documents

			else if (Tablename.equals("Training")) {
				// Get the cursor, positioned to the corresponding row in the
				// result set

				// Get the state's capital from this row in the database.
				String title = cursor.getString(cursor
						.getColumnIndexOrThrow("title"));
				// SU VIKALP DATE ORDER ISSUE
				// String detail = cursor
				// .getString(cursor.getColumnIndexOrThrow("detail"));
				String detail = Utilities.convertDateToIndian(cursor
						.getString(cursor.getColumnIndexOrThrow("detail")));
				// SU VIKALP DATE ORDER ISSUE
				String summary = cursor.getString(cursor
						.getColumnIndexOrThrow("summary"));
				String tid = cursor.getString(cursor
						.getColumnIndexOrThrow("tid"));
				String name = cursor.getString(cursor
						.getColumnIndexOrThrow("name"));
				share = cursor.getString(cursor
						.getColumnIndexOrThrow("shareKey"));

				String ename = cursor.getString(cursor
						.getColumnIndexOrThrow("ename"));
				String _id = cursor.getString(cursor
						.getColumnIndexOrThrow("_id"));

				String type = cursor.getString(cursor
						.getColumnIndexOrThrow("type"));

				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancelAll();

				cursor.close();
				adb.close();

				try {

					Intent show;

					if (type.equals("audio")) {
						show = new Intent(context, TrainingAudio.class);
					} else if (type.equals("video")) {
						show = new Intent(context, TrainingVideo.class);
					} else if (type.equals("image")) {
						show = new Intent(context, TrainingImage.class);
					} else if (type.equals("pdf") || type.equals("ppt")
							|| type.equals("doc") || type.equals("xls")) {
						show = new Intent(context, Document.class);
						show.putExtra("mtitle", "Training");
						// show = openFile(type, name, ename, context);
					} else {
						show = null;
					}

					show.putExtra("title", title);
					System.out.println("Title -->" + title);
					show.putExtra("detail", detail);
					System.out.println("Detail -->" + detail);
					show.putExtra("type", type);
					System.out.println("Type -->" + type);
					show.putExtra("name", name);
					System.out.println("Name -->" + name);
					show.putExtra("ename", ename);
					System.out.println("Ename -->" + ename);
					show.putExtra("shareKey", share);
					System.out.println("ShareKey -->" + share);
					show.putExtra("summary", summary);
					System.out.println("Summary -->" + summary);
					show.putExtra("id", tid);
					System.out.println("id -->" + tid);
					show.putExtra("_id", _id);
					System.out.println("_id -->" + _id);

					// context.startActivity(show);
					return show;
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(context, "Suitable App not Found",
							Toast.LENGTH_SHORT).show();
				}

			}
			// end of training

		}

		Log.e("OpenContent", "Null intent returned");
		return null;

	}

	Intent openFile(String type, String name, String ename, Context ctx) {
		if (type.equals("pdf"))

		{

			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ ctx.getString(R.string.pdf) + "/" + ename);
			if (file.exists())
				Log.d("file", "exists");
			else
				Log.e("file", "does not exists");
			double bytes = file.length() / 500;
			int time = (int) bytes;
			Log.e("file length", time + "");
			// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP);
			folder.mkdirs();
			File file1 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP + name);
			if (file1.exists())
				file1.delete();

			try {

				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(file1);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			SystemClock.sleep(time);

			Intent show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file1), "application/pdf");
			return (show);

		}// end of pdf

		if (type.equals("ppt"))

		{

			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ ctx.getString(R.string.ppt) + "/" + ename);
			if (file.exists())
				Log.d("file", "exists");
			else
				Log.e("file", "does not exists");
			double bytes = file.length() / 500;
			int time = (int) bytes;
			Log.e("file length", time + "");
			// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP);
			folder.mkdirs();
			File file1 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP + name);
			if (file1.exists())
				file1.delete();

			try {

				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(file1);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			SystemClock.sleep(time);

			Intent show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file1),
					"application/vnd.ms-powerpoint");
			return (show);

		}// end of ppt

		if (type.equals("doc")) {

			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ ctx.getString(R.string.doc) + "/" + ename);
			if (file.exists())
				Log.d("file", "exists");
			else
				Log.e("file", "does not exists");
			double bytes = file.length() / 500;
			int time = (int) bytes;
			Log.e("file length", time + "");
			// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP);
			folder.mkdirs();
			File file1 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP + name);
			if (file1.exists())
				file1.delete();

			try {

				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(file1);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			SystemClock.sleep(time);

			Intent show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file1), "application/msword");
			return (show);

		}// end of doc

		if (type.equals("xls")) {

			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ ctx.getString(R.string.xls) + "/" + ename);
			if (file.exists())
				Log.d("file", "exists");
			else
				Log.e("file", "does not exists");
			double bytes = file.length() / 500;
			int time = (int) bytes;
			Log.e("file length", time + "");
			// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
			File folder = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP);
			folder.mkdirs();
			File file1 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_TEMP + name);
			if (file1.exists())
				file1.delete();

			try {

				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(file1);

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			SystemClock.sleep(time);

			Intent show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file1), "application/vnd.ms-excel");
			return (show);

		}// end of xls
		return null;
	}

}
