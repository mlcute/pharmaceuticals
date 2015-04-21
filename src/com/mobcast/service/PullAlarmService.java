package com.mobcast.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
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
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.mobcast.receiver.AwardCongratulateReceiver;
import com.mobcast.receiver.EventCalendarReceiver;
import com.mobcast.receiver.EventNoReceiver;
import com.mobcast.receiver.EventYesReceiver;
import com.mobcast.receiver.PullAlarmReceiver;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RequestBuilder;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.AnnounceListView;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.ConnectionDetector;
import com.sanofi.in.mobcast.FeedbackDBHandler;
import com.sanofi.in.mobcast.Home1;
import com.sanofi.in.mobcast.LoginV2;
import com.sanofi.in.mobcast.MainActivity;
import com.sanofi.in.mobcast.NewsList;
import com.sanofi.in.mobcast.OpenContent;
import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.Reports;
import com.sanofi.in.mobcast.SessionManagement;
import com.sanofi.in.mobcast.TrainingListView;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class PullAlarmService extends IntentService {
	private String mResponseFromApi;
    public PullAlarmService() {
        super("SchedulingService");
    }
    
    public static final String TAG = PullAlarmService.class.getSimpleName();

    @Override
    protected void onHandleIntent(Intent intent) {
        // BEGIN_INCLUDE(service_onhandle)
        // The URL from which to fetch content.
    	try {
			if(Utilities.isInternetConnected()){
				mResponseFromApi = RestClient.postJSON(Constants.PULL_SERVICE, RequestBuilder.getPostPullAlarmService());
				JSONObject mJSONObject = new JSONObject(mResponseFromApi);
				if(mJSONObject.getBoolean("success")){
				parseJSONFromApi(mResponseFromApi);	
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception ex){
			ex.printStackTrace();
		}
    	if(BuildVars.debugAlarm){
    		Utilities.devAlarm(ApplicationLoader.getApplication().getApplicationContext());
    	}
        // Release the wake lock provided by the BroadcastReceiver.
        PullAlarmReceiver.completeWakefulIntent(intent);
        // END_INCLUDE(service_onhandle)
    }
    
    /*
     * Parse JSON From Api
     */
    private void parseJSONFromApi(String mResponseFromApi){
    	try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			JSONArray mJSONAnnouncementArr = mJSONObject.getJSONArray("announcement");
			JSONArray mJSONEventArr = mJSONObject.getJSONArray("events");
			JSONArray mJSONNewsArr = mJSONObject.getJSONArray("news");
			JSONArray mJSONAwardsArr = mJSONObject.getJSONArray("awards");
			JSONArray mJSONTrainingArr = mJSONObject.getJSONArray("training");
			JSONArray mJSONFeedbackArr = mJSONObject.getJSONArray("feedback");
			
			parseJSONForAnnouncement(mJSONAnnouncementArr);
			parseJSONForEvent(mJSONEventArr);
			parseJSONForNews(mJSONNewsArr);
			parseJSONForTraining(mJSONTrainingArr);
			parseJSONForAwards(mJSONAwardsArr);
			parseJSONForFeedback(mJSONFeedbackArr);
			parseJSONForRemoteWipe(mJSONObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception ex){
			ex.printStackTrace();
		}
    }
    
    private void parseJSONForAnnouncement(JSONArray mJSONAnnouncementArr){
		for (int i = 0; i < mJSONAnnouncementArr.length(); i++) {
    		try {
    			String ID,title,fro,summary,type,link,detail,ss,expiry,category,name;
    			String readStatus = "false";
    			category = "announcement";
				JSONObject mJSONObject = mJSONAnnouncementArr.getJSONObject(i);
				ID = mJSONObject.getString("ID");
				title = mJSONObject.getString("title");
				fro = mJSONObject.getString("fro");
				summary = mJSONObject.getString("summary");
				type = mJSONObject.getString("type");
				link = mJSONObject.getString("link");
				detail = Utilities.convertdate(mJSONObject.getString("detail"));
				ss = mJSONObject.getString("ss");
				expiry = Utilities.convertdate(mJSONObject.getString("expiry"));
				name = link.substring((link.lastIndexOf('/') + 1));
				
				//SA ADDED VIKALP READ STATUS
				try{
					readStatus =mJSONObject
							.getString("readStatus"); 	
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
				//SA ADDED VIKALP READ STATUS
				
				fro = fro.replaceAll("\\\\", "");
				title = title.replaceAll("\\\\", "");
				summary = summary.replaceAll("\\\\", "");

				if (!type.contentEquals("text")){
					DownloadForAnnounce(ApplicationLoader.getApplication()
							.getApplicationContext(), type, link, name, title,
							detail, fro, summary, ID, ss, expiry, link, readStatus);
				}
				else {

					AnnounceDBAdapter announce = new AnnounceDBAdapter(ApplicationLoader.getApplication().getApplicationContext());
					announce.open();

					String _id = announce.createAnnouncement(title, detail, fro,
							type, "0", summary, ID, ss, expiry, "0")
							+ "";
					
					//SA VIKALP READ STATUS
					if (readStatus.contentEquals("true")) {
						announce.readrow(_id + "",
								AnnounceDBAdapter.SQLITE_ANNOUNCE);
					}
					//EA VIKALP READ STATUS

					announce.close();
					ApplicationLoader.getPreferences().setLastAnnouncementId(ID);
					Intent notificationIntent = new OpenContent(ApplicationLoader.getApplication().getApplicationContext(),
							AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView();

					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastAnnounce", ID).commit();
					ApplicationLoader.getPreferences().setLastAnnouncementId(ID);//ADDED VIKALP PULL SERVICE
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE BULK NOTIFICATION STOP
						generateNotification(ApplicationLoader.getApplication().getApplicationContext(), "Announcement", title,notificationIntent);	
					}//ADDED VIKALP PULL SERVICE BULK NOTIFICATION STOP
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ApplicationLoader.getApplication().getApplicationContext(), "Announcements", ID);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
    	}
    }
    
    private void parseJSONForEvent(JSONArray mJSONEventArr){
    	for(int i = 0 ;i< mJSONEventArr.length() ;i++){
    		try{
    			String ID, title,date,detail,venue,day,time,ss,calendarEnabled,rsvpNeeded,expiry,desc,category;
    			String endTime ="99:99amm";
    			String readStatus = "false";
    			category = "events";
    			JSONObject mJSONObject = mJSONEventArr.getJSONObject(i);
    			ID = mJSONObject.getString("ID");
    			title = mJSONObject.getString("title");
    			date = mJSONObject.getString("date");
    			detail = Utilities.convertdate(mJSONObject.getString("detail"));
    			venue =  mJSONObject.getString("venue");
    			day = mJSONObject.getString("day");
    			time = mJSONObject.getString("time");
    			ss = mJSONObject.getString("ss");
    			calendarEnabled = mJSONObject.getString("calendarEnabled");
    			rsvpNeeded = mJSONObject.getString("rsvpNeeded");
    			expiry = mJSONObject.getString("expiry");
    			desc = mJSONObject.getString("desc");
    			try{
    				endTime = mJSONObject.getString("endTime"); //ADDED VIKALP EVENT END TIME	
    			}catch(Exception e){
    			}
    			
    			
    			//SA VIKALP READ STATUS
    			try{
					readStatus = mJSONObject
							.getString("readStatus");
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
    			//EA VIKALP READ STATUS
    			
    			AnnounceDBAdapter db = new AnnounceDBAdapter(ApplicationLoader.getApplication().getApplicationContext());
    			// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
    			db.open();
    			String _id = String.valueOf(db.createEvent(title, date, day, time, venue, desc,
    					ID, ss, expiry, rsvpNeeded, calendarEnabled,endTime)
    					+ "");

    			db.eventDetails(detail, ID);//ADDED VIKALP PULL SERVICE NOTIFICATION STOP BULK
    			
    			{
    				SharedPreferences pref;
    				pref = getSharedPreferences("MobCastPref", 0);
    				pref.edit().putString("lastEvent", ID).commit();
    				ApplicationLoader.getPreferences().setLastEventsId(ID);//ADDED VIKALP PULL SERVICE
    			}
    			
    			if (readStatus.contentEquals("true")) {
    				db.readrow(_id + "", AnnounceDBAdapter.SQLITE_EVENT);
				}

    			db.close();
    			Intent notificationIntent = new OpenContent(ApplicationLoader.getApplication().getApplicationContext(),
    					AnnounceDBAdapter.SQLITE_EVENT, _id).itemView();

    			// SA VIKALP RICH NOTIFICATION
    			if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
    			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
    				generateEventNotification(ApplicationLoader.getApplication().getApplicationContext(), "Event", title, _id,ID ,notificationIntent);
    			} else {
    				generateNotification(ApplicationLoader.getApplication().getApplicationContext(), "Event", title,notificationIntent);
    			}
    			}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
    			// EA VIKALP RICH NOTIFICATION
    			
    			// SA VIKALP UPDATE NOTIFICATION RECEIVED
    			try {
    				updateNotificationReceived(ApplicationLoader.getApplication().getApplicationContext(), "Events", ID);
    			} catch (Exception ex) {
    				Log.e("updateNotificationReceived", ex.toString());
    			}
    			// EA VIKALP UPDATE NOTIFICATION RECEIVED	
    		} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
    	}
    }
    
    private void parseJSONForNews(JSONArray mJSONNewsArr){
    	for(int i = 0 ; i < mJSONNewsArr.length() ; i++){
    		try {
    			String ID,title,fro,summary,type,link,detail,ss,expiry,name,category;
    			String readStatus = "false";
    			category = "news";
				JSONObject mJSONObject = mJSONNewsArr.getJSONObject(i);
				ID = mJSONObject.getString("ID");
				title = mJSONObject.getString("title");
				fro = mJSONObject.getString("fro");
				summary = mJSONObject.getString("summary");
				type = mJSONObject.getString("type");
				link = mJSONObject.getString("link");
				detail = Utilities.convertdate(mJSONObject.getString("detail"));
				ss = mJSONObject.getString("ss");
				expiry = mJSONObject.getString("expiry");
				
				//SA VIKALP READ STATUS
				try{
					readStatus = mJSONObject
							.getString("readStatus"); 
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
				//EA VIKALP READ STATUS
				
				link = link.replace(" ", "%20");
				name= link.substring((link.lastIndexOf('/') + 1));
				
				if (!type.contentEquals("text"))
					DownloadForNews(ApplicationLoader.getApplication().getApplicationContext(), link,link,type,name,title,detail,summary,ID,ss,link,expiry,readStatus);
				else {
					AnnounceDBAdapter db = new AnnounceDBAdapter(ApplicationLoader.getApplication().getApplicationContext());
					db.open();
					String _id = db.createNews(title, detail, name, fro, type,
							summary, ID, ss, expiry, "0") + "";
					Log.d(TAG, "Training added to list");
					if (readStatus.contentEquals("true")) {
						db.readrow(_id + "", AnnounceDBAdapter.SQLITE_NEWS);
					}
					db.close();
					Intent notificationIntent = new OpenContent(ApplicationLoader.getApplication().getApplicationContext(),
							AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ApplicationLoader.getApplication().getApplicationContext(), "News", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ApplicationLoader.getApplication().getApplicationContext(), "News", ID);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
    	}
    }
    
    private void parseJSONForTraining(JSONArray mJSONTrainingArr){
		for (int i = 0; i < mJSONTrainingArr.length(); i++) {
			try {
				String ID, title, fro, summary,type,link,detail,ss,expiry,name,category;
				String readStatus = "false";
				category = "training";
				JSONObject mJSONObject = mJSONTrainingArr.getJSONObject(i);
				ID = mJSONObject.getString("ID");
				title = mJSONObject.getString("title");
//				fro = mJSONObject.getString("fro");
				summary = mJSONObject.getString("summary");
				type = mJSONObject.getString("type");
				link = mJSONObject.getString("link");
				detail = Utilities.convertdate(mJSONObject.getString("detail"));
				ss = mJSONObject.getString("ss");
				expiry = Utilities.convertdate(mJSONObject.getString("expiry"));
				name = mJSONObject.getString("name");
				//SA VIKALP READ STATUS
    			try{
					readStatus = mJSONObject
							.getString("readStatus");
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
    			//EA VIKALP READ STATUS
				DownloadForTraining(ApplicationLoader.getApplication()
						.getApplicationContext(), link, type, name, title,
						detail, summary, ID, ss, expiry, link,readStatus);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
    }
    
    private void parseJSONForAwards(JSONArray mJSONAwardsArr){
		for (int i = 0; i < mJSONAwardsArr.length(); i++) {
			try {
				String ID,title,fro,summary,type,link,detail,ss,expiry,name,imgName,timeStamp,category;
				String readStatus = "false";
				category = "awards";
				JSONObject mJSONObject = mJSONAwardsArr.getJSONObject(i);
				ID = mJSONObject.getString("ID");
				title = mJSONObject.getString("title");
				summary = mJSONObject.getString("summary");
				type = mJSONObject.getString("type");
				link = mJSONObject.getString("link");
				detail = Utilities.convertdate(mJSONObject.getString("detail"));
				ss = mJSONObject.getString("ss");
				expiry = mJSONObject.getString("expiry");
				imgName = mJSONObject.getString("imgName");
				name = mJSONObject.getString("name");
				timeStamp = Utilities.convertdate(mJSONObject.getString("timeStamp"));
				//SA VIKALP READ STATUS
    			try{
					readStatus = mJSONObject
							.getString("readStatus");
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
    			//EA VIKALP READ STATUS
				DownloadAwards(ApplicationLoader.getApplication().getApplicationContext(), link, imgName,
						title, name, detail, timeStamp, ID,
						summary, ss, expiry, link,readStatus);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
    }
    
    private void parseJSONForFeedback(JSONArray mJSONFeedbackArr){
    	for(int i =0 ; i < mJSONFeedbackArr.length();i++){
    		try {
				JSONObject mJSONObject = mJSONFeedbackArr.getJSONObject(i);
				HashMap<String, String> postParam = new HashMap<String, String>();
				String feedbackID = mJSONObject.getString("ID");
				String xmlString ="";
				postParam.put("feedbackID", feedbackID);
				try {
					xmlString = readFromXml(postParam);
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ApplicationLoader.getApplication().getApplicationContext(), "Feedback", mJSONObject.getString("ID"));
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
				writeData(xmlString);

				Intent notificationIntent = new OpenContent(ApplicationLoader.getApplication().getApplicationContext(),
						AnnounceDBAdapter.SQLITE_FEEDBACK, feedbackID).itemView();
				// SA VIKALP RICH NOTIFICATION
				String nQuestions = "0";
				String detail = "9999-99-99";//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
				try {
					JSONArray mJSONArray = new JSONArray(xmlString);
					nQuestions = String.valueOf(mJSONArray.length());
					JSONObject jObject = mJSONArray.getJSONObject(0).getJSONObject("feedbackQuestion");//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					detail = Utilities.convertdate(jObject//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
							.getString("feedbackDate"));
				} catch (Exception e) {
				}
				if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					generateFeedbackNotification(ApplicationLoader.getApplication().getApplicationContext(), "Feedback", "", nQuestions,notificationIntent);
				} else {
					generateNotification(ApplicationLoader.getApplication().getApplicationContext(), "Feedback", "",notificationIntent);
				}
				} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
				// EA VIKALP RICH NOTIFICATION
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception ex){
				ex.printStackTrace();
			}
    	}
    }
    
	public void parseJSONForRemoteWipe(JSONObject mJSONObject) {
		try {
			if (mJSONObject.getString("remoteWipe").equalsIgnoreCase("true")) {
				Log.i(TAG, "remoteWipe - Received");
				Context mContext = ApplicationLoader.getApplication()
						.getApplicationContext();
				AnnounceDBAdapter adb = new AnnounceDBAdapter(mContext);
				adb.open();
				adb.remotewipe(mContext);
				adb.close();
				getSharedPreferences("MobCastPref", 0).edit().clear().commit();
				SessionManagement sm = new SessionManagement(mContext);
				try {
					sm.logoutUser(true);
					ApplicationLoader.getPreferences().setLoggedIn(false);
					ApplicationLoader.getPreferences().setMobileNumber("");
					ApplicationLoader.getPreferences().setRegId("");
					// SA ADDED VIKALP PULL SERVICE
					ApplicationLoader.getPreferences().setPullAlarmService(
							false);
					ApplicationLoader.getPreferences().setLastAnnouncementId(
							"0");
					ApplicationLoader.getPreferences().setLastNewsId("0");
					ApplicationLoader.getPreferences().setLastEventsId("0");
					ApplicationLoader.getPreferences().setLastFeedbackId("0");
					ApplicationLoader.getPreferences().setLastAwardsId("0");
					ApplicationLoader.getPreferences().setLastTrainingId("0");
					// EA ADDED VIKALP PULL SERVICE
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "remoteWipe - Successful");
			}

			if (mJSONObject.getString("remoteFolderDelete").equalsIgnoreCase(
					"true")) {
				Log.i(TAG, "remoteWipe - Received");
				try {
					File folder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER);
					deleteFolder(folder);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "remoteFolderDelete - Successful");
			}

			if (mJSONObject.getString("logOut").equalsIgnoreCase("true")) {
				try {
					SessionManagement logout = new SessionManagement(
							ApplicationLoader.getApplication()
									.getApplicationContext());
					logout.logoutUser(true);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "logOut - Successful");
			}

			if (mJSONObject.getString("forceExit").equalsIgnoreCase("true")) {
				try {
					SessionManagement logout = new SessionManagement(
							ApplicationLoader.getApplication()
									.getApplicationContext());
					logout.logoutUser(true);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "logOut - Successful");
			}

			if (mJSONObject.getString("newUpdate").equalsIgnoreCase("true")) {
				try {
					Intent notificationIntent = new Intent(ApplicationLoader
							.getApplication().getApplicationContext(),
							Home1.class);
					generateUpdateNotification(ApplicationLoader
							.getApplication().getApplicationContext(),
							notificationIntent);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "newUpdate - Successful");
			}

			if (mJSONObject.getString("askUserToLogin")
					.equalsIgnoreCase("true")) {
				try {
					Intent notificationIntent = new Intent(ApplicationLoader
							.getApplication().getApplicationContext(),
							MainActivity.class);
					generateLoginNotification(ApplicationLoader
							.getApplication().getApplicationContext(),
							notificationIntent);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
				Log.i(TAG, "newUpdate - Successful");
			}

		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
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
    
    
	public void DownloadForAnnounce(Context mContext, String type, String link,
			String name, String title, String detail, String fro,
			String summary, String id, String social, String contentExpiry,
			String fileLink, String readStatus) {
		Intent notificationIntent;
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

				AnnounceDBAdapter db = new AnnounceDBAdapter(mContext);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = String.valueOf( db.createAnnouncement(title, detail, fro, type,
						name, summary, id, social, contentExpiry, fileLink)
						+ "");
				Log.d(TAG, "Announcement added to list");
				//SA VIKALP READ STATUS
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "",
							AnnounceDBAdapter.SQLITE_ANNOUNCE);
				}
				//EA VIKALP READ STATUS
				db.close();
				
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastAnnounce", id).commit();
				ApplicationLoader.getPreferences().setLastAnnouncementId(id);

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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(mContext, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
							generateVideoNotification(mContext, "Announcements", title,
									file.getAbsolutePath(), notificationIntent);
						} else {
							generateNotification(mContext, "Announcements", title,notificationIntent);
						}	
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					// EA VIKALP RICH NOTIFICATION
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(mContext, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(mContext, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(mContext, "Announcements", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(mContext, "Announcement", id);
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

				AnnounceDBAdapter announce = new AnnounceDBAdapter(mContext);
				announce.open();
				// announce.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				String _id = String.valueOf(announce.createAnnouncement(title, detail, fro,
						type, name, summary, id, social, contentExpiry,
						fileLink)
						+ "");
				//SA VIKALP READ STATUS
				if (readStatus.contentEquals("true")) {
					announce.readrow(_id + "",
							AnnounceDBAdapter.SQLITE_ANNOUNCE);
				}
				//EA VIKALP READ STATUS
				announce.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastAnnounce", id).commit();
				ApplicationLoader.getPreferences().setLastAnnouncementId(id);
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(mContext, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(mContext, "Announcements", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(mContext, "Announcements", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					// EA VIKALP RICH NOTIFICATION
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(mContext, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();
					} else
						notificationIntent = new Intent(mContext, LoginV2.class);
					
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						generateNotification(mContext , "Announcements", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						// SA VIKALP UPDATE NOTIFICATION RECEIVED
						try {
							updateNotificationReceived(mContext, "Announcement", id);
						} catch (Exception ex) {
							Log.e("updateNotificationReceived", ex.toString());
						}
						// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			} else if (type.contentEquals("image")) {

				File myDir = new File(root + Constants.APP_FOLDER_IMG);
				myDir.mkdirs();
				String fname = name;
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

				AnnounceDBAdapter db = new AnnounceDBAdapter(mContext);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				// db.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				// db.createNews(link, root, fname, link, fname, url1, link);
				String _id = String.valueOf(db.createAnnouncement(title, detail, fro, type,
						name, summary, id, social, contentExpiry,
						fileLink)
						+ "");
				Log.d(TAG, "Announcement added to list");
				//SA VIKALP READ STATUS
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "",
							AnnounceDBAdapter.SQLITE_ANNOUNCE);
				}
				//EA VIKALP READ STATUS
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastAnnounce", id).commit();
				ApplicationLoader.getPreferences().setLastAnnouncementId(id);
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

					} else
						notificationIntent = new Intent(mContext,
								AnnounceListView.class);
					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateImageNotification(mContext, "Announcements", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(mContext, "Announcements", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					// EA VIKALP RICH NOTIFICATION
					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(mContext, "Announcement", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				} catch (Exception e) {
					try {
						file.delete();
					} catch (Exception ex) {

					}
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(mContext,
								AnnounceDBAdapter.SQLITE_ANNOUNCE, _id)
								.itemView();

					} else
						notificationIntent = new Intent(mContext,
								AnnounceListView.class);
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						generateNotification(mContext, "Announcements", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						// SA VIKALP UPDATE NOTIFICATION RECEIVED
						try {
							updateNotificationReceived(mContext, "Announcement", id);
						} catch (Exception ex) {
							Log.e("updateNotificationReceived", ex.toString());
						}
						// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}
	}
	
	public void DownloadForNews(Context ctx, String prlink, String link,
			String type, String name, String title, String detail,
			String summary, String id, String social, String fileLink,
			String contentExpiry, String readStatus) {
		 Intent notificationIntent;
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

				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createNews(title, detail, name, prlink, type,
						summary, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "News added to list");
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_NEWS);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastNews", id).commit();
				ApplicationLoader.getPreferences().setLastNewsId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateVideoNotification(ctx, "News", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(ctx, "News", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ctx, "News", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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

				AnnounceDBAdapter announce = new AnnounceDBAdapter(ctx);
				announce.open();
				// announce.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				String _id = announce.createNews(title, detail, name, prlink,
						type, summary, id, social, contentExpiry, fileLink)
						+ "";
				if (readStatus.contentEquals("true")) {
					announce.readrow(_id + "", AnnounceDBAdapter.SQLITE_NEWS);
				}
				announce.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastNews", id).commit();
				ApplicationLoader.getPreferences().setLastNewsId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(ctx, "News", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(ctx, "News", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();
					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ctx, "News", title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
				String fname = name;
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

				AnnounceDBAdapter db = new AnnounceDBAdapter(ctx);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				// db.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				// db.createNews(link, root, fname, link, fname, url1, link);
				String _id = db.createNews(title, detail, name, prlink,
						type, summary, id, social, contentExpiry, fileLink)
						+ "";
				Log.d(TAG, "News added to list");
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_NEWS);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastNews", id).commit();
				ApplicationLoader.getPreferences().setLastNewsId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					} else
						notificationIntent = new Intent(ctx, NewsList.class);
					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateImageNotification(ctx, "News", title,
								file.getAbsolutePath(), notificationIntent);
					} else {
						generateNotification(ctx, "News", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_NEWS, _id).itemView();

					} else
						notificationIntent = new Intent(ctx, NewsList.class);
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ctx, "News", title,notificationIntent);
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
		}
	}// End of DownloadFromNews
	
	
	public void DownloadForTraining(Context ctx, String link, String type, String name, String title, String detail, String summary,
			String id, String social, String contentExpiry, String fileLink, String readStatus) {
		Log.v("Training ", "in downloadforTraining");
		String ename = System.currentTimeMillis() + "";
		Intent notificationIntent;
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
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateVideoNotification(ctx, "New Training Notification", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification", title,notificationIntent);
					}
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {

						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ctx, "New Training Notification",
							title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath(),"",type,notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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

				// String link = root + "/.mobcast/mobcast_pdf/" + name;
				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath(),"",type,notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateAudioNotification(ctx, "New Training Notification", title,
								file.getAbsolutePath(),notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification", title,notificationIntent);
					}
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					generateNotification(ctx, "New Training Notification",
							title,notificationIntent);
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath(),"",type,notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}//ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					// SA VIKALP RICH NOTIFICATION
					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
					if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
						generateFileNotification(ctx,
								"New Training Notification", title,
								file.getAbsolutePath(),"",type,notificationIntent);
					} else {
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					}
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
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
					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new Intent(
								new OpenContent(ctx,
										AnnounceDBAdapter.SQLITE_TRAINING, _id)
										.itemView());

					} else
						notificationIntent = new Intent(ctx, LoginV2.class);

					if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP					
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);
					} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
						// SA VIKALP UPDATE NOTIFICATION RECEIVED
						try {
							updateNotificationReceived(ctx, "Training", id);
						} catch (Exception ex) {
							Log.e("updateNotificationReceived", ex.toString());
						}
						// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}

			}// end of doc
			
			if (type.contentEquals("image")) {
				File myDir = new File(root + Constants.APP_FOLDER_IMG);
				myDir.mkdirs();
				String fname = Utilities.getFileNameFromPath(url.toString());
				File file = new File(myDir, fname);
				if (file.exists())
					file.delete();
				long startTime = System.currentTimeMillis();
				Log.d("ImageManager", "download begining");
				url = new URL(url.toString().replaceAll(" ", "%20"));
				Log.d("ImageManager", "download url:" + url);
				Log.d("ImageManager", "downloaded file name:");

				AnnounceDBAdapter db = new AnnounceDBAdapter(this);
				// db.createEvent(Dtitle, Dwhen, Dwhen, Dwhen, Dwhen, summary);
				db.open();
				// db.createAnnouncement(title, detail, fro, type, name,
				// summary, id);
				// db.createNews(link, root, fname, link, fname, url1, link);
				String _id = db.createTraining(title, detail, name, summary,
						type, ename, id, social, contentExpiry, fileLink) + "";
				Log.d(TAG, "Training added to list");
				if (readStatus.contentEquals("true")) {
					db.readrow(_id + "", AnnounceDBAdapter.SQLITE_TRAINING);
				}
				db.close();
				SharedPreferences pref;
				pref = getSharedPreferences("MobCastPref", 0);
				pref.edit().putString("lastTraining", id).commit();
				ApplicationLoader.getPreferences().setLastTrainingId(id);//ADDED VIKALP PULL SERVICE
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

					// String link = root + "/.mobcast/mobcast_images/" + name;

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_TRAINING, _id)
								.itemView();

					} else
						notificationIntent = new Intent(ctx,
								TrainingListView.class);
					if (Utilities.isContentAfterInstallationDate(detail)) {
						// SA VIKALP RICH NOTIFICATION
						if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
							generateImageNotification(ctx,
									"New Training Notification", title,
									file.getAbsolutePath(),notificationIntent);
						} else {
							generateNotification(ctx, "Training", title,notificationIntent);
						}
						// EA VIKALP RICH NOTIFICATION	
					}

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

					if (ApplicationLoader.getPreferences().isLoggedIn()) {
						notificationIntent = new OpenContent(ctx,
								AnnounceDBAdapter.SQLITE_TRAINING, _id)
								.itemView();

					} else
						notificationIntent = new Intent(ctx,
								TrainingListView.class);
					if (Utilities.isContentAfterInstallationDate(detail)) {
						generateNotification(ctx, "New Training Notification",
								title,notificationIntent);	
					}
					

					// SA VIKALP UPDATE NOTIFICATION RECEIVED
					try {
						updateNotificationReceived(ctx, "Training", id);
					} catch (Exception ex) {
						Log.e("updateNotificationReceived", ex.toString());
					}
					// EA VIKALP UPDATE NOTIFICATION RECEIVED
				}
			}

		} catch (IOException e) {
			Log.d("ImageManager", "Error: " + e);
		}

	}// end of Download for training
	
	public void DownloadAwards(Context ctx, String link, String imgName,
			String title, String name, String detail, String rdate, String id,
			String summary, String social, String contentExpiry, String fileLink, String readStatus) {
		Intent notificationIntent;
		String imagePath;
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
			if (readStatus.contentEquals("true")) {
				db.readrow(_id + "",
						AnnounceDBAdapter.SQLITE_AWARD);
			}
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

				// Passing intent to Award with data
				if (ApplicationLoader.getPreferences().isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());
				} else
					notificationIntent = new Intent(ctx, LoginV2.class);

//				SA VIKALP RICH NOTIFICATION
				if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					generateAwardNotification(ctx, "Award", title,
							file.getAbsolutePath(),_id,notificationIntent);
				} else {
					generateNotification(ctx, "Award", title,notificationIntent);
				}
				} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
