package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.File;
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
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
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

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.mobcast.myperformance.MyPerformanceActivity;
import com.mobcast.receiver.AwardCongratulateReceiver;
import com.mobcast.receiver.EventCalendarReceiver;
import com.mobcast.receiver.EventNoReceiver;
import com.mobcast.receiver.EventYesReceiver;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AsyncHttpPost.OnPostExecuteListener;

@SuppressLint("NewApi") public class GCMIntentService extends GCMBaseIntentService {

	private static final String PROJECT_ID = com.mobcast.util.Constants.PROJECT_ID; // 16011280382
	// 214463480186
	static final int uniqueID = 123456;
	private static final String TAG = "GCMIntentService";
	String title, message, id, category, imgName;
	String imagePath;
	String link, type, name, summary, detail, fro, url, caption, final_name;
	String fileLink;
	String rdate; // VIKALP AWARD RECEIVED DATE
	String fileMetaData; // ADDED RICH NOTIFICATION

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
	String endTime=""; //ADDED VIKALP EVENT END TIME

	public static void dumpIntent(Intent i) {

		Bundle bundle = i.getExtras();
		if (bundle != null) {
			Set<String> keys = bundle.keySet();
			Iterator<String> it = keys.iterator();
			Log.d(TAG, "Dumping Intent start");
			while (it.hasNext()) {
				String key = it.next();
				Log.d(TAG, "[" + key + "=" + bundle.get(key) + "]");
			}
			Log.d(TAG, "Dumping Intent end");
		}
	}

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

	static SessionManagement check;

	public GCMIntentService() {
		super(PROJECT_ID);
		Log.d(TAG, "GCMIntentService init");
	}

