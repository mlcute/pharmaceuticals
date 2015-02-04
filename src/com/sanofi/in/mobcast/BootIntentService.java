package com.sanofi.in.mobcast;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootIntentService extends IntentService {

	private static final long OFFSET = 86400000;

	public BootIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		SharedPreferences pref;
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String email = pref.getString("name", null);
		String mobileNumber = pref.getString("mobileNumber", "0");
		String passKey = pref.getString("passKey", "0");

//		String regID = GCMRegistrar.getRegistrationId(getApplicationContext());
//
//		if (regID.equals("")) {
//
//			Log.d("Boot Registration", "Registering...");
//
//			// tvRegStatusResult.setText(registrationStatus);
//
//			// register this device for this project
//			GCMRegistrar.register(getApplicationContext(),
//					com.mobcast.util.Constants.PROJECT_ID);
//			int counter = 0;
//			while (true) {
//				regID = GCMRegistrar.getRegistrationId(getApplicationContext());
//				counter++;
//				if (counter == 40 || regID != null || !regID.equals("")) {
//					break;
//				}
//			}
//
//		}

		// String regID = pref.getString("gcmregID", null);
		long currentTime = System.currentTimeMillis();

		Log.d("Current Time", String.valueOf(currentTime));

		long previousTime = pref.getLong("Timestamp", 0);

		Log.d("Previous Time", String.valueOf(previousTime));

		if (previousTime != 0) {
			if ((currentTime - previousTime) > OFFSET) {

				pref.edit().putLong("Timestamp", currentTime);
				pref.edit().commit();
				
				// pref.edit().putString("gcmregID", regID).commit();

				/*
				 * if (email != null && regID != null && regID!="1") {
				 * HashMap<String, String> params = new HashMap<String,
				 * String>(); params.put("update", "yes"); params.put("id",
				 * regID); params.put("email", email); params.put("companyID",
				 * getString(R.string.companyID));
				 * 
				 * AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
				 * asyncHttpPost.execute(com.mobcast.util.Constants.M_UPDATE);
				 */
				/*try {
					if (!mobileNumber.equals("0") && !passKey.equals("0")
							&& !mobileNumber.equals("") && !passKey.equals("") && !regID.equals("")) {
						HashMap<String, String> params = new HashMap<String, String>();

						params.put("regID", regID);
						params.put("device", "android");
						params.put("deviceType", "android");
						params.put("mobileNumber", mobileNumber);
						params.put("passKey", passKey);
						CheckLoginTask checkLoginTask = new CheckLoginTask(
								params, getApplicationContext());
						checkLoginTask
								.execute(com.mobcast.util.Constants.CHECK_LOGIN);
						Log.d("Boot success", "Registration on boot successful");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}*/
			}
		}
	}

	class CheckLoginTask extends AsyncTask<String, String, String> {

		private HashMap<String, String> mData = null;// post data
		Context context = null;
		String str = "";

		public CheckLoginTask(HashMap<String, String> data) {
			mData = data;
		}

		public CheckLoginTask(HashMap<String, String> data, Context context) {

			this.context = context;

			this.mData = data;
		}

		/**
		 * background
		 */

		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
													// is
													// URL
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
				Log.v("AsyncPost", "Posting '" + body + "' to " + params[0]);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			return str;
		}

	}
}
