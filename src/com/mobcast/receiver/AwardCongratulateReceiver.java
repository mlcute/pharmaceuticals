package com.mobcast.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.Reports;

public class AwardCongratulateReceiver extends WakefulBroadcastReceiver {

	private Context mContext;
	private Intent mIntent;
	private String mId;
	private String dbID;

	@Override
	public void onReceive(Context mContext, Intent mIntent) {
		// TODO Auto-generated method stub
		this.mContext = mContext;
		this.mIntent = mIntent;
		mId = mIntent.getExtras().getString("id");
		Utilities.cancelNotification(mContext);
		Reports reports = new Reports(mContext, "Award");
		reports.updateRead(mId);
		reports.updateCongratulate(mId);
	}

}