	@Override
	protected void onError(Context ctx, String sError) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Error: " + sError);

	}// end of onError

	@Override
	protected void onMessage(Context ctx, Intent intent) {
		try{

			this.ctx = ctx;
			i = new Random();
			j = new Random();
			postParam = new HashMap<String, String>();
			Log.d(TAG, "Message Received");
			// Bundle bundle = intent.getExtras();
			// JSONObject jObj = null;
			// try {
			// jObj = new JSONObject(String.valueOf(bundle.get("data")));
			// } catch (JSONException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			//
			String category = "";
			category = intent.getStringExtra("mobcast");
			// Log.d("Category", "is: "+category );
			/*
			 * for (String key : bundle.keySet()) { Object value = bundle.get(key);
			 * Log.d(TAG, String.format("%s %s", key, value.toString())); }
			 * Log.d("Intent", "haha"+bundle.keySet());
			 * 
			 * Log.d("Mobcast", "is: "+String.valueOf(bundle.get("mobcast")));
			 * Log.d("Push Hash", "is: "+bundle.get("push_hash")); Log.d("Details",
			 * "is: "+bundle.get("details")); Log.d("Description",
			 * "is: "+bundle.get("description").toString());
			 */

			dumpIntent(intent);
			// if(intent!=null)
			// category = intent.getStringExtra("mobcast");

			Log.d("Category", "is: " + category);
			if (category.contains("remoteWipe")) {

				Log.i(TAG, "remoteWipe - Received");
				AnnounceDBAdapter adb = new AnnounceDBAdapter(ctx);
				adb.open();
				adb.remotewipe(ctx);
				adb.close();
				getSharedPreferences("MobCastPref", 0).edit().clear().commit();
				SessionManagement sm = new SessionManagement(ctx);
				System.out.print("is in foreground :" + isInForeground(ctx));
				try {
					sm.logoutUser(isInForeground(ctx));
					ApplicationLoader.getPreferences().setLoggedIn(false);
					ApplicationLoader.getPreferences().setMobileNumber("");
					ApplicationLoader.getPreferences().setRegId("");
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "remoteWipe - Successful");
			}

			check = new SessionManagement(getApplicationContext());
			Log.v("Category", category);

			if (category.contentEquals("forceExit")) {
				SessionManagement logout = new SessionManagement(
						getApplicationContext());
				;
				logout.logoutUser(true);

			}

			if (category.contentEquals("logout")) {

			}

			if (category.contentEquals("Feedback")) {
				Log.v("feedback notif", "feedback....");

				feedbackID = intent.getStringExtra("feedbackID");

				Log.e("feedbackID", feedbackID);

				Log.v("Feedback", "  feedbackID:" + feedbackID);

				postParam.put("feedbackID", feedbackID);

				try {
					readFromXml();
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Feedback", feedbackID);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
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
//				generateNotification(ctx, "Feedback", "");
				// SA VIKALP RICH NOTIFICATION
				String nQuestions = "0";
				try {
					JSONArray mJSONArray = new JSONArray(xmlString);
					nQuestions = String.valueOf(mJSONArray.length());
				} catch (Exception e) {
				}
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					generateFeedbackNotification(ctx, "Feedback", "", nQuestions);
				} else {
					generateNotification(ctx, "Feedback", "");
				}
				// EA VIKALP RICH NOTIFICATION
							

			}// end of Feedback Category

			if (category.contentEquals("Events")) {
				String date, venue, summary, day, time, cmpl;

				// 'title' => $eTitle , 'eventID' => $eID, 'mobcast' => 'Events' ,
				// 'date' => $eDate, 'venue' => $eVenue,
				// 'day' => $eDay, 'time' => $eTime, 'description' => $eDesc,
				// 'socialSharing' => $socialSharing,
				// 'calendarEnabled' => $showCalendar, 'rsvpNeeded' => $rsvpNeeded,
				// 'contentExpiry' => $contentExpiry

				title = intent.getStringExtra("title");
				date = intent.getStringExtra("date");
				id = intent.getStringExtra("eventID");
				venue = intent.getStringExtra("venue");
				day = intent.getStringExtra("day");
				time = intent.getStringExtra("time");
				endTime = intent.getStringExtra("endTime"); //ADDED VIKALP EVENT END TIME

				summary = intent.getStringExtra("description");
				social = intent.getStringExtra("socialSharing");
				contentExpiry = convertdate(intent.getStringExtra("contentExpiry"));
				// "calendarEnabled":"on","rsvpNeeded":"on"
				String calenderEnabled = intent.getStringExtra("calendarEnabled");
				Log.d("calenderEnabled", calenderEnabled);
				String rsvpNeeded = intent.getStringExtra("rsvpNeeded");
				Log.d("rsvpNeeded", rsvpNeeded);

				/*
				 * message = intent.getStringExtra("description"); cmpl =
				 * intent.getStringExtra("cmpl");
				 * 
				 * 
				 * 
				 * /* Log.d(TAG, title); Log.d(TAG, date); Log.d(TAG, venue);
				 * Log.d(TAG, summary); Log.d(TAG, time);
				 */

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createEvent(title, date, day, time, venue, summary,
						id, social, contentExpiry, rsvpNeeded, calenderEnabled,endTime)
						+ "";

				// if(db.getUnreadCount("Event")>25)
				{
					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastEvent", id).commit();
					ApplicationLoader.getPreferences().setLastEventsId(id);//ADDED VIKALP PULL SERVICE
				}

				db.close();
				notificationIntent = new OpenContent(ctx,
						AnnounceDBAdapter.SQLITE_EVENT, _id).itemView();

//				generateNotification(ctx, "Event", title);

				// SA VIKALP RICH NOTIFICATION
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					generateEventNotification(ctx, "Event", title, _id);
				} else {
					generateNotification(ctx, "Event", title);
				}
				// EA VIKALP RICH NOTIFICATION
							
				// SA VIKALP UPDATE NOTIFICATION RECEIVED
				try {
					updateNotificationReceived(ctx, "Events", id);
				} catch (Exception ex) {
					Log.e("updateNotificationReceived", ex.toString());
				}
				// EA VIKALP UPDATE NOTIFICATION RECEIVED
			}

			if (category.contentEquals("Announcements")) {
				Log.v("Category", "Announcement");

				title = intent.getStringExtra("title");
				fro = intent.getStringExtra("by");
				summary = intent.getStringExtra("description");
				// SU VIKALP DATE ORDER ISSUE
				// detail = intent.getStringExtra("details");
				detail = Utilities.convertdate(intent.getStringExtra("details"));
				// EU VIKALP
				type = intent.getStringExtra("type");
				id = intent.getStringExtra("id");

				social = intent.getStringExtra("socialSharing");

				name = intent.getStringExtra("fileName");
				link = intent.getStringExtra("link");
				fileLink = link;
				contentExpiry = convertdate(intent.getStringExtra("contentExpiry"));
				Log.e("announceDBAdapter", "test");
				System.out.println("title  ===> " + title);
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
				title = title.replaceAll("\\\\", "");
				summary = summary.replaceAll("\\\\", "");

				if (link != null) {
					final_name = link.substring((link.lastIndexOf('/') + 1));
				}

				else
					final_name = name;
				System.out.println("Final Name  ===> " + final_name);

				AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();

				// if(announce.getUnreadCount("Announce")>25)
				{
					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastAnnounce", id).commit();
					ApplicationLoader.getPreferences().setLastAnnouncementId(id);//ADDED VIKALP PULL SERVICE
				}

				announce.close();

				if (!type.contentEquals("text"))
					DownloadForAnnounce(ctx);
				else {

					// AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
					announce.open();

					String _id = announce.createAnnouncement(title, detail, fro,
							type, "0", summary, id, social, contentExpiry, "0")
							+ "";

					announce.close();
					notificationIntent = new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();

					generateNotification(ctx, "Announcement", title);

					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcements", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

			if (category.contentEquals("Training")) {
				Log.v("training", "In training ");

				// 'title' => $tTitle, 'mobcast' => 'Training', 'description' =>
				// $tDesc, 'fileName' => $fileName, 'details' => $todaysDate,
				// 'id' => $tID, 'link' => $fileLink, 'type' => $fileType,
				// 'socialSharing' => $socialSharing, 'contentExpiry' =>
				// $contentExpiry

				title = intent.getStringExtra("title");
				name = intent.getStringExtra("fileName");
				// SU VIKALP
				// detail = intent.getStringExtra("details");
				detail = Utilities.convertdate(intent.getStringExtra("details"));
				// EU VIKALP
				// TODO
				// TODO
				// social = intent.getStringExtra("socialSharing");
				// SA VIKALP ADDED RICH NOTIFICATION
				try {
					fileMetaData = intent.getStringExtra("fileMetaData");
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				// EA VIKALP ADDED RICH NOTIFICATION
				social = intent.getStringExtra("socialSharing");

				contentExpiry = convertdate(intent.getStringExtra("contentExpiry"));
				// if(social.contentEquals(null)) social = "off";
				// Log.d("socialSharing", social);
				summary = intent.getStringExtra("description");
				id = intent.getStringExtra("id");
				String link1 = intent.getStringExtra("link");
				link = urlencode(link1);
				fileLink = link;
				type = intent.getStringExtra("type");

				Log.v("training title", title);
				Log.v("training detail", detail);
				Log.v("training type", type);
				Log.v("training summary", summary);
				Log.v("training", link);

				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
				DownloadForTraining(ctx);

			}

			if (category.contentEquals("News")) {

				title = intent.getStringExtra("title");
				name = intent.getStringExtra("fileName");
				// SU VIKALP DATE ORDER
				// detail = intent.getStringExtra("details");
				detail = Utilities.convertdate(intent.getStringExtra("details"));
				try{
					contentExpiry = convertdate(intent.getStringExtra("contentExpiry"));
				}catch(Exception e){
					
				}
				// EU VIKALP
				String link1 = intent.getStringExtra("link");
				summary = intent.getStringExtra("description");
				url = intent.getStringExtra("source");

				id = intent.getStringExtra("id");
				type = intent.getStringExtra("type");
				social = intent.getStringExtra("socialSharing");

				// 'title' => $nTitle , 'source' => $nSource, 'mobcast' => 'News',
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
				// Log.d(TAG, title);
				// Log.d(TAG, detail);
				// Log.d(TAG, caption);
				// Log.d(TAG, summary);
				Log.d(TAG, link);

				final_name = link.substring((link.lastIndexOf('/') + 1));
				System.out.println("Final Name  ===> " + final_name);

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();

				// if(announce.getUnreadCount("News")>25)
				{
					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastNews", id).commit();
					ApplicationLoader.getPreferences().setLastNewsId(id);//ADDED VIKALP PULL SERVICE
				}
				db.close();

				if (!type.contentEquals("text"))
					DownloadForNews(ctx, url);
				else {

					// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
					db.open();
					String _id = db.createNews(title, detail, name, url, type,
							summary, id, social, contentExpiry, "0") + "";
					Log.d(TAG, "Training added to list");
					db.close();
					notificationIntent = new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					generateNotification(ctx, "News", title);

					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

			if (category.contentEquals("Recruitment")) {

				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
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

						Intent i = new Intent(GCMIntentService.this.ctx,
								RecruitList.class);
						i.putExtra(RecruitList.RECRUIT_DATA, result);
						// startActivity(i);
						notificationIntent = i;
						generateNotification(GCMIntentService.this.ctx, "",
								"New Recruitment");
					}
				});

				// SA VIKALP UPDATE NOTIFICATION RECEIVED
				try {
					updateNotificationReceived(ctx, "Recruitment", id);
				} catch (Exception ex) {
					Log.e("updateNotificationReceived", ex.toString());
				}
				// EA VIKALP UPDATE NOTIFICATION RECEIVED

				// RecruitTask asyncHttpPost1 = new RecruitTask();

				// asyncHttpPost1
				// .execute(com.mobcast.util.Constants.CHECK_RECRUITMENT);;

			}// End of Recruitment category

			// For Award Category
			if (category.contentEquals("Award")) {

				// Getting Data from pushed notification
				id = intent.getStringExtra("id");// server award id for the award
				Log.e("award id", id + "");
				name = intent.getStringExtra("winnerName");// Person who won the
															// award
				// name = "winner";// Person who won the award
				link = intent.getStringExtra("link");// link to download the image
				link = link.replace(" ", "%20");
				fileLink = link;
				title = intent.getStringExtra("title");// Title of the award
				// SU VIKALP DATE ORDER ISSUE
				// detail = intent.getStringExtra("details");// Time detail
				detail = Utilities.convertdate(intent.getStringExtra("details"));// Time
																					// detail
				rdate = intent.getStringExtra("timeStamp").substring(0, 10);// ADDED
																			// VIKALP
																			// AWARD
																			// RDATE
				// EU VIKALP DATE ORDER ISSUE
				// TODO
				// TODO
				// social = intent.getStringExtra("socialSharing");
				social = intent.getStringExtra("socialSharing");

				// contentExpiry = convertdate
				// (intent.getStringExtra("contentExpiry"));
				contentExpiry = convertdate("off");
				summary = intent.getStringExtra("description");// Summary of the
																// award
				imgName = intent.getStringExtra("fileName");// Name of image file

				// DownloadImage from Link Code
				try {
					URL url = new URL(link);
					String root = Environment.getExternalStorageDirectory()
							.toString();
					File myDir = new File(root + Constants.APP_FOLDER_IMG);
					myDir.mkdirs();
					String fname = imgName;
					File file = new File(myDir, fname);
					if (file.exists())
						file.delete();
					
					imagePath = root + Constants.APP_FOLDER_IMG + fname;
					Log.v("image name", fname);
					
					AnnounceDBAdapter db = new AnnounceDBAdapter(this);
					db.open();
					String _id = db.createAward(title, name, detail, rdate, id,
							summary, // ADDED VIKALP AWARD RDATE
							imagePath, social, contentExpiry, fileLink) + "";
						SharedPreferences pref;
						pref = getSharedPreferences("MobCastPref", 0);
						pref.edit().putString("lastAward", id).commit();
						ApplicationLoader.getPreferences().setLastAwardsId(id);//ADDED VIKALP PULL SERVICE
						db.close();
					
					try{
						URLConnection ucon = url.openConnection();
						InputStream is = ucon.getInputStream();
						BufferedInputStream bis = new BufferedInputStream(is);
						ByteArrayBuffer baf = new ByteArrayBuffer(50);
						int current = 0;
						while ((current = bis.read()) != -1) {
							baf.append((byte) current);
						}

						FileOutputStream fos = new FileOutputStream(file);
						fos.write(baf.toByteArray());
						fos.close();
						
						// Storing in Database
						
						// Passing intent to Award with data
						if (check.isLoggedIn()) {
							notificationIntent = new Intent(new OpenContent(ctx,
									AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());
						} else
							notificationIntent = new Intent(ctx, LoginV2.class);
//						generateNotification(ctx, "Award", title);
						
//						SA VIKALP RICH NOTIFICATION
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
							generateAwardNotification(ctx, "Award", title,
									file.getAbsolutePath());
						} else {
							generateNotification(ctx, "Award", title);
						}
//						EA VIKALP RICH NOTIFICATION
						
						// SA VIKALP UPDATE NOTIFICATION RECEIVED
						try {
							updateNotificationReceived(ctx, "Award", id);
						} catch (Exception ex) {
							Log.e("updateNotificationReceived", ex.toString());
						}
						// EA VIKALP UPDATE NOTIFICATION RECEIVED
					}catch(Exception e){
						if (check.isLoggedIn()) {
							notificationIntent = new Intent(new OpenContent(ctx,
									AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());
						} else
							notificationIntent = new Intent(ctx, LoginV2.class);
						
						generateNotification(ctx, "Award", title);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}// end of Award Category

			// SA VIKALP
			// For Remote Folder Wipe
			if (category.contentEquals("remoteFolderWipe")) {
				AnnounceDBAdapter adb = new AnnounceDBAdapter(ctx);
				adb.open();
				adb.remoteWipeFolder(ctx);
				adb.close();
			}

			if (category.contentEquals("askUserToLogin")) {
				if (!check.isLoggedIn()) {
					notificationIntent = new Intent(ctx, LoginV2.class);
					generateLoginNotification(ctx);
					clearPreferences();
				}
			}

			if (category.contentEquals("newUpdate")) {
				notificationIntent = new Intent(ctx, Home1.class);
				generateUpdateNotification(ctx);
			}
			
			if(category.contentEquals("changeTeamIncentive")){
				new AsyncDataFromApiForIncentive().execute();
			}
			
			if (category.contentEquals("myperformance")) {
				notificationIntent = new Intent(ctx, MyPerformanceActivity.class);
				generateMyPerformanceNotification(ctx);
			}
			// EA VIKALP
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}// end of onMessage

	public void clearPreferences() {
		try {
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			pref.edit().putString("lastEvent", "0").commit();
			pref.edit().putString("lastAnnounce", "0").commit();
			pref.edit().putString("lastTraining", "0").commit();
			pref.edit().putString("lastNews", "0").commit();
			pref.edit().putString("lastAward", "0").commit();
			getSharedPreferences("MobCastPref", 0).edit().clear().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void readFromXml() throws InterruptedException, ExecutionException {
		boolean isInternetPresent;

		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
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

			AnnounceDBAdapter db = new AnnounceDBAdapter(this);

			for (j = 0; j < i; j++) {
				JSONObject jObject1 = jsonArray.getJSONObject(j);
				String Fedbackrow = jObject1.getString("feedbackQuestion");
				String totalQuestions = jObject1.getString("totalQuestions");
				JSONObject jObject = new JSONObject(Fedbackrow);

				String feedbackID = jObject.getString("feedbackID");
				// SA ADDED VIKALP FEEDBACKID SAVE
				try {
					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastFeedback", feedbackID).commit();
					ApplicationLoader.getPreferences().setLastFeedbackId(feedbackID);//ADDED VIKALP PULL SERVICE
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				// EA ADDED VIKALP FEEDBACKID SAVE
				String feedbackTitle = jObject.getString("feedbackTitle");
				String feedbackDescription = jObject
						.getString("feedbackDescription");
//				String feedbackDate = jObject.getString("feedbackDate"); //ADDED VIKALP FEEDBACK DATE SORT ORDER
				String feedbackDate = Utilities.convertdate(jObject.getString("feedbackDate"));
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
				feedback.setFeedbacktitle(feedbackTitle);
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

	private void generateNotification(Context ctx, String message, String title) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
					.setContentTitle("New Mobcast " + message)
					.setContentText(title);

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
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

	// SA VIKALP RICH NOTIFICATION
		private void generateEventNotification(Context ctx, String message,
				String title, String dbID) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);

				Intent yesIntent = new Intent(this, EventYesReceiver.class);
				yesIntent.putExtra("id", id);
				yesIntent.putExtra("dbID", dbID);
				PendingIntent yesPendingIntent = PendingIntent.getBroadcast(this,
						0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				Intent noIntent = new Intent(this, EventNoReceiver.class);
				noIntent.putExtra("id", id);
				noIntent.putExtra("dbID", dbID);
				PendingIntent noPendingIntent = PendingIntent.getBroadcast(this, 0,
						noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				Intent calIntent = new Intent(this, EventCalendarReceiver.class);
				calIntent.putExtra("id", id);
				calIntent.putExtra("dbID", dbID);
				PendingIntent calPendingIntent = PendingIntent.getBroadcast(this,
						0, calIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle("New Mobcast " + message)
						.setContentText(title)
						.addAction(android.R.drawable.ic_menu_directions, "Yes",
								yesPendingIntent)
						.addAction(android.R.drawable.ic_menu_close_clear_cancel,
								"No", noPendingIntent)
						.addAction(android.R.drawable.ic_menu_my_calendar,
								"Add to Calendar", calPendingIntent)
						.setDefaults(-1).setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

		private void generateFeedbackNotification(Context ctx, String message,
				String title, String question) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				
				String nQuestions = "No of questions : " + question;
				
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationCompat.Builder mBuilder;
				mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle(message)
						.setContentText(title)
						.addAction(R.drawable.feedback, nQuestions,
								resultPendingIntent)
						.setDefaults(-1)
						.setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				// Notification notification = new Notification.InboxStyle(mBuilder)
				// .setSummaryText("vikalppatelce@yahoo.com")
				// .build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
		
		private void generateAudioNotification(Context ctx, String message,
				String title, String filePath) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationCompat.Builder mBuilder;
				mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle("New Mobcast " + message)
						.setContentText(title)
						.setContentInfo(Utilities.getFileSize(filePath))
						// .setContentInfo(Utilities.getAudioDuration(filePath))
						.addAction(android.R.drawable.ic_media_play, "Play",
								resultPendingIntent)
						.addAction(android.R.drawable.ic_menu_info_details,
								Utilities.getAudioDuration(filePath),
								resultPendingIntent).setDefaults(-1)
						.setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				// Notification notification = new Notification.InboxStyle(mBuilder)
				// .setSummaryText("vikalppatelce@yahoo.com")
				// .build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

		private void generateVideoNotification(Context ctx, String message,
				String title, String filePath) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationCompat.Builder mBuilder;
				mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle("New Mobcast " + message)
						.setContentText(title)
						.setContentInfo(Utilities.getFileSize(filePath))
						// .setContentInfo(Utilities.getAudioDuration(filePath))
						.addAction(android.R.drawable.ic_media_play, "Play",
								resultPendingIntent)
						.addAction(
								android.R.drawable.ic_menu_info_details,
								Utilities.getAudioDuration(filePath) + " , "
										+ Utilities.getFileSize(filePath),
								resultPendingIntent).setDefaults(-1)
						.setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				Notification notification = new NotificationCompat.BigPictureStyle(
						mBuilder).bigPicture(
						Utilities.getVideoLayerThumbnail(filePath)).build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				SessionManagement sm = new SessionManagement(ctx);
				Random r = new Random();
				if (sm.isLoggedIn()) {
					mNotificationManager.cancel(434);
					mNotificationManager.notify(434, notification);
				} else {
					Log.e("logged out", "notificaiton received");
				}

			}
		}
		
		private void generateAwardNotification(Context ctx, String message,
				String title, String filePath) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
				
				Intent congratulateIntent = new Intent(this, AwardCongratulateReceiver.class);
				congratulateIntent.putExtra("id", id);
				PendingIntent congratulatePendingIntent = PendingIntent.getBroadcast(this,
						0, congratulateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				
				NotificationCompat.Builder mBuilder;
				mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle("New Mobcast " + message)
						.setContentText(title)
						.addAction(R.drawable.recruitmentswhite, "Congratulate",
								congratulatePendingIntent)
						.setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				
				Notification notification = new NotificationCompat.BigPictureStyle(
						mBuilder).bigPicture(
						BitmapFactory.decodeFile(filePath, options)).build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				SessionManagement sm = new SessionManagement(ctx);
				Random r = new Random();
				if (sm.isLoggedIn()) {
					mNotificationManager.cancel(434);
					mNotificationManager.notify(434, notification);
				} else {
					Log.e("logged out", "notificaiton received");
				}

			}
		}

		private void generateFileNotification(Context ctx, String message,
				String title, String filePath) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				String messageInfo = "detail not found";
				String mExtension = "doc";
				try {
					if (!TextUtils.isEmpty(fileMetaData)) {
						messageInfo = fileMetaData;
					} else {
						messageInfo = "detail not found";
					}

					if (!TextUtils.isEmpty(type)) {
						mExtension = type;
					} else {
						mExtension = "doc";
					}

				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
				int typeIcon = R.drawable.documentwhite;

				if (mExtension.equalsIgnoreCase("doc")
						|| mExtension.equalsIgnoreCase("docx")) {
					typeIcon = R.drawable.word;
				} else if (mExtension.equalsIgnoreCase("pdf")) {
					typeIcon = R.drawable.pdf;
				} else if (mExtension.equalsIgnoreCase("xls")
						|| mExtension.equalsIgnoreCase("xlsx")) {
					typeIcon = R.drawable.excel;
				} else if (mExtension.equalsIgnoreCase("ppt")
						|| mExtension.equalsIgnoreCase("pptx")) {
					typeIcon = R.drawable.powerpoint;
				} else {
					typeIcon = R.drawable.documentwhite;
				}

				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);
				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
				NotificationCompat.Builder mBuilder;
				mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle(message)
						.setContentText(title)
						.setContentInfo(Utilities.getFileSize(filePath))
						// .setContentInfo(Utilities.getAudioDuration(filePath))
						.addAction(typeIcon, "Open", resultPendingIntent)
						.addAction(android.R.drawable.ic_menu_info_details,
								messageInfo, resultPendingIntent).setDefaults(-1)
						.setOnlyAlertOnce(true).setAutoCancel(true)
						.setContentIntent(resultPendingIntent);

				// Notification notification = new Notification.InboxStyle(mBuilder)
				// .setSummaryText("vikalppatelce@yahoo.com")
				// .build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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

		@SuppressLint("NewApi")
		private void generateImageNotification(Context ctx, String message,
				String title, String mImagePath) {
			// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {

				PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
						uniqueID, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle(message)
						.setContentText(title)
						.addAction(android.R.drawable.ic_menu_gallery, title,
								resultPendingIntent);

				mBuilder.setDefaults(-1);
				mBuilder.setOnlyAlertOnce(true);
				mBuilder.setAutoCancel(true);
				// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;

				mBuilder.setContentIntent(resultPendingIntent);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;

				Notification notification = new NotificationCompat.BigPictureStyle(
						mBuilder).bigPicture(
						BitmapFactory.decodeFile(mImagePath, options)).build();

				NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				SessionManagement sm = new SessionManagement(ctx);
				Random r = new Random();
				if (sm.isLoggedIn()) {
					mNotificationManager.cancel(444);
					mNotificationManager.notify(444, notification);
				} else {
					Log.e("logged out", "notificaiton received");
				}
			}
		}

		// EA VIKALP RICH NOTIFICATION
		
	private void generateLoginNotification(Context ctx) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
				.setContentTitle("Your not loggedin to Sanofi")
				.setContentText("Please login to Sanofi");

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
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}

	private void generateUpdateNotification(Context ctx) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
				.setContentTitle("A new update is available.")
				.setContentText("Please update the application.");

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
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}
	
	private void generateMyPerformanceNotification(Context ctx) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
				.setContentTitle("A new data for My Performance is available")
				.setContentText("Please open the application to view it");

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
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}

	/*
	 * private void generateNotification(Context ctx, String message,String
	 * title) { SharedPreferences prefs = PreferenceManager
	 * .getDefaultSharedPreferences(ctx); if
	 * (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) { int icon
	 * = R.drawable.ic_launcher; long when = System.currentTimeMillis();
	 * 
	 * NotificationManager nm = (NotificationManager) ctx
	 * .getSystemService(Context.NOTIFICATION_SERVICE); Notification
	 * notification = new Notification(icon, title, when);
	 * 
	 * PendingIntent pintent; String main_title = "Mobcast "+message;
	 * 
	 * // context.sendBroadcast(event); // set intent so it does not start a new
	 * activity notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	 * Intent.FLAG_ACTIVITY_SINGLE_TOP);
	 * 
	 * pintent = PendingIntent.getActivity(ctx, uniqueID, notificationIntent,
	 * PendingIntent.FLAG_UPDATE_CURRENT);
	 * 
	 * notification.setLatestEventInfo(ctx, main_title, title, pintent);
	 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
	 * 
	 * // Play default notification sound notification.defaults |=
	 * Notification.DEFAULT_SOUND;
	 * 
	 * // Vibrate if vibrate is enabled notification.defaults |=
	 * Notification.DEFAULT_VIBRATE;
	 * 
	 * nm.notify(uniqueID, notification);
	 * 
	 * Intent intent = new Intent("MyGCMMessageReceived");
	 * ctx.sendBroadcast(intent); } }
	 */

	@Override
	protected void onRegistered(Context ctx, String regID) {
		// TODO Auto-generated method stub
		// send regID to your server

		Log.d(TAG, regID);
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String email = pref.getString("name", null);
		Editor edit = pref.edit();

		edit.putString("gcmregID", regID).commit();
		edit.putLong("Timestamp", System.currentTimeMillis());
		edit.commit();

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("update", "yes");
		params.put("id", regID);
		params.put("email", email);
		params.put("companyID", getString(R.string.companyID));

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
		asyncHttpPost.execute(com.mobcast.util.Constants.M_UPDATE);
	}

	@Override
	protected void onUnregistered(Context ctx, String regID) {
		// TODO Auto-generated method stub
		// send notification to your server to remove that regID
	}

	public void DownloadFromUrl(Context ctx) { // this is the downloader method
		try {
			URL url = new URL(link);
			String root = Environment.getExternalStorageDirectory().toString();
			if (type.contentEquals("image")) {
				File myDir = new File(root + Constants.APP_FOLDER_IMG);
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

				String imgPath = root + Constants.APP_FOLDER_IMG + fname;

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createAnnouncement(title, detail, fro, type,
						fname, summary, id, social, contentExpiry, imgPath)
						+ "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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
					if (check.isLoggedIn()) {

						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

						generateNotification(ctx, "Announcement", title);
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {

						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

						generateNotification(ctx, "Announcement", title);
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
				}
			}
			if (type.contentEquals("video")) {

				File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
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

				String link = root + Constants.APP_FOLDER_IMG + name;

				AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();
				String _id = announce.createAnnouncement(title, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				announce.close();
				try {
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
					 * // // * Read bytes to the Buffer until there is nothing
					 * more to read(-1). // ByteArrayBuffer baf = new
					 * ByteArrayBuffer(50); int current = 0; while ((current =
					 * bis.read()) != -1) { baf.append((byte) current); }
					 * 
					 * // Convert the Bytes read to a String. FileOutputStream
					 * fos = new FileOutputStream(file);
					 * fos.write(baf.toByteArray()); fos.close();
					 */

					// bookmarkend
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
						generateNotification(ctx, "Announcement", title);
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
						generateNotification(ctx, "Announcement", title);
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
				}
			}
			if (type.contentEquals("audio")) {
				Log.v("training", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + Constants.APP_FOLDER_AUDIO);
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();
				String _id = announce.createAnnouncement(title, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				announce.close();
				try {
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

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
						generateNotification(ctx, "Announcement", title);

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", title);
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {
					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
						generateNotification(ctx, "Announcement", title);

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", title);
				}
			}// end of audio

			generateNotification(ctx, "Announcement", title);

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}catch(Exception e){
			
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

				File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
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

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();
					/*
					 * Define InputStreams to read from the URLConnection.
					 */
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
					if (check.isLoggedIn()) {

						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateVideoNotification(ctx, "New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
					
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}

					if (check.isLoggedIn()) {

						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}
			if (type.contentEquals("pdf")) {
				Log.v("training", "in pdf");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ Constants.APP_FOLDER
						+ getResources().getString(R.string.pdf));
				myDir.mkdirs();

				// String fname = ename;
				String fname = ename;
				Log.v("name of pdf", fname);
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification",
								title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}// end of type is pdf

			if (type.equals("ppt")) {
				Log.v("training", "in ppt");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ Constants.APP_FOLDER
						+ getResources().getString(R.string.ppt));
				myDir.mkdirs();
				// String fname = ename;
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// long startTime = System.currentTimeMillis();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification",
								title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
					
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {
					}
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}// end of ppt

			if (type.contentEquals("audio")) {
				Log.v("training", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + Constants.APP_FOLDER_AUDIO);
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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

					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(ctx, "New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {
					}
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}// end of audio

			if (type.equals("xls")) {
				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ Constants.APP_FOLDER
						+ getResources().getString(R.string.xls));
				myDir.mkdirs();
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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

					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification",
								title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}// end of xls

			if (type.equals("doc")) {
				Log.v("training", "in doc");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ Constants.APP_FOLDER
						+ getResources().getString(R.string.doc));
				myDir.mkdirs();
				String fname = ename;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				db.close();
				try {
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
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "New Training Notification",
//							title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "New Training Notification",
								title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "New Training Notification",
							title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}// end of doc

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}catch(Exception e){
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
				File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createAnnouncement(title, detail, fro, type,
						name, summary, id, social, contentExpiry, fileLink)
						+ "";
				Log.d(TAG, "Announcement added to list");
				db.close();
				try {
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
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
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "Announcement", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateVideoNotification(ctx, "Announcements", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "Announcements", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
					
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

			else if (type.contentEquals("audio")) {

				Log.v("Announcement", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + Constants.APP_FOLDER_AUDIO);
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();
				// announce.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				String _id = announce.createAnnouncement(title, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				announce.close();
				try {
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
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "Announcement", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(ctx, "Announcements", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "Announcements", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
					
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "Announcement", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			} else if (type.contentEquals("image")) {

				File myDir = new File(root + Constants.APP_FOLDER_IMG);
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

				// String link = root + "/.mobcast/mobcast_images/" + name;

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				// db.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				// db.createNews(link, root, fname, link, fname, url1, link);
				String _id = db.createAnnouncement(title, detail, fro, type,
						final_name, summary, id, social, contentExpiry,
						fileLink)
						+ "";
				Log.d(TAG, "Announcement added to list");
				db.close();
				try {
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
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
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

					} else
						notificationIntent = new Intent(ctx,
								AnnounceListView.class);
//					generateNotification(ctx, "Announcements", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateImageNotification(ctx, "Announcements", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "Announcements", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED

				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

					} else
						notificationIntent = new Intent(ctx,
								AnnounceListView.class);
					generateNotification(ctx, "Announcements", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		} catch(Exception e){
			
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
				File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
				myDir.mkdirs();
				String fname = name;
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createNews(title, detail, name, prlink, type,
						summary, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "News added to list");
				db.close();
				try {
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
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
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "News", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateVideoNotification(ctx, "News", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "News", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
					
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "News", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

			else if (type.contentEquals("audio")) {

				Log.v("News", "in audio");

				File myDir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + Constants.APP_FOLDER_AUDIO);
				myDir.mkdirs();
				String fname = name;

				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();

				AnnounceDBAdapter announce = new AnnounceDBAdapter(this);
				announce.open();
				// announce.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				String _id = announce.createNews(title, detail, name, prlink,
						type, summary, id, social, contentExpiry, fileLink)
						+ "";
				announce.close();
				try {
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

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

//					generateNotification(ctx, "News", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(ctx, "News", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "News", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED

				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					generateNotification(ctx, "News", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			} else if (type.contentEquals("image")) {

				File myDir = new File(root + Constants.APP_FOLDER_IMG);
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

				// String link = root + "/.mobcast/mobcast_images/" + name;

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				// db.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				// db.createNews(link, root, fname, link, fname, url1, link);
				String _id = db.createNews(title, detail, final_name, prlink,
						type, summary, id, social, contentExpiry, fileLink)
						+ "";
				Log.d(TAG, "News added to list");
				db.close();
				try {
					/* Open a connection to that URL. */
					URLConnection ucon = url.openConnection();

					/*
					 * Define InputStreams to read from the URLConnection.
					 */
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
					Log.d("ImageManager",
							"download ready in"
									+ ((System.currentTimeMillis() - startTime) / 1000)
									+ " sec");

					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					} else
						notificationIntent = new Intent(ctx, NewsList.class);
//					generateNotification(ctx, "News", title);
					
					// SA VIKALP RICH NOTIFICATION
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateImageNotification(ctx, "News", title,
								file.getAbsolutePath());
					} else {
						generateNotification(ctx, "News", title);
					}
					// EA VIKALP RICH NOTIFICATION
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (check.isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					} else
						notificationIntent = new Intent(ctx, NewsList.class);
					generateNotification(ctx, "News", title);
					
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "News", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}catch(Exception e){
			
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

	// SA VIKALP UPDATE NOTIFICATION RECEIVED
	public void updateNotificationReceived(Context mContext,
			String mModuleName, String id) {
		Reports reportsObj = new Reports(mContext, mModuleName);
		reportsObj.updateNotification(id);
	}
	// EA VIKALP UPDATE NOTIFICATION RECEIVED

	/*
	 * FETCH INCENTIVE PRODUCT UPDATE
	 */
	public class AsyncDataFromApiForIncentive extends AsyncTask<Void, Void, Void>{
		private boolean isProductData = false;
		private String strJSONData;
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String temp1 = pref.getString("name", "tushar@mobcast.in");
			
			HashMap<String, String> formValue = new HashMap<String, String>();
			formValue.put("device", "android");
			if(!BuildVars.debug){
				formValue.put(com.mobcast.util.Constants.user_id, temp1);
			}else{
				formValue.put(com.mobcast.util.Constants.user_id, "tushar@mobcast.in");
			}

			String str = RestClient
					.postData(Constants.INCEN_PRODUCT, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray  mJSONArray = mJSONObj.getJSONArray("products");
				if(mJSONArray.length() > 0){
					isProductData = true;
					strJSONData = str;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isProductData){
				ApplicationLoader.getPreferences().setProductJSON(strJSONData);
				ApplicationLoader.getPreferences().setIncenFirstTime(false);
				new AsyncBaseDataFromApi().execute();
			}
		}
	}
	
	public class AsyncBaseDataFromApi extends AsyncTask<Void, Void, Void>{
		private boolean isMonthQuarterData = false;
		private String strJSONData;
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String temp1 = pref.getString("name", "tushar@mobcast.in");
			
			HashMap<String, String> formValue = new HashMap<String, String>();
			formValue.put("device", "android");
			if(!BuildVars.debug){
				formValue.put(com.mobcast.util.Constants.user_id, temp1);
			}else{
				formValue.put(com.mobcast.util.Constants.user_id, "tushar@mobcast.in");
			}

			String str = RestClient
					.postData(Constants.INCEN_BASE, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray  mJSONArray = mJSONObj.getJSONArray("month");
				if(mJSONArray.length() > 0){
					isMonthQuarterData = true;
					strJSONData = str;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isMonthQuarterData){
				ApplicationLoader.getPreferences().setMonthQuarterJSON(strJSONData);
				ApplicationLoader.getPreferences().setIncenFirstTime(false);
				generateChangeTeamUpdateNotification(ctx);
			}
		}
	}
	
	private void generateChangeTeamUpdateNotification(Context ctx) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("User has changed his/her team!")
				.setContentText("Your incentive product has been updated.");

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
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}
	
}// end of GCMIntentService