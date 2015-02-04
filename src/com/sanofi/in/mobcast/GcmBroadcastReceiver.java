package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.AsyncHttpPost.OnPostExecuteListener;
import com.parse.PushService;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	static final int uniqueID = 123456;
	private static final String TAG = "GcmBroadcastReceiver";
	String titlencp, message, id, category, imgName;
	String imagePath;
	String link, type, name, summary, detail, fro, url, caption, final_name;
	String fileLink;
	String rdate; // ADDED VIKALP AWARD RDATE

	JSONObject json = null;
	// String social = "no";

	String social = "off";
	String contentExpiry = "9999-99-99";
	String mobileNumber;
	Context ctx;
	Intent event = new Intent();

	int size;
	public Intent notificationIntent;
	Bundle b;
	HashMap<String, String> postParam;
	String xmlString = "";

	static Random i, j;
	String question;
	// Recruitment declaration starts
	String designation, location, minExp, contact;// Part of gcm
	String recruitString, skill, jobDesc, qualification, ctc; // To hold the
																// recruitment
																// details in a
																// string
	// Recruitment declaration ends

	// For feedback

	String feedbackID;
	static SessionManagement check;
	String endTime; // ADDED VIKALP EVENT END TIME

	String convertdate(String dateString1) {
		java.util.Date date = null;
		if (contentExpiry.equals("off")) {
			contentExpiry = "0";
		} else {

			try {
				date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString1);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "9999-99-99";
			}
		}
		String dateString2 = new SimpleDateFormat("yyyy-MM-dd").format(date);
		Log.d("datestring2", dateString2);
		return dateString2;

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		try {
			if (intent != null) {

				Log.d("New message for you", "Intent is not null");
				Log.d("Intent data", "is: --  " + intent.getExtras().keySet());
				handleIntent(context, intent);
			} else {
				Log.d("New Message for you", "Receiver intent is null");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void handleIntent(Context ctx, Intent intent) throws JSONException {
		// TODO Auto-generated method stub
		this.ctx = ctx;
		i = new Random();
		j = new Random();
		postParam = new HashMap<String, String>();
		Log.d(TAG, "Message Received");

		try {
			if (intent.getExtras().containsKey("data"))
				json = new JSONObject(intent.getExtras().getString("data"));
			else
				json = new JSONObject(intent.getExtras().getString(
						"com.parse.Data"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.d("titlencp", "is : " + json.getString("titlencp"));
		// Log.d("Message", "is : "+json.getString("message"));
		// Log.d("By", "is : " + json.getString("by"));
		/*
		 * Log.d("details", "is : " + json.getString("details"));
		 * Log.d("Description", "is : " + json.getString("description"));
		 * Log.d("ID", "is : " + json.getString("id")); Log.d("Type", "is : " +
		 * json.getString("type"));
		 */
		// Log.d("FileName", "is : " + json.getString("fileName"));

		if (json.has("mobcast"))
			try {
				category = json.getString("mobcast");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		Log.d("Category", "is: " + category);

		if (category.contains("remoteWipe")) {

			AnnounceDBAdapter adb = new AnnounceDBAdapter(ctx);
			adb.open();
			adb.remotewipe(ctx);
			adb.close();
			ctx.getSharedPreferences("MobCastPref", 0).edit().clear().commit();
			SessionManagement sm = new SessionManagement(ctx);
			System.out.print("is in foreground :" + isInForeground(ctx));
			try {
				sm.logoutUser(isInForeground(ctx));

			} catch (Exception e) {
			}
		}
		check = new SessionManagement(ctx);
		Log.v("Category", category);

		if (category.contentEquals("forceExit")) {
			SessionManagement logout = new SessionManagement(ctx);
			;
			logout.logoutUser(true);

		}

		if (category.contentEquals("logout")) {

		}

		if (category.contentEquals("Feedback")) {
			Log.v("feedback notification", "feedback....");

			try {

				feedbackID = json.getString("feedbackID");

			} catch (JSONException e)

			{
				e.printStackTrace();
			}

			Log.e("feedbackID", feedbackID);

			Log.v("Feedback", "  feedbackID:" + feedbackID);

			postParam.put("feedbackID", feedbackID);

			try {
				readFromXml();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// write data onto to the list.

			Log.v("XML", "xmlString is :" + xmlString);

			writeData(xmlString);

			Log.v("GCM", "data is written");
			Log.v("Feedback", "position of new data is " + (size + 1));
			notificationIntent = new OpenContent(ctx,
					AnnounceDBAdapter.SQLITE_FEEDBACK, feedbackID).itemView();

			generateNotification(ctx, "Feedback", "");

		}// end of Feedback Category

		if (category.contentEquals("Events")) {
			String date = "", venue = "", summary = "", day = "", time = "", cmpl = "";

			// 'titlencp' => $etitlencp , 'eventID' => $eID, 'mobcast' =>
			// 'Events' ,
			// 'date' => $eDate, 'venue' => $eVenue,
			// 'day' => $eDay, 'time' => $eTime, 'description' => $eDesc,
			// 'socialSharing' => $socialSharing,
			// 'calendarEnabled' => $showCalendar, 'rsvpNeeded' => $rsvpNeeded,
			// 'contentExpiry' => $contentExpiry

			// "calendarEnabled":"on","rsvpNeeded":"on"
			String calenderEnabled = "";
			String rsvpNeeded = "";
			try {
				titlencp = json.getString("titlencp");
				date = json.getString("date");
				id = json.getString("eventID");
				venue = json.getString("venue");
				day = json.getString("day");
				time = json.getString("time");
				endTime = json.getString("endTime"); // ADDED VIKALP EVENT END
														// TIME

				summary = json.getString("description");
				social = json.getString("socialSharing");
				contentExpiry = convertdate(json.getString("contentExpiry"));
				calenderEnabled = json.getString("calendarEnabled");
				Log.d("calenderEnabled", calenderEnabled);
				rsvpNeeded = json.getString("rsvpNeeded");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("rsvpNeeded", rsvpNeeded);

			/*
			 * message = intent.getStringExtra("description"); cmpl =
			 * intent.getStringExtra("cmpl");
			 * 
			 * 
			 * 
			 * /* Log.d(TAG, titlencp); Log.d(TAG, date); Log.d(TAG, venue);
			 * Log.d(TAG, summary); Log.d(TAG, time);
			 */

			AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
			// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen, summary);
			db.open();
			String _id = db.createEvent(titlencp, date, day, time, venue,
					summary, id, social, contentExpiry, rsvpNeeded,
					calenderEnabled, endTime)
					+ "";

			int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
					+ db.getUnreadCount(db.SQLITE_AWARD)
					+ db.getUnreadCount(db.SQLITE_EVENT)
					+ db.getUnreadCount(db.SQLITE_FEEDBACK)
					+ db.getUnreadCount(db.SQLITE_NEWS)
					+ db.getUnreadCount(db.SQLITE_TRAINING);

			// if(db.getUnreadCount("Event")>25)
			{
				SharedPreferences pref;
				pref = ctx.getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastEvent", id).commit();

			}

			db.close();

			Intent nIntent = new Intent(ctx, GcmService.class);
			nIntent.putExtra("unread", unread);
			nIntent.putExtra("message", "unread");
			Log.d("Notifs service", "Starting notification service....");
			ctx.startService(nIntent);
			notificationIntent = new OpenContent(ctx,
					AnnounceDBAdapter.SQLITE_EVENT, _id).itemView();

			generateNotification(ctx, "Event", titlencp);
		}

		if (category.contentEquals("Announcements")) {
			Log.v("Category", "Announcement");

			try {
				titlencp = json.getString("titlencp");
				fro = json.getString("by");
				summary = json.getString("description");
				detail = json.getString("details");
				type = json.getString("type");
				id = json.getString("id");

				social = json.getString("socialSharing");

				name = json.getString("fileName");
				link = json.getString("link");
				link = link.replace("www.mobcast.in", "192.168.1.106");
				fileLink = link;
				contentExpiry = convertdate(json.getString("contentExpiry"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.e("announceDBAdapter", "test");
			System.out.println("titlencp  ===> " + titlencp);
			System.out.println("fro  ===> " + fro);
			System.out.println("summary  ===> " + summary);
			System.out.println("detail  ===> " + detail);
			System.out.println("type  ===> " + type);
			System.out.println("id  ===> " + id);
			System.out.println("social  ===> " + social);

			System.out.println("name  ===> " + name);
			System.out.println("link  ===> " + link);

			Log.d("content expiry", contentExpiry);

			fro = fro.replaceAll("\\\\", "");
			titlencp = titlencp.replaceAll("\\\\", "");
			summary = summary.replaceAll("\\\\", "");

			if (link != null) {
				final_name = link.substring((link.lastIndexOf('/') + 1));
			}

			else
				final_name = name;
			System.out.println("Final Name  ===> " + final_name);

			AnnounceDBAdapter announce = new AnnounceDBAdapter(ctx);
			announce.open();

			// if(announce.getUnreadCount("Announce")>25)
			{
				SharedPreferences pref;
				pref = ctx.getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastAnnounce", id).commit();

			}

			announce.close();

			if (!type.contentEquals("text"))
				DownloadForAnnounce(ctx);
			else {

				// AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();

				String _id = announce.createAnnouncement(titlencp, detail, fro,
						type, "0", summary, id, social, contentExpiry, "0")
						+ "";

				int unread = announce.getUnreadCount(announce.SQLITE_ANNOUNCE)
						+ announce.getUnreadCount(announce.SQLITE_AWARD)
						+ announce.getUnreadCount(announce.SQLITE_EVENT)
						+ announce.getUnreadCount(announce.SQLITE_FEEDBACK)
						+ announce.getUnreadCount(announce.SQLITE_NEWS)
						+ announce.getUnreadCount(announce.SQLITE_TRAINING);

				announce.close();

				Intent nIntent = new Intent(ctx, GcmService.class);
				nIntent.putExtra("unread", unread);
				nIntent.putExtra("message", "unread");
				Log.d("Notifs service", "Starting notification service....");
				ctx.startService(nIntent);
				/*
				 * SharedPreferences prefs =
				 * PreferenceManager.getDefaultSharedPreferences(ctx); String
				 * objectID = prefs.getString("objectID", "0");
				 * 
				 * Log.d("ObjectID", "is: "+objectID);
				 */
				notificationIntent = new OpenContent(ctx,
						AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();

				/*
				 * prefs.edit().putString("category", category).commit();
				 * prefs.edit().putString("titlencp", titlencp).commit();
				 */

				// PushService.subscribe(ctx, objectID, AnnounceListView.class);

				generateNotification(ctx, "Announcement", titlencp);
			}
		}

		if (category.contentEquals("Training")) {
			Log.v("training", "In training ");

			// 'titlencp' => $ttitlencp, 'mobcast' => 'Training', 'description'
			// =>
			// $tDesc, 'fileName' => $fileName, 'details' => $todaysDate,
			// 'id' => $tID, 'link' => $fileLink, 'type' => $fileType,
			// 'socialSharing' => $socialSharing, 'contentExpiry' =>
			// $contentExpiry

			try {
				titlencp = json.getString("titlencp");
				name = json.getString("fileName");
				detail = json.getString("details");

				// TODO
				// TODO
				// social = json.getString("socialSharing");
				social = json.getString("socialSharing");

				contentExpiry = convertdate(json.getString("contentExpiry"));
				// if(social.contentEquals(null)) social = "off";
				// Log.d("socialSharing", social);
				summary = json.getString("description");
				id = json.getString("id");
				String link1 = json.getString("link");
				link = urlencode(link1);
				fileLink = link;
				type = json.getString("type");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.v("training titlencp", titlencp);
			Log.v("training detail", detail);
			Log.v("training type", type);
			Log.v("training summary", summary);
			Log.v("training", link);

			SharedPreferences pref;
			pref = ctx.getSharedPreferences("MobCastPref", 0);
			pref.edit().putString("lastTraining", id).commit();

			DownloadForTraining(ctx);

		}

		if (category.contentEquals("News")) {

			String link1 = "";
			try {
				titlencp = json.getString("titlencp");
				name = json.getString("fileName");
				detail = json.getString("details");
				link1 = json.getString("link");
				summary = json.getString("description");
				url = json.getString("source");

				id = json.getString("id");
				type = json.getString("type");
				social = json.getString("socialSharing");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 'titlencp' => $ntitlencp , 'source' => $nSource, 'mobcast' =>
			// 'News',
			// 'description' => $nDesc,
			// 'details' => $todaysDate, 'type' => 'audio', 'id' => $nID,
			// 'fileName' => $fileName,
			// 'socialSharing' => $socialSharing, 'contentExpiry' =>
			// $contentExpiry, 'link' => '$fileLink',

			/*
			 * caption = intent.getStringExtra("caption"); Log.e("LINK", link1);
			 */

			link = urlencode(link1);
			fileLink = link;
			Log.d(TAG, "is:" + titlencp);
			Log.d(TAG, "is:" + detail);
			// Log.d(TAG, "is:"+caption);
			Log.d(TAG, "is:" + summary);
			Log.d(TAG, "is:" + link);

			final_name = link.substring((link.lastIndexOf('/') + 1));
			System.out.println("Final Name  ===> " + final_name);

			AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
			// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen, summary);
			db.open();

			// if(announce.getUnreadCount("News")>25)
			{
				SharedPreferences pref;
				pref = ctx.getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastNews", id).commit();

			}
			db.close();

			if (!type.contentEquals("text"))
				DownloadForNews(ctx, url);
			else {

				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createNews(titlencp, detail, name, url, type,
						summary, id, social, contentExpiry, "0") + "";
				Log.d(TAG, "Training added to list");

				int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
						+ db.getUnreadCount(db.SQLITE_AWARD)
						+ db.getUnreadCount(db.SQLITE_EVENT)
						+ db.getUnreadCount(db.SQLITE_FEEDBACK)
						+ db.getUnreadCount(db.SQLITE_NEWS)
						+ db.getUnreadCount(db.SQLITE_TRAINING);

				db.close();
				notificationIntent = new OpenContent(ctx,
						AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

				Intent nIntent = new Intent(ctx, GcmService.class);
				nIntent.putExtra("unread", unread);
				nIntent.putExtra("message", "unread");
				Log.d("Notifs service", "Starting notification service....");
				ctx.startService(nIntent);

				/*
				 * SharedPreferences prefs = PreferenceManager
				 * .getDefaultSharedPreferences(ctx);
				 * 
				 * prefs.edit().putString("category", category).commit();
				 * prefs.edit().putString("titlencp", titlencp).commit();
				 */

				generateNotification(ctx, "News", titlencp);
			}
		}

		if (category.contentEquals("Recruitment")) {

			SharedPreferences pref;
			pref = ctx.getSharedPreferences("MobCastPref", 0);
			pref.edit().putString("gcmregID", null).commit();
			mobileNumber = pref.getString("LoginID", null);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(com.mobcast.util.Constants.user_id, mobileNumber);

			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
			asyncHttpPost.execute(com.mobcast.util.Constants.CHECK_RECRUITMENT);

			asyncHttpPost.setOnPostExecuteListener(new OnPostExecuteListener() {

				@Override
				public void onPostExecute(String result) {
					// TODO Auto-generated method stub

					Intent i = new Intent("com.mobcast.ncp.RecruitList");
					i.putExtra(RecruitList.RECRUIT_DATA, result);
					// startActivity(i);
					notificationIntent = i;
					generateNotification(GcmBroadcastReceiver.this.ctx, "",
							"New Recruitment");
				}
			});

			// RecruitTask asyncHttpPost1 = new RecruitTask();

			// asyncHttpPost1
			// .execute(com.mobcast.util.Constants.CHECK_RECRUITMENT);;

		}// End of Recruitment category

		// For Award Category
		if (category.contentEquals("Award")) {

			try {
				// Getting Data from pushed notification
				id = json.getString("id");// server award id for the award
				Log.e("award id", id + "");
				name = json.getString("winnerName");// Person who won the
													// award
				// name = "winner";// Person who won the award
				link = json.getString("link");// link to download the image
				link = link.replace(" ", "%20");
				link = link.replace("www.mobcast.in", "192.168.1.106");
				fileLink = link;
				titlencp = json.getString("titlencp");// titlencp of the award
				detail = json.getString("details");// Time detail
				rdate = json.getString("timeStamp");

				// TODO
				// TODO
				// social = intent.getStringExtra("socialSharing");
				social = json.getString("socialSharing");

				// contentExpiry = convertdate
				// (intent.getStringExtra("contentExpiry"));
				contentExpiry = convertdate("off");
				summary = json.getString("description");// Summary of the
														// award
				imgName = json.getString("fileName");// Name of image file
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// DownloadImage from Link Code
			try {
				URL url = new URL(link);
				String root = Environment.getExternalStorageDirectory()
						.toString();
				File myDir = new File(root + "/.mobcast/mobcast_images");
				myDir.mkdirs();
				String fname = imgName;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * URLConnection ucon = url.openConnection(); InputStream is =
				 * ucon.getInputStream(); BufferedInputStream bis = new
				 * BufferedInputStream(is); ByteArrayBuffer baf = new
				 * ByteArrayBuffer(50); int current = 0; while ((current =
				 * bis.read()) != -1) { baf.append((byte) current); }
				 * 
				 * FileOutputStream fos = new FileOutputStream(file);
				 * fos.write(baf.toByteArray()); fos.close();
				 */
				imagePath = root + "/.mobcast/mobcast_images/" + fname;
				Log.v("image name", fname);
				// Storing in Database

				if (res.equals("done")) {

					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
					db.open();
					String _id = db.createAward(titlencp, name, detail, rdate,
							id, summary, imagePath, social, contentExpiry,
							fileLink)
							+ "";

					// if(db.getUnreadCount("Award")>25)
					{
						SharedPreferences pref;
						pref = ctx.getSharedPreferences("MobCastPref", 0);
						pref.edit().putString("lastAward", id).commit();

					}
					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);
					db.close();

					// Passing intent to Award with data
					if (check.isLoggedIn()) {

						Intent nIntent = new Intent(ctx, GcmService.class);
						nIntent.putExtra("unread", unread);
						nIntent.putExtra("message", "unread");
						Log.d("Notifs service",
								"Starting notification service....");
						ctx.startService(nIntent);

						notificationIntent = new Intent(new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());
						generateNotification(ctx, "Award", titlencp);
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}// end of Award Category

		// end of handleIntent()

	}

	public void readFromXml() throws InterruptedException, ExecutionException {
		boolean isInternetPresent;

		ConnectionDetector cd = new ConnectionDetector(ctx);
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent) {

			FeedbackTask asyncHttpPost = new FeedbackTask();
			asyncHttpPost.execute(com.mobcast.util.Constants.CHECK_FEEDBACK);

			xmlString = asyncHttpPost.get();
			Log.v("readfromxml", "xml is:" + xmlString);
		}

	}// end of readFromXml

	public void writeData(String response) {
		try {

			Log.d("response", response);
			JSONArray jsonArray = new JSONArray(response);
			int i, j;
			i = jsonArray.length();
			Log.d("Number of entries", i + "");

			Log.v("GCMIntent", "in feedback writeData");

			AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);

			for (j = 0; j < i; j++) {
				JSONObject jObject1 = jsonArray.getJSONObject(j);
				String Fedbackrow = jObject1.getString("feedbackQuestion");
				String totalQuestions = jObject1.getString("totalQuestions");
				JSONObject jObject = new JSONObject(Fedbackrow);

				String feedbackID = jObject.getString("feedbackID");
				String feedbacktitlencp = jObject.getString("feedbacktitlencp");
				String feedbackDescription = jObject
						.getString("feedbackDescription");
				String feedbackDate = jObject.getString("feedbackDate");
				String feedbackAttempts = jObject.getString("feedbackAttempts");
				String questionNo = jObject.getString("questionNo");

				Log.d("question no",
						questionNo.substring(questionNo.lastIndexOf("/") + 1));
				questionNo = questionNo
						.substring(questionNo.lastIndexOf("/") + 1);

				String questionType = jObject.getString("questionType");
				String question = jObject.getString("question");
				String answerA = jObject.getString("answerA");
				String answerB = jObject.getString("answerB");
				String answerC = jObject.getString("answerC");
				String answerD = jObject.getString("answerD");
				String answerLimit = jObject.getString("answerLimit");
				// String contentExpiry = jObject.getString("contentExpiry");
				String contentExpiry = convertdate(jObject
						.getString("contentExpiry"));

				FeedbackDBHandler feedback = new FeedbackDBHandler();
				feedback.setFeedbackNo(feedbackID);
				feedback.setFeedbacktitle(feedbacktitlencp);
				feedback.setFeedbackDescription(feedbackDescription);
				feedback.setFeedbackDate(feedbackDate);
				feedback.setFeedbackAttempt(feedbackAttempts);
				feedback.setFeedbackTotalQuestions(totalQuestions);
				feedback.setQuestionNo(questionNo);
				feedback.setQuestionType(questionType);
				feedback.setQuestionTitle(question);
				feedback.setOptionA(answerA);
				feedback.setOptionB(answerB);
				feedback.setOptionC(answerC);
				feedback.setOptionD(answerD);
				feedback.setAnswerLimit(answerLimit);
				db.open();
				db.addFeedback(feedback, contentExpiry);
				db.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end of writeData

	public void DownloadFromUrl(Context ctx) { // this is the downloader method
		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();
			if (type.contentEquals("image")) {
				File myDir = new File(root + "/.mobcast/mobcast_images/");
				myDir.mkdirs();

				// Changes done here
				String fname = name + String.valueOf(i.nextInt(50))
						+ String.valueOf(j.nextInt(320000));
				Log.d("old name", name);
				Log.d("new name", fname);
				// changes stopped here
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("old name", name);
				Log.d("new name", fname);
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */
				HttpURLConnection ucon = (HttpURLConnection) url
						.openConnection();

				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				FileOutputStream fos = new FileOutputStream(file);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				byte[] buffer = new byte[1024];
				int current = 0;
				while ((current = bis.read(buffer)) != -1) {
					fos.write(buffer, 0, current);
				}

				/* Convert the Bytes read to a String. */

				fos.close();
				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				String imgPath = root + "/.mobcast/mobcast_images/" + fname;

				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createAnnouncement(titlencp, detail, fro, type,
						fname, summary, id, social, contentExpiry, imgPath)
						+ "";
				Log.d(TAG, "Training added to list");
				db.close();

				if (check.isLoggedIn()) {

					notificationIntent = new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();

					generateNotification(ctx, "Announcement", titlencp);
				} else
					notificationIntent = new Intent(ctx, LoginV2.class);
			}
			if (type.contentEquals("video")) {

				File myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
				String fname = name;
				// Log.v("fname",fname);
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */
				URLConnection ucon = url.openConnection();
				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				InputStream is = ucon.getInputStream();
				// bookmarkstart
				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1) and write on the fly in the file.
				 */
				FileOutputStream fos = new FileOutputStream(file);
				final int BUFFER_SIZE = 25 * 1024;
				BufferedInputStream bis = new BufferedInputStream(is,
						BUFFER_SIZE);
				byte[] baf = new byte[BUFFER_SIZE];
				int actual = 0;
				while (actual != -1) {
					fos.write(baf, 0, actual);
					actual = bis.read(baf, 0, BUFFER_SIZE);
				}

				fos.close();

				/*
				 * BufferedInputStream bis = new BufferedInputStream(is);
				 * 
				 * // // * Read bytes to the Buffer until there is nothing more
				 * to read(-1). // ByteArrayBuffer baf = new
				 * ByteArrayBuffer(50); int current = 0; while ((current =
				 * bis.read()) != -1) { baf.append((byte) current); }
				 * 
				 * // Convert the Bytes read to a String. FileOutputStream fos =
				 * new FileOutputStream(file); fos.write(baf.toByteArray());
				 * fos.close();
				 */

				// bookmarkend
				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				String link = root + "/.mobcast/mobcast_images/" + name;

				AnnounceDBAdapter announce = new AnnounceDBAdapter(ctx);
				announce.open();
				String _id = announce.createAnnouncement(titlencp, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				announce.close();

				if (check.isLoggedIn()) {
					notificationIntent = new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();
					generateNotification(ctx, "Announcement", titlencp);
				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

			}
			if (type.contentEquals("audio")) {
				Log.v("training", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

				AnnounceDBAdapter announce = new AnnounceDBAdapter(ctx);
				announce.open();
				String _id = announce.createAnnouncement(titlencp, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				announce.close();

				if (check.isLoggedIn()) {
					notificationIntent = new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();
					generateNotification(ctx, "Announcement", titlencp);

				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

				generateNotification(ctx, "Announcement", titlencp);

			}// end of audio

			generateNotification(ctx, "Announcement", titlencp);

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}

	public void DownloadForTraining(Context ctx) {

		Log.v("Training ", "in downloadforTraining");
		String ename = System.currentTimeMillis() + "";

		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();
			Log.v("type is ", type.toString());

			if (type.contentEquals("video")) {

				File myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
				String fname = name;
				// Log.v("fname",fname);
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */

				new Downloader(file, url).execute();

				/*
				 * URLConnection ucon = url.openConnection();
				 * 
				 * Define InputStreams to read from the URLConnection.
				 * 
				 * InputStream is = ucon.getInputStream(); BufferedInputStream
				 * bis = new BufferedInputStream(is);
				 * 
				 * 
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 * 
				 * ByteArrayBuffer baf = new ByteArrayBuffer(50); int current =
				 * 0; while ((current = bis.read()) != -1) { baf.append((byte)
				 * current); }
				 * 
				 * Convert the Bytes read to a String. FileOutputStream fos =
				 * new FileOutputStream(file); fos.write(baf.toByteArray());
				 * fos.close();
				 * 
				 * AnnounceDBAdapter db = new AnnounceDBAdapter(ctx); //
				 * db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				 * summary); db.open(); String _id = db.createTraining(titlencp,
				 * detail, name, summary, type, ename, id, social,
				 * contentExpiry, fileLink) + ""; Log.d(TAG,
				 * "Training added to list"); db.close();
				 * 
				 * if (check.isLoggedIn()) {
				 * 
				 * notificationIntent = new Intent(new OpenContent(ctx,
				 * AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());
				 * 
				 * } else notificationIntent = new Intent(ctx, LoginV2.class);
				 * 
				 * generateNotification(ctx, "New Training Notification",
				 * titlencp);
				 */
			}

			if (type.contentEquals("pdf")) {
				Log.v("training", "in pdf");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getResources().getString(R.string.pdf));
				myDir.mkdirs();

				// String fname = ename;
				String fname = ename;
				Log.v("name of pdf", fname);
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				new Downloader(file, url).execute();

				/*
				 * URLConnection ucon = url.openConnection();
				 * 
				 * InputStream is = ucon.getInputStream(); BufferedInputStream
				 * bis = new BufferedInputStream(is);
				 * 
				 * ByteArrayBuffer baf = new ByteArrayBuffer(50); int current =
				 * 0; while ((current = bis.read()) != -1) { baf.append((byte)
				 * current); }
				 * 
				 * Convert the Bytes read to a String. FileOutputStream fos =
				 * new FileOutputStream(file); fos.write(baf.toByteArray());
				 * fos.close();
				 * 
				 * // String link = root + "/.mobcast/mobcast_pdf/" + name;
				 * AnnounceDBAdapter db = new AnnounceDBAdapter(ctx); //
				 * db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				 * summary); db.open(); String _id = db.createTraining(titlencp,
				 * detail, name, summary, type, ename, id, social,
				 * contentExpiry, fileLink) + ""; Log.d(TAG,
				 * "Training added to list"); db.close();
				 * 
				 * if (check.isLoggedIn()) { notificationIntent = new Intent(new
				 * OpenContent(ctx, AnnounceDBAdapter.SQLITE_TRAINING,
				 * _id).itemView());
				 * 
				 * } else notificationIntent = new Intent(ctx, LoginV2.class);
				 * 
				 * generateNotification(ctx, "New Training Notification",
				 * titlencp);
				 */
			}// end of type is pdf

			if (type.equals("ppt")) {
				Log.v("training", "in ppt");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getResources().getString(R.string.ppt));
				myDir.mkdirs();
				// String fname = ename;
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				new Downloader(file, url).execute();

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createTraining(titlencp, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();

				if (check.isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());

				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

				generateNotification(ctx, "New Training Notification", titlencp);
			}// end of ppt

			if (type.contentEquals("audio")) {
				Log.v("training", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createTraining(titlencp, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();

				if (check.isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());

				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

				generateNotification(ctx, "New Training Notification", titlencp);

			}// end of audio

			if (type.equals("xls")) {

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getResources().getString(R.string.xls));
				myDir.mkdirs();
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createTraining(titlencp, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();

				if (check.isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());

				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

				generateNotification(ctx, "New Training Notification", titlencp);
			}// end of xls

			if (type.equals("doc")) {
				Log.v("training", "in doc");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/.mobcast/"
						+ ctx.getResources().getString(R.string.doc));
				myDir.mkdirs();
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				URLConnection ucon = url.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
				// summary);
				db.open();
				String _id = db.createTraining(titlencp, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();

				if (check.isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());

				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

				generateNotification(ctx, "New Training Notification", titlencp);
			}// end of doc

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}// end of Download for training

	public void DownloadForAnnounce(Context ctx) {

		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();

			if (type.contentEquals("video")) {
				// URL url = new URL(link);
				// String root =
				// Environment.getExternalStorageDirectory().toString();
				File myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */
				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				if (res.equals("done")) {

					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx); //
					db.open();
					String _id = db.createAnnouncement(titlencp, detail, fro,
							type, name, summary, id, social, contentExpiry,
							fileLink)
							+ "";
					Log.d(TAG, "Announcement added to list");

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", titlencp);
				}
			}

			else if (type.contentEquals("audio")) {

				Log.v("Announcement", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();
				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (res.equals("done")) {

					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx); //
					db.open();
					String _id = db.createAnnouncement(titlencp, detail, fro,
							type, name, summary, id, social, contentExpiry,
							fileLink)
							+ "";
					Log.d(TAG, "Announcement added to list");

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", titlencp);
				}

			} else if (type.contentEquals("image")) {

				File myDir = new File(root + "/.mobcast/mobcast_images");
				myDir.mkdirs();
				String fname = final_name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */

				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * URLConnection ucon = url.openConnection();
				 * 
				 * 
				 * Define InputStreams to read from the URLConnection.
				 * 
				 * InputStream is = ucon.getInputStream(); BufferedInputStream
				 * bis = new BufferedInputStream(is);
				 * 
				 * 
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 * 
				 * ByteArrayBuffer baf = new ByteArrayBuffer(50); int current =
				 * 0; while ((current = bis.read()) != -1) { baf.append((byte)
				 * current); }
				 * 
				 * Convert the Bytes read to a String. FileOutputStream fos =
				 * new FileOutputStream(file); fos.write(baf.toByteArray());
				 * fos.close();
				 */
				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				if (res.equals("done")) {

					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx); //

					Log.d("Nulls", "are:" + summary + "-" + social + "-"
							+ contentExpiry + "-" + fileLink + "-" + id);

					db.open();

					String _id = db.createAnnouncement(titlencp, detail, fro,
							type, final_name, summary, id, social,
							contentExpiry, fileLink)
							+ "";
					Log.d(TAG, "Announcement added to list");

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", titlencp);
				}

				// String link = root + "/.mobcast/mobcast_images/" + name;

			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}
	}

	public class Downloader extends AsyncTask<URL, Void, String> {

		File file;
		URL url;
		long startTime;

		Downloader(File file, URL url) {
			this.file = file;
			this.url = url;
			Log.d("url", "is: " + url);
			long startTime = System.currentTimeMillis();
		}

		@Override
		protected String doInBackground(URL... params) {
			// TODO Auto-generated method stub

			Log.d("url", "is: " + url);

			String done = "";

			try {
				URL url1 = url;

				Log.d("url1", "is: " + url1);
				URLConnection ucon = url1.openConnection();

				/*
				 * Define InputStreams to read from the URLConnection.
				 */
				Log.d("URL Connection", "is: " + ucon.getContentLength());
				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				/* Convert the Bytes read to a String. */
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(baf.toByteArray());
				fos.close();
				done = "done";
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return done;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			Log.d("tHIS IS THEE RESULT OF DO IN BACKGROUND", "is: " + result);

			/*
			 * Log.d("ImageManager Downloader method is done now",
			 * "download ready in" + ((System.currentTimeMillis() - startTime) /
			 * 1000) + " sec");
			 * 
			 * if (category.contentEquals("Announcements")) doAnnounceRest();
			 * 
			 * else if (category.contentEquals("News")) doNewsRest(prlink);
			 * 
			 * else if (category.contentEquals("Training")) doTrainingRest();
			 * 
			 * else if (category.contentEquals("Award")) doAwardRest();
			 * 
			 * else if (category.contentEquals("Events")) doEventRest();
			 */
		}

	}

	public void DownloadForNews(Context ctx, String prlink) { // this is the
		// downloader
		// method
		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();

			if (type.contentEquals("video")) {
				// URL url = new URL(link);
				// String root =
				// Environment.getExternalStorageDirectory().toString();
				File myDir = new File(root + "/.mobcast/mobcast_videos");
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");

				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				if (res.equals("done")) {
					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
					// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
					// summary);
					db.open();
					String _id = db.createNews(titlencp, detail, name, prlink,
							type, summary, id, social, contentExpiry, fileLink)
							+ "";
					Log.d(TAG, "News added to list");

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "News", titlencp);
				}
			}

			else if (type.contentEquals("audio")) {

				Log.v("News", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/.mobcast/mobcast_audio");
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (res.equals("done")) {

					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
					db.open();
					// announce.createAnnouncement(titlencp, detail, fro, type,
					// name,
					// summary, id);
					String _id = db.createNews(titlencp, detail, name, prlink,
							type, summary, id, social, contentExpiry, fileLink)
							+ "";

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "News", titlencp);
				}
			} else if (type.contentEquals("image")) {

				File myDir = new File(root + "/.mobcast/mobcast_images");
				myDir.mkdirs();
				String fname = final_name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				String url1 = url.toString().replaceAll(" ", "%20");
				url = new URL(url1);
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");
				/* Open a connection to that URL. */

				String res = "";
				try {
					res = new Downloader(file, url).execute().get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("ImageManager",
						"download ready in"
								+ ((System.currentTimeMillis() - startTime) / 1000)
								+ " sec");

				// String link = root + "/.mobcast/mobcast_images/" + name;

				if (res.equals("done")) {
					AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
					// db.createEvent(Dtitlencp, Dwhen, Dwhen, Dwhen, Dwhen,
					// summary);
					db.open();
					// db.createAnnouncement(titlencp, detail, fro, type, name,
					// summary, id);
					// db.createNews(link, root, fname, link, fname, url1,
					// link);

					String _id = db.createNews(titlencp, detail, final_name,
							prlink, type, summary, id, social, contentExpiry,
							fileLink)
							+ "";

					int unread = db.getUnreadCount(db.SQLITE_ANNOUNCE)
							+ db.getUnreadCount(db.SQLITE_AWARD)
							+ db.getUnreadCount(db.SQLITE_EVENT)
							+ db.getUnreadCount(db.SQLITE_FEEDBACK)
							+ db.getUnreadCount(db.SQLITE_NEWS)
							+ db.getUnreadCount(db.SQLITE_TRAINING);

					Log.d(TAG, "News added to list");
					db.close();

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					} else
						notificationIntent = new Intent(ctx, NewsList.class);
					generateNotification(ctx, "News", titlencp);
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}
	}// End of DownloadFromNews

	public class FeedbackTask extends AsyncTask<String, Void, String> {

		private HashMap<String, String> mData = postParam;
		// private HashMap<String, String> mData = null;
		String str = "";

		@Override
		protected String doInBackground(String... params) {

			// perform long running operation operation

			byte[] result = null;

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
			// is URL
			try {
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					nameValuePair.add(new BasicNameValuePair(key, mData
							.get(key)));
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				HttpResponse response = client.execute(post);

				StringBuilder bodyBuilder = new StringBuilder();
				Iterator<Entry<String, String>> iterator = mData.entrySet()
						.iterator();
				// constructs the POST body using the parameters
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					bodyBuilder.append(param.getKey()).append('=')
							.append(param.getValue());
					if (iterator.hasNext()) {
						bodyBuilder.append('&');
					}
				}
				String body = bodyBuilder.toString();
				Log.v("FeedbackPost", "Posting '" + body + "' to " + params[0]);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "iso-8859-1");
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			Log.v("exevuteXML", "str:" + str);
			return str;

		}// End of doInBackgrounf

		@Override
		protected void onPostExecute(String result) {
			Log.v("postxml", "Result is:" + result);
			// xmlString=result;
		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

		}// end of onPreExecute

	}// end of Feedback Task class

	String urlencode(String s) {
		return s.replace(" ", "%20");
	}

	// For getting data from server for Recruitment Module

	private class RecruitTask extends AsyncTask<String, Void, String> {

		private HashMap<String, String> mData = postParam;
		String str = "";

		@Override
		protected String doInBackground(String... params) {

			// perform long running operation operation

			byte[] result = null;

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
			// is URL
			try {
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					mobileNumber = new SessionManagement(ctx).getUserDetails()
							.get("name");
					nameValuePair.add(new BasicNameValuePair(
							com.mobcast.util.Constants.user_id, mobileNumber));
					Log.d("mobile number in recruitment", mobileNumber);
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				HttpResponse response = client.execute(post);

				StringBuilder bodyBuilder = new StringBuilder();
				Iterator<Entry<String, String>> iterator = mData.entrySet()
						.iterator();
				// constructs the POST body using the parameters
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					bodyBuilder.append(param.getKey()).append('=')
							.append(param.getValue());
					if (iterator.hasNext()) {
						bodyBuilder.append('&');
					}
				}
				String body = bodyBuilder.toString();
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "iso-8859-1");
					Log.d("response", str);

				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			return str;

		}// End of doInBackgrounf

		@Override
		protected void onPostExecute(String result) {

			Intent i = new Intent("com.mobcast.ncp.RecruitList");
			i.putExtra(RecruitList.RECRUIT_DATA, result);
			// startActivity(i);
			notificationIntent = i;
			generateNotification(ctx, "Mobcast", "New Recruitment");
		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

		}// end of onPreExecute

	}// end of RecruitTask class

	// end of recruittask
	public static boolean isInForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		if (services.get(0).topActivity.getPackageName().toString()
				.equalsIgnoreCase(context.getPackageName().toString())) {
			System.out.print("true");
			return true;
		}
		return false;
	}

	private void generateNotification(Context ctx, String message,
			String titlencp) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					ctx).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("New Mobcast " + message)
					.setContentText(titlencp);

			mBuilder.setDefaults(-1);
			mBuilder.setOnlyAlertOnce(true);
			mBuilder.setAutoCancel(true);
			// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;
			PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
					uniqueID, notificationIntent,
					//
					// PendingIntent.FLAG_ONE_SHOT);
					PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) ctx
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			SessionManagement sm = new SessionManagement(ctx);
			Random r = new Random();
			if (sm.isLoggedIn()) {
				mNotificationManager.cancel(434);
				mNotificationManager.notify(434, mBuilder.build());
			} else {
				Log.e("logged out", "notificaiton received");
			}

		}

	}

	private void putInPref(Context ctx, String category, String titlencp) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		prefs.edit().putString("category", category);
		prefs.edit().putString("titlencp", titlencp);
		prefs.edit().commit();
	}

}
