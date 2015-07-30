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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.mobcast.calc.IncenDashBoardActivity;
import com.mobcast.myperformance.MyPerformanceActivity;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class Home1 extends Activity {

	ImageButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
	static ProgressDialog pDialog;

	private static final String TAG = Home1.class.getSimpleName();

	static final String KEY_COMPANYID = "companyID";
	HashMap<String, String> postParam;
	AnnounceDBAdapter adb;
	TextView unreadAnounce, unreadEvents, unreadNews, unreadTraining,
			unreadAwards, unreadFeedback;
	private EasyTracker easyTracker = null;
	private boolean isToDismiss = false;
	private ImageView mOverFlow;
	private RelativeLayout mLayout;
	private TextView mPopUpMenuLogOut;

	private TableRow mTableRow3;
	private LinearLayout mIncenModule;
	private LinearLayout mPerformanceModule;
	private TableLayout mTableLayout;

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Toast.makeText(getApplicationContext(), "received",
			// Toast.LENGTH_SHORT);
			onPause();
			onResume();

		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		super.onPause();
	}

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		
		setAlarm();//ADDED VIKALP PULL SERVICE

		// analytics code starts
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		easyTracker = EasyTracker.getInstance(Home1.this);

		// analytics code ends

		postParam = new HashMap<String, String>();
		postParam.put("companyID", getString(R.string.companyID));

		unreadAnounce = (TextView) findViewById(R.id.unreadannounce);
		unreadNews = (TextView) findViewById(R.id.unreadNews);
		unreadEvents = (TextView) findViewById(R.id.unreadEvents);
		unreadTraining = (TextView) findViewById(R.id.unreadTraining);
		unreadAwards = (TextView) findViewById(R.id.unreadAwards);
		unreadFeedback = (TextView) findViewById(R.id.unreadFeedback);

		btn1 = (ImageButton) findViewById(R.id.imageButton1);
		btn1.setOnClickListener(myhandler1);
		btn1.setOnTouchListener(myhandler2);

		btn2 = (ImageButton) findViewById(R.id.imageButton2);
		btn2.setOnClickListener(myhandler1);
		btn2.setOnTouchListener(myhandler2);

		btn3 = (ImageButton) findViewById(R.id.imageButton3);
		btn3.setOnClickListener(myhandler1);
		btn3.setOnTouchListener(myhandler2);

		btn4 = (ImageButton) findViewById(R.id.imageButton4);
		btn4.setOnClickListener(myhandler1);
		btn4.setOnTouchListener(myhandler2);

		btn5 = (ImageButton) findViewById(R.id.imageButton5);
		btn5.setOnClickListener(myhandler1);
		btn5.setOnTouchListener(myhandler2);

		btn6 = (ImageButton) findViewById(R.id.imageButton6);
		btn6.setOnClickListener(myhandler1);
		btn6.setOnTouchListener(myhandler2);

		btn7 = (ImageButton) findViewById(R.id.imageButton7);
		btn7.setOnClickListener(myhandler1);
		// btn7.setOnClickListener(feedhandler);
		btn7.setOnTouchListener(myhandler2);

		btn8 = (ImageButton) findViewById(R.id.imageButton8);
		btn8.setOnClickListener(myhandler1);
		btn8.setOnTouchListener(myhandler2);

		btn9 = (ImageButton) findViewById(R.id.imageButton9);
		btn9.setOnClickListener(myhandler1);

		btn9.setOnTouchListener(myhandler2);
		mLayout = (RelativeLayout) findViewById(R.id.mLayout);
		mOverFlow = (ImageView) findViewById(R.id.menuOverflow);
		mOverFlow.setOnClickListener(myhandler1);

		mTableLayout = (TableLayout) findViewById(R.id.tableLayout1);
		mIncenModule = (LinearLayout) findViewById(R.id.incenModule);
		mPerformanceModule = (LinearLayout) findViewById(R.id.performanceModule);
		mTableRow3 = (TableRow) findViewById(R.id.tableRow2);

		adb = new AnnounceDBAdapter(Home1.this);

		try {
			Context mContext = Home1.this;
			String pkg = mContext.getPackageName();
			String mVersionNumber = mContext.getPackageManager()
					.getPackageInfo(pkg, 0).versionName;
			// Toast.makeText(mContext, mVersionNumber,
			// Toast.LENGTH_SHORT).show();

			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String temp1 = pref.getString("name", "akshay@bombil.com");

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("deviceType", "android");
			params.put("currentVersion", mVersionNumber);
			params.put(com.mobcast.util.Constants.user_id, temp1);
			if (TextUtils
					.isEmpty(ApplicationLoader.getPreferences().getRegId())) {
				params.put("regID", Utilities.getregID(Home1.this));
			} else {
				params.put("regID", ApplicationLoader.getPreferences()
						.getRegId());
			}
			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
			asyncHttpPost
					.execute(com.mobcast.util.Constants.CHECK_VERSION_UPDATES);
			// JSONObject jsonObj = new JSONObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		;
		setIncenModule();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		IntentFilter filter = new IntentFilter();
		filter.addAction("MyGCMMessageReceived");
		registerReceiver(receiver, filter);

		adb.open();

		// Log.d("Announcement", adb.getUnreadCount("Announce") + "");
		int u1 = adb.getUnreadCount("Announce");
		if (u1 < 1)
			unreadAnounce.setVisibility(TextView.INVISIBLE);
		else {
			unreadAnounce.setText(u1 + "");
			unreadAnounce.setVisibility(TextView.VISIBLE);
		}

		// Log.d("Events", adb.getUnreadCount("Event") + "");
		int u2 = adb.getUnreadCount("Event");
		if (u2 < 1)
			unreadEvents.setVisibility(TextView.INVISIBLE);
		else {
			unreadEvents.setText(u2 + "");
			unreadEvents.setVisibility(TextView.VISIBLE);
		}

		// Log.d("News", adb.getUnreadCount("News") + "");
		int u3 = adb.getUnreadCount("news");
		if (u3 < 1)
			unreadNews.setVisibility(TextView.INVISIBLE);
		else {
			unreadNews.setText(u3 + "");
			unreadNews.setVisibility(TextView.VISIBLE);
		}

		// Log.d("Training", adb.getUnreadCount("Training") + "");
		int u4 = adb.getUnreadCount("training");
		if (u4 < 1)
			unreadTraining.setVisibility(TextView.INVISIBLE);
		else {
			unreadTraining.setText(u4 + "");
			unreadTraining.setVisibility(TextView.VISIBLE);
		}

		// Log.d("Award", adb.getUnreadCount("Award") + "");
		int u5 = adb.getUnreadCount("Award");
		if (u5 < 1)
			unreadAwards.setVisibility(TextView.INVISIBLE);
		else {
			unreadAwards.setText(u5 + "");
			unreadAwards.setVisibility(TextView.VISIBLE);
		}

		// Log.d("Feedback", adb.getUnreadCount("Feedback") + "");
		int u6 = adb.getUnreadCount("Feedback");
		if (u6 < 1)
			unreadFeedback.setVisibility(TextView.INVISIBLE);
		else {
			unreadFeedback.setText(u6 + "");
			unreadFeedback.setVisibility(TextView.VISIBLE);
		}

		adb.close();

		super.onResume();
		// Anounce, Award, Document, Event, Feedback, News, Training,
		Log.i(TAG, "regId " + ApplicationLoader.getPreferences().getRegId());
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			if (v.getId() == R.id.imageButton1) {
				// Intent media = new
				// Intent("com.mobcast.ncp.AnnounceListView");
				Intent media = new Intent(Home1.this, AnnounceListView.class);
				easyTracker.send(MapBuilder.createEvent(
						"home screen click event", "announcement module",
						"button_name/id", null).build());

				startActivity(media);

			}

			else if (v.getId() == R.id.imageButton2) {
				// Intent event = new Intent("com.mobcast.ncp.EventListView");
				Intent event = new Intent(Home1.this, EventListView.class);
				easyTracker.send(MapBuilder.createEvent(
						"home screen click event", "event module",
						"button_name/id", null).build());
				startActivity(event);
			}

			else if (v.getId() == R.id.imageButton3) {
				// Intent news = new Intent("com.mobcast.ncp.NewsList");
				Intent news = new Intent(Home1.this, NewsList.class);
				easyTracker.send(MapBuilder.createEvent(
						"home screen click event", "news module",
						"button_name/id", null).build());
				startActivity(news);
			}

			else if (v.getId() == R.id.imageButton4) {

				boolean isInternetPresent;

				ConnectionDetector cd = new ConnectionDetector(
						getApplicationContext());
				isInternetPresent = cd.isConnectingToInternet();

				if (isInternetPresent) {

					RecruitTask asyncHttpPost = new RecruitTask();
					easyTracker.send(MapBuilder.createEvent(
							"home screen click event", "Recruitment module",
							"button_name/id", null).build());
					asyncHttpPost
							.execute(com.mobcast.util.Constants.CHECK_RECRUITMENT);
				}

			}

			else if (v.getId() == R.id.imageButton5) {
				// Intent share = new
				// Intent("com.mobcast.ncp.TrainingListView");
				// Intent share = new Intent(Home1.this,
				// TrainingListView.class);
				// easyTracker.send(MapBuilder.createEvent("home screen click event",
				// "Training module", "button_name/id", null).build());
				Intent share = new Intent(Home1.this,
						IncenDashBoardActivity.class);
				startActivity(share);
			}

			else if (v.getId() == R.id.imageButton6) {
				// Intent award = new Intent("com.mobcast.ncp.AwardList");
				Intent award = new Intent(Home1.this, AwardList.class);
				easyTracker.send(MapBuilder.createEvent(
						"home screen click event", "Award module",
						"button_name/id", null).build());
				startActivity(award);
			}

			else if (v.getId() == R.id.imageButton7) {

				// Toast.makeText(getBaseContext(), "test",
				// Toast.LENGTH_SHORT).show();
				Intent myIntent = new Intent(Home1.this, FeedbackNewList.class);
				easyTracker.send(MapBuilder.createEvent(
						"home screen click event", "Feedback module",
						"button_name/id", null).build());
				startActivity(myIntent);

			}

			else if (v.getId() == R.id.imageButton8) {
				Intent myPerformance = new Intent(Home1.this, MyPerformanceActivity.class);
				startActivity(myPerformance);
			}

			else if (v.getId() == R.id.imageButton9) {
				// logOut();
				Intent mIntent = new Intent(Home1.this, AboutActivity.class);
				startActivity(mIntent);
			}

			else if (v.getId() == R.id.menuOverflow) {
				showPopUpMenu();
			}

		}
	};

	private void logOut() {
		SessionManagement logout = new SessionManagement(
				getApplicationContext());
		;
		SharedPreferences pref;
		pref = getSharedPreferences("MobCastPref", 0);
		pref.edit().putString("gcmregID", null).commit();
		String email = pref.getString("LoginID", null);
		String regID = pref.getString("gcmregID", null);

		ConnectionDetector cd = new ConnectionDetector(Home1.this);
		if (cd.isConnectingToInternet()) {
			// SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			// String email = pref.getString("LoginID" ,null);
			// String regID = pref.getString("gcmregID" , null);

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("update", "yes");
			params.put("id", "1");
			params.put("mobileNumber",
					logout.getUserDetails().get(SessionManagement.KEY_NAME));
			params.put("companyID", getString(R.string.companyID));

			com.sanofi.in.mobcast.AsyncHttpPost asyncHttpPost = new com.sanofi.in.mobcast.AsyncHttpPost(
					params);
			asyncHttpPost.execute(com.mobcast.util.Constants.M_UPDATE);

			com.sanofi.in.mobcast.AsyncHttpPost asyncHttpPost1 = new com.sanofi.in.mobcast.AsyncHttpPost(
					params);
			asyncHttpPost1.execute(com.mobcast.util.Constants.CHECK_LOGIN);

		}

		logout.logoutUser(true);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		prefs.edit().putBoolean(getString(R.string.isloggedin), false).commit();
		// ApplicationLoader.getPreferences().logoutUser(true);
		ApplicationLoader.getPreferences().setLoggedIn(false);
		AnnounceDBAdapter adb = new AnnounceDBAdapter(Home1.this);
		adb.open();
		adb.remotewipe(Home1.this);
		adb.close();
		clearPreferences();
		finish();
	}

	public void clearPreferences() {
		try {
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			pref.edit().putString("lastEvent", "0").commit();
			pref.edit().putString("lastAnnounce", "0").commit();
			pref.edit().putString("lastTraining", "0").commit();
			pref.edit().putString("lastNews", "0").commit();
			pref.edit().putString("lastAward", "0").commit();
			//SA ADDED VIKALP PULL SERVICE
			ApplicationLoader.getPreferences().setLastAnnouncementId("0");
			ApplicationLoader.getPreferences().setLastAwardsId("0");
			ApplicationLoader.getPreferences().setLastEventsId("0");
			ApplicationLoader.getPreferences().setLastFeedbackId("0");
			ApplicationLoader.getPreferences().setLastNewsId("0");
			ApplicationLoader.getPreferences().setLastTrainingId("0");
			ApplicationLoader.getPreferences().setPullAlarmService(false);
			ApplicationLoader.getPreferences().setInstallationDate(null);
			//EA ADDED VIKALP PULL SERVICE
			
			getSharedPreferences("MobCastPref", 0).edit().clear().commit();
			clearIncentivePreferences();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearIncentivePreferences() {
		try {
			String nullString[] = new String[] { null, null, null };
			ApplicationLoader.getPreferences().setIncenFirstTime(false);

			ApplicationLoader.getPreferences().setCoachMarksFirstTime(false);

			ApplicationLoader.getPreferences().setMonthQuarterJSON(null);
			ApplicationLoader.getPreferences().setProductJSON(null);

			ApplicationLoader.getPreferences().setQuarterCheck1(nullString);
			ApplicationLoader.getPreferences().setQuarterValue1(nullString);
			ApplicationLoader.getPreferences().setQuarter1(nullString);
			ApplicationLoader.getPreferences().setQuarterTotal1(null);

			ApplicationLoader.getPreferences().setQuarterCheck2(nullString);
			ApplicationLoader.getPreferences().setQuarterValue2(nullString);
			ApplicationLoader.getPreferences().setQuarter2(nullString);
			ApplicationLoader.getPreferences().setQuarterTotal2(null);

			ApplicationLoader.getPreferences().setQuarterCheck3(nullString);
			ApplicationLoader.getPreferences().setQuarterValue3(nullString);
			ApplicationLoader.getPreferences().setQuarter3(nullString);
			ApplicationLoader.getPreferences().setQuarterTotal3(null);

			ApplicationLoader.getPreferences().setQuarterCheck4(nullString);
			ApplicationLoader.getPreferences().setQuarterValue4(nullString);
			ApplicationLoader.getPreferences().setQuarter4(nullString);
			ApplicationLoader.getPreferences().setQuarterTotal4(null);

			// QUARTER RESET
			ApplicationLoader.getPreferences().setQ1(null);
			ApplicationLoader.getPreferences().setQValue1(null);

			ApplicationLoader.getPreferences().setQ2(null);
			ApplicationLoader.getPreferences().setQValue2(null);

			ApplicationLoader.getPreferences().setQ3(null);
			ApplicationLoader.getPreferences().setQValue3(null);

			ApplicationLoader.getPreferences().setQ4(null);
			ApplicationLoader.getPreferences().setQValue4(null);

			// PRODUCT RESET
			int productNumber = Integer.parseInt(ApplicationLoader
					.getPreferences().getProductNumber());
			String nullProductString[] = new String[productNumber];
			for (int i = 0; i < productNumber; i++) {
				nullProductString[i] = null;
			}

			ApplicationLoader.getPreferences().setProduct1(nullProductString);
			ApplicationLoader.getPreferences().setProductValue1(
					nullProductString);
			ApplicationLoader.getPreferences().setProductTotalValue1(null);

			ApplicationLoader.getPreferences().setProduct2(nullProductString);
			ApplicationLoader.getPreferences().setProductValue2(
					nullProductString);
			ApplicationLoader.getPreferences().setProductTotalValue2(null);

			ApplicationLoader.getPreferences().setProduct3(nullProductString);
			ApplicationLoader.getPreferences().setProductValue3(
					nullProductString);
			ApplicationLoader.getPreferences().setProductTotalValue3(null);

			ApplicationLoader.getPreferences().setProduct4(nullProductString);
			ApplicationLoader.getPreferences().setProductValue4(
					nullProductString);
			ApplicationLoader.getPreferences().setProductTotalValue4(null);

			// KPI RESET
			ApplicationLoader.getPreferences().setKPIQ1(nullString);
			ApplicationLoader.getPreferences().setKPIQValue1(null);
			ApplicationLoader.getPreferences().setKPI1(nullProductString);
			ApplicationLoader.getPreferences().setKPIValue1(null);
			ApplicationLoader.getPreferences().setKPITotalValue1(null);

			ApplicationLoader.getPreferences().setKPIQ2(nullString);
			ApplicationLoader.getPreferences().setKPIQValue2(null);
			ApplicationLoader.getPreferences().setKPI2(nullProductString);
			ApplicationLoader.getPreferences().setKPIValue2(null);
			ApplicationLoader.getPreferences().setKPITotalValue2(null);

			ApplicationLoader.getPreferences().setKPIQ3(nullString);
			ApplicationLoader.getPreferences().setKPIQValue3(null);
			ApplicationLoader.getPreferences().setKPI3(nullProductString);
			ApplicationLoader.getPreferences().setKPIValue3(null);
			ApplicationLoader.getPreferences().setKPITotalValue3(null);

			ApplicationLoader.getPreferences().setKPIQ4(nullString);
			ApplicationLoader.getPreferences().setKPIQValue4(null);
			ApplicationLoader.getPreferences().setKPI4(nullProductString);
			ApplicationLoader.getPreferences().setKPIValue4(null);
			ApplicationLoader.getPreferences().setKPITotalValue4(null);

			ApplicationLoader.getPreferences().setIncenPdfPath("");
			ApplicationLoader.getPreferences().setIncenModuleEnable(false);
			ApplicationLoader.getPreferences()
					.setIncenModuleLayoutEnable(false);

			clearTeamWisePreferences();	
			
			SharedPreferences pref = ApplicationLoader.getSharedPreferences();
			pref.edit().clear().commit();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private void clearTeamWisePreferences(){
		try{
			ApplicationLoader.getPreferences().setIncenTeamName(null);
			
			String nullString[] = new String[] { null, null, null };
			ApplicationLoader.getPreferences().setIncenBioSurgeryTeam(false);
			ApplicationLoader.getPreferences().setIncenRenealTeam(false);
			ApplicationLoader.getPreferences().setIncenHeritageTeam(false);
			
			//Product
			ApplicationLoader.getPreferences().setIncenBioSurgeryVialSlider1(null);
			ApplicationLoader.getPreferences().setIncenBioSurgeryVialSlider2(null);
			ApplicationLoader.getPreferences().setIncenBioSurgeryVialSlider3(null);
			ApplicationLoader.getPreferences().setIncenBioSurgeryVialSlider4(null);
			
			//KPI
			 ApplicationLoader.getPreferences().setKPIRightFreqQ1(null);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ2(null);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ3(null);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ4(null);
			 
			 ApplicationLoader.getPreferences().setKPIPOBAvgQ1(null);
			 ApplicationLoader.getPreferences().setKPIPOBAvgQ2(null);
			 ApplicationLoader.getPreferences().setKPIPOBAvgQ3(null);
			 ApplicationLoader.getPreferences().setKPIPOBAvgQ4(null);
			 
			 ApplicationLoader.getPreferences().setKPIRightFreqQ1(nullString);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ2(nullString);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ3(nullString);
			 ApplicationLoader.getPreferences().setKPIRightFreqQ4(nullString);
			 
			 ApplicationLoader.getPreferences().setKPIPOBQ1(nullString);
			 ApplicationLoader.getPreferences().setKPIPOBQ2(nullString);
			 ApplicationLoader.getPreferences().setKPIPOBQ3(nullString);
			 ApplicationLoader.getPreferences().setKPIPOBQ4(nullString);
				
			//Annual
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
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

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_main, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // TODO
	 * Auto-generated method stub switch (item.getItemId()) { case
	 * R.id.menu_logout: logOut(); return true; default: return
	 * super.onOptionsItemSelected(item); } }
	 */

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
					nameValuePair.add(new BasicNameValuePair("mobileNumber",
							getPreferences(MODE_PRIVATE).getString("name",
									"9930688306")));
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
			pDialog.dismiss();
			// Intent i = new Intent("com.mobcast.ncp.RecruitList");
			Intent i = new Intent(Home1.this, RecruitList.class);
			i.putExtra(RecruitList.RECRUIT_DATA, result);
			startActivity(i);
		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(Home1.this);
			pDialog.setMessage(Html.fromHtml("Please Wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}// end of onPreExecute

	}// end of RecruitTask class

	class AsyncHttpPost extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		String str = "";
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;

		/**
		 * constructor
		 */

		public AsyncHttpPost(HashMap<String, String> data) {
			mData = data;
		}

		public AsyncHttpPost(HashMap<String, String> data, Activity activity) {

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

			JSONObject jObject;
			try {
				jObject = new JSONObject(str);

				String update = jObject.getString("result");
				Log.e("result", update);

				JSONObject jObject1 = new JSONObject(update);
				String update1 = jObject1.getString("updateAvailable");
				String department = jObject1.getString("department");
				Log.e("update1", update1);
				if (update1.contentEquals("yes")) {
					showUpdateAppDialog();
				}

				try {
					department = jObject.getString("department");
//					ApplicationLoader.getPreferences().setDesignation(jObject.getString("designation"));
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
					setIncenModule();
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//SA VIKALP ADDED PULL SERVICE
	private void setAlarm(){
		if (!ApplicationLoader.getPreferences().isPullAlarmService()) {
			getLastIdFromPreferences();//ADDED VIKALP PULL SERVICE
			ApplicationLoader.setAlarm();
		}
		
		if(TextUtils.isEmpty(ApplicationLoader.getPreferences().getInstallationDate())){
			ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());
		}
	}
	//EA VIKALP ADDED PULL SERVICE

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

	public void showUpdateAppDialog() {
		final Dialog dialog = new Dialog(Home1.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.update_app_pop_up);

		TextView mCancel = (TextView) dialog
				.findViewById(R.id.update_app_pop_up_cancel);
		TextView mSubmit = (TextView) dialog
				.findViewById(R.id.update_app_pop_up_submit);

		mCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
			}
		});

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isToDismiss = true;
				dialog.dismiss();
				String url = Constants.APP_UPDATE_LINK;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		dialog.show();
	}

	@SuppressLint("NewApi")
	public void showPopUpMenu() {
		final Dialog dialog = new Dialog(Home1.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f;
			lp.gravity = Gravity.TOP | Gravity.RIGHT;
			lp.y = mOverFlow.getHeight() + 10;
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			// dialog.getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.pop_up_menu);

		mPopUpMenuLogOut = (TextView) dialog
				.findViewById(R.id.pop_up_menu_log_out_tv);

		if (Build.VERSION.SDK_INT < 11) {
			final AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
			animation.setDuration(1000);
			animation.setFillAfter(true);
			mLayout.startAnimation(animation);
		} else {
			mLayout.setAlpha(0.5f);
		}

		mPopUpMenuLogOut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showLogOutDialog();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (Build.VERSION.SDK_INT < 11) {
					final AlphaAnimation animation = new AlphaAnimation(0.5f,
							1.0f);
					animation.setDuration(1000);
					animation.setFillAfter(true);
					mLayout.startAnimation(animation);
				} else {
					mLayout.setAlpha(1.0f);
				}
			}
		});
		dialog.show();
	}

	private void showLogOutDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				Home1.this);
		// set title
		alertDialogBuilder
				.setTitle(getResources().getString(R.string.app_name));

		// set dialog message
		alertDialogBuilder
				.setMessage(
						getResources().getString(R.string.logout_alert_message))
				.setCancelable(true)
				.setPositiveButton("Log Out",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								logOut();
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void setIncenModule() {
		try {
			if (ApplicationLoader.getPreferences().isIncenModuleLayoutEnable()) {
				if (ApplicationLoader.getPreferences().isIncenModuleEnable()) {
					mTableRow3.setVisibility(View.VISIBLE);
					mIncenModule.setVisibility(View.VISIBLE);
					mPerformanceModule.setVisibility(View.VISIBLE);
					mTableLayout.invalidate();
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		FlurryAgent.onEndSession(this);
	}

}
