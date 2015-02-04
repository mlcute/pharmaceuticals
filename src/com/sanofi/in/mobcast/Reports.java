package com.sanofi.in.mobcast;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Reports {

	/**
	 * @param args
	 */

	String value = "1";
	String fID;
	String type;

	String mobileNumber;
	Context context;
	String link;
	String moduleName;

	public String KEY_ANNOUNCE_ADDRESS = com.mobcast.util.Constants.KEY_ANNOUNCE_ADDRESS;
	public String KEY_NEWS_ADDRESS = com.mobcast.util.Constants.KEY_NEWS_ADDRESS;
	public String KEY_EVENT_ADDRESS = com.mobcast.util.Constants.KEY_EVENT_ADDRESS;
	public String KEY_TRAINING_ADDRESS = com.mobcast.util.Constants.KEY_TRAINING_ADDRESS;
	public String KEY_RECRUITMENT_ADDRESS = com.mobcast.util.Constants.KEY_RECRUITMENT_ADDRESS;
	public String KEY_AWARDS_ADDRESS = com.mobcast.util.Constants.KEY_AWARDS_ADDRESS;
	public String KEY_FEEDBACK_ADDRESS = com.mobcast.util.Constants.KEY_FEEDBACK_ADDRESS;

	public Reports(Context context, String moduleName) {
		this.moduleName = moduleName;
		this.context = context;

		if (moduleName.contains("Announce"))
			link = KEY_ANNOUNCE_ADDRESS;
		else if (moduleName.contains("Event"))
			link = KEY_EVENT_ADDRESS;
		else if (moduleName.contains("News"))
			link = KEY_NEWS_ADDRESS;
		else if (moduleName.contains("Recruitment"))
			link = KEY_RECRUITMENT_ADDRESS;
		else if (moduleName.contains("Training"))
			link = KEY_TRAINING_ADDRESS;
		else if (moduleName.contains("Award"))
			link = KEY_AWARDS_ADDRESS;
		else if (moduleName.contains("Feedback"))
			link = KEY_FEEDBACK_ADDRESS;

		SharedPreferences prefs = context
				.getSharedPreferences("MobCastPref", 0);
		mobileNumber = prefs.getString("name", "akshay@bombil.com");

	}

	// for all modules
	public void updateRead(String fID) {
		type = "read";
		this.fID = fID;
		send();
	}

	public void updateShare(String fID) {
		type = "share";
		this.fID = fID;
		send();
	}

	// for announcement news and training
	public void updateDuration(String fID, String value) {
		type = "duration";
		this.value = value;
		this.fID = fID;
		send();
	}

	// for news
	public void updateLinkCLicked(String fID) {
		type = "linkClicked";
		this.fID = fID;
		send();
	}

	// for event module
	public void updateRSVP(String fID, String value) {
		type = "rsvp";
		this.value = value;
		this.fID = fID;
		send();
	}

	public void updateAddToCalendar(String fID) {
		type = "addToCalendar";
		this.fID = fID;
		send();

	}

	// for recruitment module
	public void updateRefferenceShared(String fID) {
		type = "referenceShared";
		this.fID = fID;
		send();
	}

	public void updateCallClicked(String fID) {
		type = "callClicked";
		this.fID = fID;
		send();
	}

	public void updateEmailClicked(String fID) {
		type = "emailClicked";
		this.fID = fID;
		send();
	}

	// for awards module
	public void updateCongratulate(String fID) {
		type = "congratulate";
		this.fID = fID;
		send();
	}

	// for feedback module
	public void updateAttempts(String fID) {
		type = "attempts";
		this.fID = fID;
		send();
	}

	// for notification module
	public void updateNotification(String fID) {
		type = "notification";
		this.fID = fID;
		send();
	}

	// to send the post to server
	void send() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fID", fID);
		params.put("type", type);
		params.put(com.mobcast.util.Constants.user_id, mobileNumber);
		params.put("value", value);

		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
		asyncHttpPost.execute(link);

		System.out.println("fID   ====>" + fID);
		System.out.println("type   ====>" + type);
		System.out.println("mobileNumber   ====>" + mobileNumber);
		System.out.println("value   ====>" + value);
		System.out.println("link   ====>" + link);

	}
}
