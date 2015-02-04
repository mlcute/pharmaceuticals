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
import com.sanofi.in.mobcast.GcmBroadcastReceiver.FeedbackTask;

import android.app.ActivityManager;
import android.app.IntentService;
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
import android.util.Log;

public class GcmService extends IntentService {
	
	static final int uniqueID = 123456;
	private final String TAG = "Inside notifs service";
	public GcmService() {
		super("Notification Service");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		if(intent!=null){
			int unread = intent.getIntExtra("unread", 0);
			if(unread==0 || unread==1){
				Log.d(TAG, "No new unread messages");
			}
			else
			{
				generateNotification(getApplicationContext(), "Mobcast", unread);
				
			}
		
		}
	}
	private void generateNotification(Context ctx, String message,
			int unread) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		Intent notificationIntent = new Intent(ctx,Home1.class);
		if (prefs.getBoolean(ctx.getString(R.string.isloggedin), false)) {

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					ctx).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle(message)
					.setContentText(String.valueOf(unread)+" unread messages");

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
				mNotificationManager.cancel(435);
				mNotificationManager.notify(435, mBuilder.build());
			} else {
				Log.e(TAG, "Notification received!!");
			}

		}

	}
	
	
}
