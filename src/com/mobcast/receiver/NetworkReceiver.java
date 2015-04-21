/**
 * 
 */
package com.mobcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.mobcast.service.PullAlarmService;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.SessionManagement;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class NetworkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo activeWifiInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeHighNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI);

		boolean isMobileConnected = activeNetInfo != null
				&& activeNetInfo.isConnectedOrConnecting();

		boolean isHighSpeedConnected = activeHighNetInfo != null
				&& activeHighNetInfo.isConnectedOrConnecting();

		boolean isWifiConnected = activeWifiInfo != null
				&& activeWifiInfo.isConnectedOrConnecting();
		if (isWifiConnected || isMobileConnected || isHighSpeedConnected) {
			if (ApplicationLoader.getPreferences().isLoggedIn()) {
				if (!ApplicationLoader.getPreferences().isPullAlarmService()) {
					SessionManagement sm = new SessionManagement(
							context);
					sm.getLastIdFromPreferences();
					ApplicationLoader.setAlarm();
				}
				if(TextUtils.isEmpty(ApplicationLoader.getPreferences().getInstallationDate())){
					ApplicationLoader.getPreferences().setInstallationDate(Utilities.getTodayDate());
				}
				context.startService(new Intent(context,PullAlarmService.class));
			}
		}
	}
}
