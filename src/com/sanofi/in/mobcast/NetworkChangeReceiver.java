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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.sanofi.in.mobcast.LoginV2.CheckLoginTask;

public class NetworkChangeReceiver extends BroadcastReceiver {
	String regID = "", registrationStatus = "";
	HashMap<String, String> params = new HashMap<String, String>();
	private static final String PROJECT_ID = com.mobcast.util.Constants.PROJECT_ID;// 195344484693

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction()==ConnectivityManager.CONNECTIVITY_ACTION){
			
			
			if(isOnline(context)){
				SharedPreferences pref;
				
				pref = context.getSharedPreferences("MobCastPref", 0);
				
				String mobileNumber = pref.getString("mobileNumber", "0");
				String passKey = pref.getString("passKey", "0");
				
				regID = GCMRegistrar.getRegistrationId(context);

				if (regID.equals("")) {

					registrationStatus = "Registering...";

					// tvRegStatusResult.setText(registrationStatus);

					// register this device for this project
					GCMRegistrar.register(context, PROJECT_ID);
					int counter = 0;
					while (true) {
						regID = GCMRegistrar.getRegistrationId(context);
						counter++;
						if (counter == 40 || regID != null || !regID.equals("")) {
							break;
						}
					}
				
				}
				try{
					if(!mobileNumber.equals("0") && !passKey.equals("0") && !mobileNumber.equals("") && !passKey.equals("")){
					params.put("regID", regID);
					params.put("device", "android");
					params.put("deviceType", "android");
					params.put("mobileNumber", mobileNumber);
					params.put("passKey", passKey);
					CheckLoginTask checkLoginTask = new CheckLoginTask(params,context);
					checkLoginTask
							.execute(com.mobcast.util.Constants.CHECK_LOGIN);
					}
			}catch(Exception e){
				e.printStackTrace();
			}
			}
	}
	}

	public boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		// should check null because in air plan mode it will be null
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
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