//				EA VIKALP RICH NOTIFICATION
				
				// SA VIKALP UPDATE NOTIFICATION RECEIVED
				try {
					updateNotificationReceived(ctx, "Award", id);
				} catch (Exception ex) {
					Log.e("updateNotificationReceived", ex.toString());
				}
				// EA VIKALP UPDATE NOTIFICATION RECEIVED
				
			}catch(Exception e){
				if (ApplicationLoader.getPreferences().isLoggedIn()) {
					notificationIntent = new Intent(new OpenContent(ctx,
							AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());
				} else
					notificationIntent = new Intent(ctx, LoginV2.class);
				
				if(Utilities.isContentAfterInstallationDate(detail)){ //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
				generateNotification(ctx, "Award", title, notificationIntent);
				} //ADDED VIKALP PULL SERVICE NOTIFICATION BULK STOP
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	public String readFromXml(HashMap<String, String> postParams) throws InterruptedException, ExecutionException {
		boolean isInternetPresent;
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();

		if (isInternetPresent) {
			FeedbackTask asyncHttpPost = new FeedbackTask(postParams);
			asyncHttpPost.execute(com.mobcast.util.Constants.CHECK_FEEDBACK);
			return asyncHttpPost.get();
		}
		return "";
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
				// String feedbackDate = jObject.getString("feedbackDate");
				// //ADDED VIKALP FEEDBACK DATE SORT ORDER
				String feedbackDate = Utilities.convertdate(jObject
						.getString("feedbackDate"));
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
				String contentExpiry = Utilities.convertdate(jObject
						.getString("contentExpiry"));
//				SA ADDED VIKALP QUIZ POINTS
				String correctAnswer = null;
				String points = null;
				try{
					correctAnswer = jObject.getString("correctAnswer");
					points = jObject.getString("points");
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}
//				EA ADDED VIKALP QUIZ POINTS


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
	
	public class FeedbackTask extends AsyncTask<String, Void, String> {

		private HashMap<String, String> mData;
		// private HashMap<String, String> mData = null;
		String str = "";
		
		public FeedbackTask(HashMap<String, String> mData){
			this.mData = mData;
		}

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
	
	@SuppressLint("NewApi") private void generateNotification(Context ctx, String message, String title,Intent notificationIntent) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {
			try{
				// RemoteViews remoteViews = new RemoteViews(getPackageName(),
				// R.layout.notification_event);

				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
						.setContentTitle("New Mobcast " + message)
						.setContentText(title);
				// .setContent(remoteViews);
				mBuilder.setDefaults(-1);
				mBuilder.setOnlyAlertOnce(true);
				// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;
				PendingIntent resultPendingIntent;
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
					TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
			        taskStackBuilder.addNextIntent(notificationIntent);
			        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
			                PendingIntent.FLAG_UPDATE_CURRENT);
				}else{
					resultPendingIntent = PendingIntent.getActivity(ctx,
							Constants.Notification_Id, notificationIntent,
							//
							// PendingIntent.FLAG_ONE_SHOT);
							PendingIntent.FLAG_UPDATE_CURRENT);				
				}
				
				mBuilder.setContentIntent(resultPendingIntent);
				mBuilder.setAutoCancel(true);
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
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}
		}

	}

	// SA VIKALP RICH NOTIFICATION

	@SuppressLint("NewApi") private void generateEventNotification(Context ctx, String message,
			String title, String dbID, String id,Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {

			// RemoteViews remoteViews = new RemoteViews(getPackageName(),
			// R.layout.notification_event);
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
					.setDefaults(-1).
					setOnlyAlertOnce(true);
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);				
			}
					mBuilder.setContentIntent(resultPendingIntent);
					mBuilder.setAutoCancel(true);
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

	@SuppressLint("NewApi") private void generateFeedbackNotification(Context ctx, String message,
			String title, String question, Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {

			// RemoteViews remoteViews = new RemoteViews(getPackageName(),
			// R.layout.notification_event);
			
			String nQuestions = "No of questions : " + question;
			
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);			
				}
			NotificationCompat.Builder mBuilder;
			mBuilder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
					.setContentTitle(message)
					.setContentText(title)
					.addAction(R.drawable.feedback, nQuestions,
							resultPendingIntent)
					.setDefaults(-1)
					.setOnlyAlertOnce(true)
					.setContentIntent(resultPendingIntent);

			mBuilder.setAutoCancel(true);
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
	
	@SuppressLint("NewApi") private void generateAudioNotification(Context ctx, String message,
			String title, String filePath, Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {

			// RemoteViews remoteViews = new RemoteViews(getPackageName(),
			// R.layout.notification_event);
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);			
				}			NotificationCompat.Builder mBuilder;
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
					.setOnlyAlertOnce(true)
					.setContentIntent(resultPendingIntent);

			mBuilder.setAutoCancel(true);
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

	@SuppressLint("NewApi") private void generateVideoNotification(Context ctx, String message,
			String title, String filePath, Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {

			// RemoteViews remoteViews = new RemoteViews(getPackageName(),
			// R.layout.notification_event);
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);			
				}
			
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
					.setOnlyAlertOnce(true)
					.setContentIntent(resultPendingIntent);

			mBuilder.setAutoCancel(true);
			Notification notification = new NotificationCompat.BigPictureStyle(
					mBuilder).bigPicture(
					Utilities.getVideoLayerThumbnail(filePath)).build();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			SessionManagement sm = new SessionManagement(ctx);
			if (sm.isLoggedIn()) {
				mNotificationManager.cancel(434);
				mNotificationManager.notify(434, notification);
			} else {
				Log.e("logged out", "notificaiton received");
			}

		}
	}
	
	@SuppressLint("NewApi") private void generateAwardNotification(Context ctx, String message,
			String title, String filePath, String id,Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {

			// RemoteViews remoteViews = new RemoteViews(getPackageName(),
			// R.layout.notification_event);
			
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);			
				}
			
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
					.setOnlyAlertOnce(true)
					.setContentIntent(resultPendingIntent);
			mBuilder.setAutoCancel(true);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			
			Notification notification = new NotificationCompat.BigPictureStyle(
					mBuilder).bigPicture(
					BitmapFactory.decodeFile(filePath, options)).build();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			SessionManagement sm = new SessionManagement(ctx);
			if (sm.isLoggedIn()) {
				mNotificationManager.cancel(434);
				mNotificationManager.notify(434, notification);
			} else {
				Log.e("logged out", "notificaiton received");
			}

		}
	}

	@SuppressLint("NewApi") private void generateFileNotification(Context ctx, String message,
			String title, String filePath, String fileMetaData, String type,Intent notificationIntent) {
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
			
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);			
				}
			
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
					.setOnlyAlertOnce(true)
					.setContentIntent(resultPendingIntent);
			
			mBuilder.setAutoCancel(true);
			// Notification notification = new Notification.InboxStyle(mBuilder)
			// .setSummaryText("vikalppatelce@yahoo.com")
			// .build();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			SessionManagement sm = new SessionManagement(ctx);
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
			String title, String mImagePath, Intent notificationIntent) {
		// if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {
		if (ApplicationLoader.getPreferences().isLoggedIn()) {
			
			PendingIntent resultPendingIntent;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
				TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(ctx);
		        taskStackBuilder.addNextIntent(notificationIntent);
		        resultPendingIntent = taskStackBuilder.getPendingIntent(Constants.Notification_Id,
		                PendingIntent.FLAG_UPDATE_CURRENT);
			}else{
				resultPendingIntent = PendingIntent.getActivity(ctx,
						Constants.Notification_Id, notificationIntent,
						//
						// PendingIntent.FLAG_ONE_SHOT);
						PendingIntent.FLAG_UPDATE_CURRENT);
			}
			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_stat_sanofi_notification)
					.setContentTitle(message)
					.setContentText(title)
					.addAction(android.R.drawable.ic_menu_gallery, title,
							resultPendingIntent);

			mBuilder.setDefaults(-1);
			mBuilder.setOnlyAlertOnce(true);
			
			// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;
			
			mBuilder.setContentIntent(resultPendingIntent);
			mBuilder.setAutoCancel(true);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;

			Notification notification = new NotificationCompat.BigPictureStyle(
					mBuilder).bigPicture(
					BitmapFactory.decodeFile(mImagePath, options)).build();

			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			SessionManagement sm = new SessionManagement(ctx);
			if (sm.isLoggedIn()) {
				mNotificationManager.cancel(434);
				mNotificationManager.notify(434, notification);
			} else {
				Log.e("logged out", "notificaiton received");
			}
		}
	}

	// EA VIKALP RICH NOTIFICATION

	private void generateLoginNotification(Context ctx, Intent notificationIntent) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
				.setContentTitle("Your not loggedin to ShoppersStop")
				.setContentText("Please login to ShoppersStop");

		mBuilder.setDefaults(-1);
		mBuilder.setOnlyAlertOnce(true);
		mBuilder.setAutoCancel(true);
		// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;
		PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
				Constants.Notification_Id, notificationIntent,
				//
				// PendingIntent.FLAG_ONE_SHOT);
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}

	private void generateUpdateNotification(Context ctx, Intent notificationIntent) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_stat_sanofi_notification)
				.setContentTitle("A new update is available.")
				.setContentText("Please update the application.");

		mBuilder.setDefaults(-1);
		mBuilder.setOnlyAlertOnce(true);
		mBuilder.setAutoCancel(true);
		// mBuilder.getNotification().flags|= Notification.FLAG_AUTO_CANCEL;
		PendingIntent resultPendingIntent = PendingIntent.getActivity(ctx,
				Constants.Notification_Id, notificationIntent,
				//
				// PendingIntent.FLAG_ONE_SHOT);
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.cancel(434);
		mNotificationManager.notify(434, mBuilder.build());
	}
	
	public void updateNotificationReceived(Context mContext,
			String mModuleName, String id) {
		Reports reportsObj = new Reports(mContext, mModuleName);
		reportsObj.updateNotification(id);
	}
}
