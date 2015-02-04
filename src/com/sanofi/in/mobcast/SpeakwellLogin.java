package com.sanofi.in.mobcast;



import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
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
import org.json.JSONObject;

import com.google.android.gcm.GCMRegistrar;
import com.sanofi.in.mobcast.R;


import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SpeakwellLogin extends Activity {
	
	
	Button submit;
	EditText username, countrycode;
	IntentFilter gcmFilter;
	ProgressDialog pDialog;
	HashMap<String, String> params = new HashMap<String, String>();

	private static final String PROJECT_ID = com.mobcast.util.Constants.PROJECT_ID;// 16011280382

	// This tag is used in Log.x() calls
	private static final String TAG = "MainActivity";

	// server
	SessionManagement session;
	private static final Random random = new Random();

	// This string will hold the lengthy registration id that comes
	// from GCMRegistrar.register()
	private String regID = "";
String randomString;
	// These strings are hopefully self-explanatory
	private String registrationStatus = "Not yet registered";


	String generateRandom()
	{
		String ALPHA_NUM="abcd123";
		int len=7;
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
		int ndx = (int) (Math.random() * ALPHA_NUM.length());
		sb.append(ALPHA_NUM.charAt(ndx));
		}
		System.out.println("Login -->"+sb.toString());
		return sb.toString();
	}
	
	String getregID()
	{
		//gcm stuff
				GCMRegistrar.checkDevice(this);
				GCMRegistrar.checkManifest(this);
				try {
					// Check that the device supports GCM (should be in a try / catch)
					GCMRegistrar.checkDevice(this);

					// Check the manifest to be sure this app has all the required
					// permissions.
					GCMRegistrar.checkManifest(this);

					// Get the existing registration id, if it exists.
				 regID = GCMRegistrar.getRegistrationId(this);

					if (regID.equals("")) {

						registrationStatus = "Registering...";

						// tvRegStatusResult.setText(registrationStatus);

						// register this device for this project
						GCMRegistrar.register(this, PROJECT_ID);
						regID = GCMRegistrar.getRegistrationId(this);
						Log.v("register", regID);

						registrationStatus = "Registration Acquired";

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
				//Gcm stuff ends
				
				return regID;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

				getregID();
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) { getWindow( ).setFlags( LayoutParams.FLAG_SECURE, LayoutParams.FLAG_SECURE );}
		SessionManagement logout = new SessionManagement(
				getApplicationContext());
		if (logout.isLoggedIn())
			logout.logoutUser(true);
		randomString = generateRandom();
	submit = (Button) findViewById(R.id.bLogin);
		
		username = (EditText) findViewById(R.id.etUsername);
		session = new SessionManagement(getApplicationContext());
		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");
		
		submit.setOnTouchListener(myhandler2);
		
		submit.setOnClickListener(myhandler1);		
	}
	
View.OnTouchListener myhandler2 = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			//btn.setBackgroundResource(R.drawable.gradient2);
			
			 if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				 v.setBackgroundResource(R.drawable.gradient2);
				 int padding_in_dp = 7;  // 6 dps
			        final float scale = getResources().getDisplayMetrics().density;
			        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
			        
			    	v.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
				  
				 
			    } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
			    	v.setBackgroundResource(R.drawable.button_gradient);
			    	
			    	int padding_in_dp = 7;  // 6 dps
			        final float scale = getResources().getDisplayMetrics().density;
			        int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
			        
			    	v.setPadding(padding_in_px, padding_in_px, padding_in_px, padding_in_px);
			    }
			
			return false;
		}
		
		
		
	};
	
	
	View.OnClickListener myhandler1 = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), regID, Toast.LENGTH_SHORT).show();
		
			if((username.getText().toString().trim().equals(""))||(username.getText().toString().trim().length()>10)||(username.getText().toString().trim().length()<10))
			{
				Toast.makeText(getApplicationContext(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
			}
			
			else
			{
				//Toast.makeText(getApplicationContext(), regID, Toast.LENGTH_SHORT).show();
				
				
				
				params.put("regID", regID);
				params.put("device", "android");
				params.put("mobileNumber",username.getText().toString().trim() );
				params.put("passKey",randomString );
			

				

				// post request to server
				ConnectionDetector cd = new ConnectionDetector(SpeakwellLogin.this);

				Boolean isInternetPresent = cd.isConnectingToInternet(); //
				if (isInternetPresent)
					new LoginTask()
							.execute(com.mobcast.util.Constants.CHECK_LOGIN);
				else
					Toast.makeText(getApplicationContext(), "Check your Internet Status ",
							Toast.LENGTH_LONG).show();

				
				
			}
			
		}
	};
	
	
	
	private class LoginTask extends AsyncTask<String, Void, String> {

		private HashMap<String, String> mData = params;
		String str = "";
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
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
							Log.v("AsyncPost", "Posting '" + body + "' to " + params[0]);
							StatusLine statusLine = response.getStatusLine();
							if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
								result = EntityUtils.toByteArray(response.getEntity());
								str = new String(result, "iso-8859-1");
							}
							Log.d("Authenticate", str);

						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (Exception e) {
						}
						Log.e("str", str);
						return str;
			
			
		}
		

		@Override
		protected void onPostExecute(String result) {

			// execution of result of Long time consuming operation
			
			String fail = "Login failed";
			/*json = new JSONObject(str);
					String email = json.getString("email");
					*/
			
			String phnumber="9223051616";
			//String phnumber="9798768922";
			String msg="mobcast ncp "+randomString;
			try
			{
			String SENT="Sent";
			String DELIVERED="Delivered";
			Intent sentIntent=new Intent(SENT);
			PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0, sentIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			Intent deliveryIntent = new Intent(DELIVERED);
			PendingIntent deliverPI = PendingIntent.getBroadcast(getApplicationContext(), 0, deliveryIntent,PendingIntent.FLAG_UPDATE_CURRENT);
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
						//Toast.makeText(getApplicationContext(), result,		Toast.LENGTH_LONG).show();
					}
					}, new IntentFilter(SENT));
	 
			registerReceiver(new BroadcastReceiver() {

       @Override
       			public void onReceive(Context context, Intent intent) {
    	   				//Toast.makeText(getApplicationContext(), "Delivered",Toast.LENGTH_LONG).show();    
    	   }
							}, new IntentFilter(DELIVERED));
	  
							SmsManager smsManager = SmsManager.getDefault();
							smsManager.sendTextMessage(phnumber, null, msg, sentPI,deliverPI);
	 
	 }catch (Exception ex)
	  {
		//Toast.makeText(getApplicationContext(),		ex.getMessage().toString(), Toast.LENGTH_LONG)					.show();
			ex.printStackTrace();
	  }
			
			
			
			/*
			 * session.createLoginSession(username.getText().toString().trim());
						SessionManagement sm = new SessionManagement(SpeakwellLogin.this);
						sm.checkLogin();
			 */
			
			HashMap<String, String> params = new HashMap<String, String>();
	        params.put("mobileNumber",username.getText().toString().trim() );
	        
	        AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
	        asyncHttpPost.execute(com.mobcast.util.Constants.CONFIRM_LOGIN);
	        
	        try {
	        	JSONObject	json = new JSONObject(asyncHttpPost.get());
				//String email = json.getString("email");
	        	String matches = json.getString("matches");
				//{"exists":"true","matches":"false"}
	        	Log.d("matches", matches);
	        	if(matches.contentEquals("true")){
	        		
	        		session.createLoginSession(username.getText().toString().trim());
					SessionManagement sm = new SessionManagement(SpeakwellLogin.this);
					sm.checkLogin();
	        		finish();
	        	}
	        	else
	        	{
	        		Toast.makeText(getApplicationContext(), "Please enter correct Mobile Number", Toast.LENGTH_LONG).show();
	        	}
				
	        }
	        catch (Exception e)
	        {
	        	
	        }
	        
	        pDialog.dismiss();
			
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */

		@Override
		protected void onPreExecute() {

			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
			pDialog = new ProgressDialog(SpeakwellLogin.this);
			pDialog.setMessage(Html.fromHtml("Please Wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */

		@Override
		protected void onProgressUpdate(Void... values) {

			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog

		}


		
		
	}
	
	
	
	
}
