package com.sanofi.in.mobcast;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;

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
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class LoginV2 extends Activity {

	Button submit;
	EditText username, countrycode, password;
	IntentFilter gcmFilter;
	ProgressDialog pDialog;
	Context context;
	String emailAddress, passKey, name;
	HashMap<String, String> params = new HashMap<String, String>();
	int flag = 0;
	private static final String PROJECT_ID = com.mobcast.util.Constants.PROJECT_ID;// 195344484693

	// This tag is used in Log.x() calls
	private static final String TAG = "LoginV2Activity";

	// server
	SessionManagement session;
	private static final Random random = new Random();

	// This string will hold the lengthy registration id that comes
	// from GCMRegistrar.register()
	private String regID = "", objectID = "";
	String randomString;
	// These strings are hopefully self-explanatory
	private String registrationStatus = "Not yet registered";
	private TextView mForgetPassword;

	String generateRandom() {
		String ALPHA_NUM = "abcd123";
		int len = 7;
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int ndx = (int) (Math.random() * ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		System.out.println("Login -->" + sb.toString());
		return sb.toString();
	}

	String getregID() {
		// gcm stuff
		// GCMRegistrar.checkDevice(this);
		// GCMRegistrar.checkManifest(this);
		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);

			// Check the manifest to be sure this app has all the required
			// permissions.
			GCMRegistrar.checkManifest(this);

			// Get the existing registration id, if it exists.
			regID = GCMRegistrar.getRegistrationId(this);

			Log.i(TAG, "regID : " + regID);

			if (regID.equals("")) {

				registrationStatus = "Registering...";

				// tvRegStatusResult.setText(registrationStatus);

				// register this device for this project
				GCMRegistrar.register(this, PROJECT_ID);
				int counter = 0;
				while (true) {
					regID = GCMRegistrar.getRegistrationId(this);
					counter++;
					if (counter == 1000 || regID != null || !regID.equals("")) {
						break;
					}
				}
				Log.v("register", regID);

				registrationStatus = "Registration Acquired";

				if (!TextUtils.isEmpty(regID)) {
					ApplicationLoader.getPreferences().setRegId(regID);
				}

				// This is actually a dummy function. At this point, one
				// would send the registration id, and other identifying
				// information to your server, which should save the id
				// for use when broadcasting messages.

			} else {

				registrationStatus = "Already registered" + regID;

			}

		} catch (Exception e) {

			e.printStackTrace();
			registrationStatus = e.getMessage();

		}
		// Gcm stuff ends

		// storeRegistrationId(context,regID);
		return regID;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		getregID();

		// PushService.setDefaultPushCallback(getApplicationContext(),
		// MainActivity.class);
		// ParseInstallation.getCurrentInstallation().saveInBackground(new
		// SaveCallback(){
		//
		// @Override
		// public void done(ParseException arg0) {
		// // TODO Auto-generated method stub
		// regID = (String)
		// ParseInstallation.getCurrentInstallation().get("deviceToken");
		// PushService.setDefaultPushCallback(getApplicationContext(),
		// MainActivity.class);
		// }});
		// String object_ID = "";
		// if(ParseInstallation.getCurrentInstallation()!=null){
		// object_ID = ParseInstallation.getCurrentInstallation().getObjectId();
		// if(object_ID == null || object_ID==""){
		// try {
		// ParseInstallation.getCurrentInstallation().save();
		//
		// object_ID = ParseInstallation.getCurrentInstallation().getObjectId();
		//
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		//
		// }
		//
		//
		// }
		//
		// PushService.subscribe(getApplicationContext(), "a"+object_ID,
		// MainActivity.class);
		//
		// SharedPreferences prefs1 =
		// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// prefs1.edit().putString("objectID", objectID).commit();
		//
		// //regID=
		// (String)ParseInstallation.getCurrentInstallation().get("deviceToken");
		//
		//
		// objectID = object_ID;
		// Log.d("Object id hhahdsadsadsadasdasd", objectID);
		//
		// Log.d("Reg id hhahdsadsadsadasdasd", "is: "+regID);
		// /*String deviceToken =
		// (String)ParseInstallation.getCurrentInstallation().get("deviceToken");
		//
		// Log.d("DeviceToken or old Registration ID", deviceToken);*/
		//
		// /*regID="";
		// //regID =
		// (String)ParseInstallation.getCurrentInstallation().get("deviceToken");
		// if (regID.equals("") || regID==null) {
		//
		// registrationStatus = "Registering...";
		//
		// // tvRegStatusResult.setText(registrationStatus);
		//
		// // register this device for this project
		// //GCMRegistrar.register(this, PROJECT_ID);
		// int counter = 0;
		// while (true) {
		// try {
		// ParseInstallation.getCurrentInstallation().save();
		// regID = GCMRegistrar.getRegistrationId(getApplicationContext());
		// counter++;
		//
		// Log.d("COunter", "is: "+String.valueOf(counter)+"--"+regID);
		//
		// if (counter == 1000 || regID != null || !regID.equals("")) {
		// break;
		// }
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		//
		// }*/
		//
		// if(regID!=null && !regID.equals(""))
		// {
		// SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// prefs.edit().putString("regID", regID).commit();
		//
		// }
		//
		// /*String deviceToken =
		// (String)ParseInstallation.getCurrentInstallation().get("deviceToken");*/
		// Log.d("Device Token", "is: "+regID);
		//
		// Log.d("Registration id yeh asdadsadasdsad", "haha=" + regID);
		//

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		SessionManagement logout = new SessionManagement(
				getApplicationContext());
		if (logout.isLoggedIn())
			logout.logoutUser(true);
		randomString = generateRandom();
		submit = (Button) findViewById(R.id.bLogin);

		username = (EditText) findViewById(R.id.etUsername);
		password = (EditText) findViewById(R.id.etPassword);
		mForgetPassword = (TextView) findViewById(R.id.forgetPassword);
		session = new SessionManagement(getApplicationContext());
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");

		// SA VIKALP
		// username.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// username.setHint("");
		// }
		// });
		//
		// username.setOnFocusChangeListener(new OnFocusChangeListener() {
		//
		// public void onFocusChange(View v, boolean hasFocus) {
		// if (!hasFocus) {
		// // code to execute when EditText loses focus
		// username.setHint("Enter Mobile Number");
		// }
		// }
		// });
		// EA VIKALP

		submit.setOnTouchListener(myhandler2);

		submit.setOnClickListener(myhandler1);

		submit.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				ConnectionDetector cd = new ConnectionDetector(LoginV2.this);
				if (!cd.isConnectingToInternet()) {
					Toast.makeText(LoginV2.this, "No internet present",
							Toast.LENGTH_SHORT).show();
				} else
					verifywithoutsms();
				return false;
			}
		});

		mForgetPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(LoginV2.this, ForgetPassword.class);
				mIntent.putExtra("emailAddress", username.getText().toString()
						.trim());
				startActivity(mIntent);
			}
		});
	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.gradient2);
				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.button_gradient);

				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);
			}

			return false;
		}

	};

	View.OnClickListener myhandler1 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(), regID,
			// Toast.LENGTH_SHORT).show();
			ConnectionDetector cd = new ConnectionDetector(LoginV2.this);

			if (TextUtils.isEmpty(username.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(),
						"Please Enter Email Id", Toast.LENGTH_SHORT).show();
			}

			else if (TextUtils.isEmpty(password.getText().toString().trim())) {
				Toast.makeText(getApplicationContext(),
						"Please Enter password", Toast.LENGTH_SHORT).show();
			}

			else if (!cd.isConnectingToInternet()) {
				Toast.makeText(LoginV2.this, "No internet present",
						Toast.LENGTH_SHORT).show();
			}

			else {
				// Toast.makeText(getApplicationContext(), regID,
				// Toast.LENGTH_SHORT).show();
				emailAddress = username.getText().toString().trim();
				passKey = randomString;

				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				// pref = getSharedPreferences("MobCastPref", 0);

				Editor editor = pref.edit();
				editor.putString("mobileNumber", emailAddress);
				// pref.edit().putString("passKey", passKey);
				editor.putString("passKey", passKey);
				editor.commit();
				// ApplicationLoader.getPreferences().setMobileNumber(mobileNumber);
				ApplicationLoader.getPreferences()
						.setEmailAddress(emailAddress);
				if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
						.getRegId())) {
					params.put("regID", ApplicationLoader.getPreferences()
							.getRegId());
				} else {
					getregID();
					params.put("regID", regID);
				}
				params.put("objectID", objectID);
				params.put("device", "android");
				params.put("deviceType", "android");
				params.put("mobileNumber", username.getText().toString().trim());
				params.put("passKey", randomString);
				params.put(Constants.user_id, username.getText().toString()
						.trim());
				params.put("password", password.getText().toString().trim());

				// post request to server

				Log.d("device", params.get("device").toString());

				Boolean isInternetPresent = cd.isConnectingToInternet(); //
				if (isInternetPresent
						&& params.get("device").equalsIgnoreCase("android")) {
					CheckLoginTask checkLoginTask = new CheckLoginTask(params,
							LoginV2.this);
					checkLoginTask
							.execute(com.mobcast.util.Constants.CHECK_LOGIN);
				}

				else
					Toast.makeText(getApplicationContext(),
							"Check your Internet Status ", Toast.LENGTH_LONG)
							.show();
			}

		}
	};

	class CheckLoginTask extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;
		String str = "";

		/**
		 * constructor
		 */

		public CheckLoginTask(HashMap<String, String> data) {
			mData = data;
		}

		public CheckLoginTask(HashMap<String, String> data, Activity activity) {

			this.activity = activity;
			context = activity;
			pDialog = new ProgressDialog(context);
			mData = data;
		}

		/**
		 * background
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (context != null) {
				pDialog = new ProgressDialog(context);
				pDialog.setMessage(Html.fromHtml("Please Wait..."));
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
													// is
													// URL
			try {

				if (TextUtils.isEmpty(regID)) {
					for (int i = 0; i < 3; i++) {
						getregID();
						Log.i(TAG, "getregID()");
					}
				}

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

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				pDialog.dismiss();
				pDialog = null;
			} catch (Exception e) {
				// nothing
			}

			Log.d("checkLogin result", str);

			JSONObject jObject;
			try {
				jObject = new JSONObject(str);

				String exists = jObject.getString("exists");
				// if(exists.contains("true")){verifywithoutsms();}
				if (exists.contains("true")) {
					ApplicationLoader.getPreferences().setRegId(regID);
					String matches = jObject.getString("matches");
					String department;
					if (matches.contentEquals("true")) {
						emailAddress = jObject.getString("emailAddress");
						name = jObject.getString("name");
						ApplicationLoader.getPreferences().setEmailAddress(
								emailAddress);
						ApplicationLoader.getPreferences().setName(name);
						
						try {
							department = jObject.getString("department");
//							ApplicationLoader.getPreferences().setDesignation(jObject.getString("designation"));
							if (department.contentEquals("sales")) {
								ApplicationLoader.getPreferences()
										.setIncenModuleEnable(true);
								ApplicationLoader.getPreferences()
										.setIncenModuleLayoutEnable(true);
								ApplicationLoader.getPreferences()
								.setPerformanceModuleEnable(true);
						ApplicationLoader.getPreferences()
								.setPerformanceModuleLayoutEnable(true);
							} else {
								ApplicationLoader.getPreferences()
										.setIncenModuleEnable(false);
								ApplicationLoader.getPreferences()
										.setIncenModuleLayoutEnable(false);
								ApplicationLoader.getPreferences()
								.setPerformanceModuleEnable(false);
								ApplicationLoader.getPreferences()
								.setPerformanceModuleLayoutEnable(false);
							}
						} catch (Exception e) {
							Log.i(TAG, e.toString());
						}
						
						verifywithoutsms();
						Log.d("email", emailAddress);
						Log.d("name", name);
					}
				} else {
					Toast.makeText(LoginV2.this, "User Not Registered",
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	void verify() {
		// sendSms(); // VIKALP
		ConfirmLoginTask confirmLoginTask = new ConfirmLoginTask(params,
				LoginV2.this);
		confirmLoginTask.execute(com.mobcast.util.Constants.CONFIRM_LOGIN);
	}

	void verifywithoutsms() {

		session.createLoginSession(username.getText().toString().trim());
		SessionManagement sm = new SessionManagement(LoginV2.this);

		SharedPreferences pref;
		pref = getSharedPreferences("MobCastPref", 0);
		String temp1 = pref.getString("name", "akshay@bombil.com");

		SharedPreferences pref1 = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		String regID = pref1.getString("regID", "0");

		if (!TextUtils.isEmpty(ApplicationLoader.getPreferences().getRegId())) {
			HashMap<String, String> params11 = new HashMap<String, String>();
			params11.put("update", "yes");
			params11.put("id", regID);
			params11.put("mobileNumber", temp1);
			params11.put(Constants.user_id, temp1);
			params11.put("deviceType", "android");
			params11.put("device", "android");
			params11.put("companyID", getString(R.string.companyID));

			AsyncHttpPost asyncHttpPost11 = new AsyncHttpPost(params11);
			asyncHttpPost11.execute(com.mobcast.util.Constants.M_UPDATE);
			// sm.checkLogin();
			
			//SA VIKALP ADDED PULL SERVICE
			if (!ApplicationLoader.getPreferences().isPullAlarmService()) {
				getLastIdFromPreferences();
				ApplicationLoader.setAlarm();
				if(TextUtils.isEmpty(ApplicationLoader.getPreferences().getInstallationDate())){
					ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());
				}
			}
			//EA VIKALP ADDED PULL SERVICE
			ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());//ADDED VIKALP PULL SERVICE NOTFICATION BULK STOP
			
			if (sm.checkSession()) {
				startActivity(new Intent(LoginV2.this,
						PreDashboardActivity.class));
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Please Try Again",
						Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	}

	class ConfirmLoginTask extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;
		String str = "";

		/**
		 * constructor
		 */

		public ConfirmLoginTask(HashMap<String, String> data) {
			mData = data;
		}

		public ConfirmLoginTask(HashMap<String, String> data, Activity activity) {

			this.activity = activity;
			context = activity;
			pDialog = new ProgressDialog(context);
			mData = data;
		}

		/**
		 * background
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (context != null) {
				pDialog = new ProgressDialog(context);
				pDialog.setMessage(Html.fromHtml("Please Wait..."));
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				pDialog.dismiss();
				pDialog = null;
			} catch (Exception e) {
				// nothing
			}

			Log.d("confirm result", str);

			JSONObject jObject;
			try {
				jObject = new JSONObject(str);

				String matches = jObject.getString("matches");

				String remotewipe = "";
				try {
					remotewipe = jObject.getString("remoteWipeEnabled");
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (remotewipe.contains("true")) {
					AnnounceDBAdapter adb = new AnnounceDBAdapter(LoginV2.this);
					adb.open();
					adb.remotewipe(LoginV2.this);
					adb.close();
				}
				

				if (matches.contains("true") || !BuildVars.debug) {
					ApplicationLoader.getPreferences().setLoggedIn(true);
					session.createLoginSession(username.getText().toString()
							.trim());
					// SessionManagement sm = new
					// SessionManagement(LoginV2.this);

					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					String temp1 = pref.getString("name", "akshay@bombil.com");

					SharedPreferences pref1 = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					String regID = pref1.getString("regID", "0");

					HashMap<String, String> params11 = new HashMap<String, String>();
					params11.put("update", "yes");
					params11.put("id", regID);
					params11.put("mobileNumber", temp1);
					params11.put(Constants.user_id, temp1);
					params11.put("companyID", getString(R.string.companyID));

					AsyncHttpPost asyncHttpPost11 = new AsyncHttpPost(params11);
					asyncHttpPost11
							.execute(com.mobcast.util.Constants.M_UPDATE);
					// sm.checkLogin();
					// if (sm.checkSession()) {
					if (ApplicationLoader.getPreferences().checkSession()) {
						// SA VIKALP
						try {
							ApplicationLoader.getPreferences().setEmailAddress(
									jObject.getString("emailAddress"));
							ApplicationLoader.getPreferences().setName(
									jObject.getString("name"));
						} catch (Exception e) {
							Log.i(TAG, e.toString());
						}
						// EA VIKALP
						startActivity(new Intent(LoginV2.this,
								PreDashboardActivity.class));
						finish();
					} else {
						Toast.makeText(getApplicationContext(),
								"Please Try Again", Toast.LENGTH_SHORT).show();
					}
					finish();
				} else {
					flag++;
					if (flag < 5) {
						ConfirmLoginTask confirmLoginTask = new ConfirmLoginTask(
								params, LoginV2.this);
						confirmLoginTask
								.execute(com.mobcast.util.Constants.CONFIRM_LOGIN);
					} else
						Toast.makeText(LoginV2.this, "Login Failed",
								Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	//SA VIKALP PULL SERVICE
	private void getLastIdFromPreferences(){
		try{
			ApplicationLoader.getPreferences().setLoggedIn(true);
			if (!ApplicationLoader.getPreferences().isPullAlarmService()) {
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			ApplicationLoader.getPreferences().setLastAnnouncementId(pref.getString("lastAnnounce", "0"));
			ApplicationLoader.getPreferences().setLastEventsId(pref.getString("lastEvent", "0"));
			ApplicationLoader.getPreferences().setLastNewsId(pref.getString("lastNews", "0"));
			ApplicationLoader.getPreferences().setLastTrainingId(pref.getString("lastTraining", "0"));
			ApplicationLoader.getPreferences().setLastAwardsId(pref.getString("lastAward", "0"));
			ApplicationLoader.getPreferences().setLastFeedbackId(pref.getString("lastFeedback", "0"));
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	//EA VIKALP PULL SERVICE
	
	void sendSms() {
		String phnumber = "9223051616";
		// String phnumber="9798768922";
		String msg = "mobcast sanofi " + randomString;
		try {
			String SENT = "Sent";
			String DELIVERED = "Delivered";
			Intent sentIntent = new Intent(SENT);
			PendingIntent sentPI = PendingIntent.getBroadcast(
					getApplicationContext(), 0, sentIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			Intent deliveryIntent = new Intent(DELIVERED);
			PendingIntent deliverPI = PendingIntent.getBroadcast(
					getApplicationContext(), 0, deliveryIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			registerReceiver(new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String result = "";
					switch (getResultCode()) {
					case Activity.RESULT_OK:
						result = "Transmission successful";
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						result = "Transmission failed";
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						result = "Radio off";
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						result = "No PDU defined";
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						result = "No service";
						break;
					}
					// Toast.makeText(getApplicationContext(), result,
					// Toast.LENGTH_LONG).show();
					unregisterReceiver(this);
					Log.d("sms", result);
				}
			}, new IntentFilter(SENT));

			registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					// Toast.makeText(getApplicationContext(),
					// "Delivered",Toast.LENGTH_LONG).show();
					Log.d("sms", "Delivered");
					unregisterReceiver(this);
				}
			}, new IntentFilter(DELIVERED));

			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phnumber, null, msg, sentPI, deliverPI);

		} catch (Exception ex) {
			// Toast.makeText(getApplicationContext(),
			// ex.getMessage().toString(), Toast.LENGTH_LONG) .show();
			ex.printStackTrace();
		}

	}

	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}