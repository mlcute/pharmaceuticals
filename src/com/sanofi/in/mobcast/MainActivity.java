package com.sanofi.in.mobcast;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager.LayoutParams;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class MainActivity extends Activity {

	// This string will hold the lengthy registration id that comes
	// from GCMRegistrar.register()
	private String regID = "";
	SessionManagement sm;
	// These strings are hopefully self-explanatory
	

	// This intent filter will be set to filter on the string
	// "GCM_RECEIVED_ACTION"

	// textviews used to show the status of our app's registration, and the
	// latest
	// broadcast message.

	// This broadcastreceiver instance will receive messages broadcast
	// with the action "GCM_RECEIVED_ACTION" via the gcmFilter

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// ourSong.release();
		finish();

	}

	// Reminder that the onCreate() method is not just called when an app is
	// first opened,
	// but, among other occasions, is called when the device changes
	// orientation.
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		ConnectionDetector cd = new ConnectionDetector(MainActivity.this);
		if (cd.isConnectingToInternet()) {
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String email = pref.getString("LoginID", null);
			String regID = pref.getString("gcmregID", null);

			
		}
		
		setContentView(R.layout.activity_main);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					sm = new SessionManagement(
							MainActivity.this);
					sm.checkLogin();
//					ApplicationLoader.getPreferences().checkLogin();
				}
			}
		};
		timer.start();
		
		/*if(sm.isLoggedIn()){
		 Calendar cal = Calendar.getInstance();
		 // add 5 minutes to the calendar object
		 //cal.add(Calendar.MINUTE, 1);
		 Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
		 intent.putExtra("alarm_message", "hello");
		 // In reality, you would want to have a static variable for the request code instead of 192837
		 PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 
		 // Get the AlarmManager service
		 AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
	
		 am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 10000, sender);
	
		}*/
	}
	

	// There are no menus for this demo app. This is just
	// boilerplate code.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}

}
