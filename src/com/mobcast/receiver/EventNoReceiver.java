package com.mobcast.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AnnounceDBAdapter;
import com.sanofi.in.mobcast.Reports;

public class EventNoReceiver extends WakefulBroadcastReceiver {

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
		dbID = mIntent.getExtras().getString("dbID");
		Utilities.cancelNotification(mContext);
		Reports reports = new Reports(mContext, "Event");
		reports.updateRead(mId);
		reply("0");
		reports.updateRSVP(mId, "no");
	}

	public void reply(String reply) {
		AnnounceDBAdapter db = new AnnounceDBAdapter(mContext);
		db.open();
		db.eventRsvp(reply, mId);
		db.readrow(dbID+"", "Event");
		db.close();
	}
}
