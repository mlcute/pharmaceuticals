package com.sanofi.in.mobcast;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.facebook.stetho.Stetho;
import com.mobcast.receiver.PullAlarmReceiver;
import com.mobcast.util.AppPreferences;
import com.mobcast.util.BuildVars;
import com.parse.Parse;

public class ApplicationLoader extends Application {

	public static SharedPreferences sharedPreferences;
	public static AppPreferences preferences;
	public static volatile Context applicationContext = null;
	public static volatile Handler applicationHandler = null;
	public static ApplicationLoader applicationLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "PSTWPwO4ak693e2MYlTIjwHAAhBbEJLvhSCWloYc",
				"MbYPj4t3BIOGbKbhUngIZ0L3QEO0AjWX2JWD4qea");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		applicationContext = getApplicationContext();
		applicationHandler = new Handler(applicationContext.getMainLooper());
		applicationLoader = this;
		preferences = new AppPreferences(this);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(BuildVars.DEBUG_STETHO){
			initStetho(applicationLoader);
		}
	}

	public static Context getApplication() {
		return applicationContext;
	}

	public static AppPreferences getPreferences() {
		return preferences;
	}

	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public static synchronized ApplicationLoader getInstance() {
		return applicationLoader;
	}
	
	/*
	 * STETHO DEBUG BRIDGE
	 */
	
	public static void initStetho(Context applicationContext){
		Stetho.initialize(
		        Stetho.newInitializerBuilder(applicationContext)
		            .enableDumpapp(Stetho.defaultDumperPluginsProvider(applicationContext))
		            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(applicationContext))
		            .build());
	}
	
	// SA VIKALP PULL SERVICE
		public static void setAlarm() {
			PullAlarmReceiver alarm = new PullAlarmReceiver();
			alarm.setAlarm(getApplication().getApplicationContext());
			ApplicationLoader.getPreferences().setPullAlarmService(true);
			ApplicationLoader.getPreferences().setLoggedIn(true);
		}
		// EA VIKALP PULL SERVICE
	/*
	 * private GoogleCloudMessaging gcm; private AtomicInteger msgId = new
	 * AtomicInteger(); private String regid; public static final String
	 * EXTRA_MESSAGE = "message"; public static final String PROPERTY_REG_ID =
	 * "registration_id"; private static final String PROPERTY_APP_VERSION =
	 * "appVersion"; private static final int PLAY_SERVICES_RESOLUTION_REQUEST =
	 * 9000;
	 * 
	 * 
	 * 
	 * 
	 * private RequestQueue mRequestQueue; public static
	 * ImageLoaderConfiguration imageLoaderConfiguration; private static
	 * volatile boolean applicationInited = false; public static volatile
	 * boolean isScreenOn = false;
	 * 
	 * public static final String TAG = ApplicationLoader.class.getSimpleName();
	 * 
	 * public static void postInitApplication() { if (applicationInited) {
	 * return; } applicationInited = true; // UserConfig.loadConfig(); //
	 * ConnectionsManager.getInstance().initPushConnection();
	 * 
	 * ApplicationLoader app = (ApplicationLoader)
	 * ApplicationLoader.applicationContext; app.initPlayServices(); }
	 * 
	 * 
	 * 
	 * public static Context getApplication() { return applicationContext; }
	 * 
	 * public static ImageLoaderConfiguration getImageLoaderConfiguration() {
	 * return imageLoaderConfiguration; }
	 * 
	 * public static AppPreferences getPreferences() { return preferences; }
	 * 
	 * public static SharedPreferences getSharedPreferences() { return
	 * sharedPreferences; }
	 * 
	 * public static synchronized ApplicationLoader getInstance() { return
	 * applicationLoader; }
	 * 
	 * public RequestQueue getRequestQueue() { if (mRequestQueue == null) {
	 * mRequestQueue = Volley.newRequestQueue(getApplicationContext()); }
	 * 
	 * return mRequestQueue; }
	 * 
	 * public <T> void addToRequestQueue(Request<T> req, String tag) {
	 * req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
	 * getRequestQueue().add(req); }
	 * 
	 * public <T> void addToRequestQueue(Request<T> req) { req.setTag(TAG);
	 * getRequestQueue().add(req); }
	 * 
	 * public void cancelPendingRequests(Object tag) { if (mRequestQueue !=
	 * null) { mRequestQueue.cancelAll(tag); } }
	 * 
	 * 
	 * public void setUpFacebookSampler(){ Permission[] permissions = new
	 * Permission[] { Permission.PUBLIC_PROFILE, Permission.USER_FRIENDS,
	 * Permission.EMAIL }; SimpleFacebookConfiguration configuration = new
	 * SimpleFacebookConfiguration.Builder()
	 * .setAppId(ApplicationLoader.getApplication
	 * ().getResources().getString(R.string.facebook_app_id))
	 * .setNamespace("tellus") .setPermissions(permissions)
	 * .setDefaultAudience(SessionDefaultAudience.FRIENDS)
	 * .setAskForAllPermissionsAtOnce(true) .build();
	 * SimpleFacebook.setConfiguration(configuration); }
	 * 
	 * 
	 * 
	 * public static void startPushService() { SharedPreferences preferences =
	 * applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);
	 * if (preferences.getBoolean("pushService", true)) {
	 * applicationContext.startService(new Intent(applicationContext,
	 * NotificationsService.class)); if (android.os.Build.VERSION.SDK_INT >= 19)
	 * { Calendar cal = Calendar.getInstance(); PendingIntent pintent =
	 * PendingIntent.getService(applicationContext, 0, new
	 * Intent(applicationContext, NotificationsService.class), 0); AlarmManager
	 * alarm = (AlarmManager)
	 * applicationContext.getSystemService(Context.ALARM_SERVICE);
	 * alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30000,
	 * pintent); } } else { stopPushService(); } } public static void
	 * stopPushService() { applicationContext.stopService(new
	 * Intent(applicationContext, NotificationsService.class)); PendingIntent
	 * pintent = PendingIntent.getService(applicationContext, 0, new
	 * Intent(applicationContext, NotificationsService.class), 0); AlarmManager
	 * alarm =
	 * (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
	 * alarm.cancel(pintent); }
	 * 
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); try {
	 * LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
	 * Utilities.checkDisplaySize(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 * 
	 * 
	 * 
	 * public static void resetLastPauseTime() { if (lastPauseTime != 0 &&
	 * System.currentTimeMillis() - lastPauseTime > 5000) {
	 * ContactsController.getInstance().checkContacts(); } lastPauseTime = 0;
	 * ConnectionsManager.getInstance().applicationMovedToForeground(); }
	 * 
	 * 
	 * private void initPlayServices() { if (checkPlayServices()) { gcm =
	 * GoogleCloudMessaging.getInstance(this); regid = getRegistrationId();
	 * 
	 * Log.i(TAG, regid); if (regid.length() == 0) { registerInBackground(); }
	 * else { sendRegistrationIdToBackend(); } } else { Log.i(TAG,
	 * "No valid Google Play Services APK found."); } }
	 * 
	 * private boolean checkPlayServices() { int resultCode =
	 * GooglePlayServicesUtil .isGooglePlayServicesAvailable(this); if
	 * (resultCode != ConnectionResult.SUCCESS) {
	 * 
	 * if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	 * GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	 * PLAY_SERVICES_RESOLUTION_REQUEST).show(); } else { Log.i(TAG,
	 * "This device is not supported."); finish(); }
	 * 
	 * return false; } return true; }
	 * 
	 * private String getRegistrationId() { final SharedPreferences prefs =
	 * getGCMPreferences(applicationContext); String registrationId =
	 * prefs.getString(PROPERTY_REG_ID, ""); if (registrationId.length() == 0) {
	 * Log.i(TAG, "Registration not found."); return ""; } int registeredVersion
	 * = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE); int
	 * currentVersion = getAppVersion(); if (registeredVersion !=
	 * currentVersion) { Log.i(TAG, "App version changed."); return ""; } return
	 * registrationId; }
	 * 
	 * private SharedPreferences getGCMPreferences(Context context) { return
	 * getSharedPreferences(ApplicationLoader.class.getSimpleName(),
	 * Context.MODE_PRIVATE); }
	 * 
	 * public static int getAppVersion() { try { PackageInfo packageInfo =
	 * applicationContext.getPackageManager()
	 * .getPackageInfo(applicationContext.getPackageName(), 0); return
	 * packageInfo.versionCode; } catch (PackageManager.NameNotFoundException e)
	 * { throw new RuntimeException("Could not get package name: " + e); } }
	 * 
	 * private void registerInBackground() { AsyncTask<String, String, Boolean>
	 * task = new AsyncTask<String, String, Boolean>() {
	 * 
	 * @Override protected Boolean doInBackground(String... objects) { if (gcm
	 * == null) { gcm = GoogleCloudMessaging.getInstance(applicationContext); }
	 * int count = 0; while (count < 100) { try { count++; regid =
	 * gcm.register(Constants.PROJECT_ID);
	 * storeRegistrationId(applicationContext, regid); if
	 * (!ApplicationLoader.getSharedPreferences()
	 * .getBoolean("isRegisteredToServer", false)) { } else {
	 * Log.i("AndroidToServer", "Already registered to server"); } return true;
	 * } catch (Exception e) { } try { if (count % 20 == 0) { Thread.sleep(60000
	 * * 30); } else { Thread.sleep(5000); } } catch (InterruptedException e) {
	 * } } return false; } };
	 * 
	 * if (android.os.Build.VERSION.SDK_INT >= 11) {
	 * task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
	 * } else { task.execute(null, null, null); } }
	 * 
	 * private void storeRegistrationId(Context context, String regId) { final
	 * SharedPreferences prefs = getGCMPreferences(context); int appVersion =
	 * getAppVersion(); Log.i(TAG, "appVersion :" + appVersion); Log.i(TAG,
	 * "regId :" + regId); SharedPreferences.Editor editor = prefs.edit();
	 * editor.putString(PROPERTY_REG_ID, regId);
	 * editor.putInt(PROPERTY_APP_VERSION, appVersion); editor.commit();
	 * 
	 * ApplicationLoader.getPreferences().setRegId(regId); }
	 */
}
