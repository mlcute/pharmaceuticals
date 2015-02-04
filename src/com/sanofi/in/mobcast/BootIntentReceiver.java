package com.sanofi.in.mobcast;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BootIntentReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent service = new Intent(context, BootIntentService.class);
		startWakefulService(context, service);
		
		AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	    /*Intent aIntent = new Intent(context,AlarmReceiver.class);
	   
		PendingIntent pendingIntent =
	               PendingIntent.getBroadcast(context, 0, aIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Calendar c = Calendar.getInstance();
	      // use inexact repeating which is easier on battery (system can phase events and not wake at exact times)
	      alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,c.getTimeInMillis() ,
	               10000, pendingIntent);*/
	}

}
