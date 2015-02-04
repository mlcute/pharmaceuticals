package com.sanofi.in.mobcast;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.WakefulBroadcastReceiver;

public class WifiReceiver extends WakefulBroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	       NetworkInfo info = cm.getActiveNetworkInfo();
	       if (info != null) {
	           if (info.isConnected()) {
	               //start service
	               
	        	   		Intent service = new Intent(context, WifiIntentService.class);
	        	   		startWakefulService(context, service);
	           		}
	       }
}
}
