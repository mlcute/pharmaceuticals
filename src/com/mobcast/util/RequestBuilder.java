package com.mobcast.util;

import org.json.JSONObject;

import com.sanofi.in.mobcast.ApplicationLoader;

public class RequestBuilder {

	private static final String TAG = RequestBuilder.class.getSimpleName();
	
	public static JSONObject getPostPullAlarmService() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("regID", ApplicationLoader.getPreferences().getRegId());
			stringBuffer.put(Constants.user_id, ApplicationLoader.getPreferences().getMobileNumber());
			stringBuffer.put("deviceType", "android");
			stringBuffer.put("device", "android");
			
			JSONObject mJSONObjAnn = new JSONObject();
			mJSONObjAnn.put("last_id", ApplicationLoader.getPreferences().getLastAnnouncementId());
//			mJSONObjAnn.put("last_id", "444");
			stringBuffer.put("announcement", mJSONObjAnn);
			
			JSONObject mJSONObjEve = new JSONObject();
			mJSONObjEve.put("last_id", ApplicationLoader.getPreferences().getLastEventsId());
//			mJSONObjEve.put("last_id", "64");
			stringBuffer.put("events", mJSONObjEve);
			
			JSONObject mJSONObjNews = new JSONObject();
			mJSONObjNews.put("last_id", ApplicationLoader.getPreferences().getLastNewsId());
//			mJSONObjNews.put("last_id", "119");
			stringBuffer.put("news", mJSONObjNews);
			
			JSONObject mJSONObjTraining = new JSONObject();
			mJSONObjTraining.put("last_id", ApplicationLoader.getPreferences().getLastTrainingId());
//			mJSONObjTraining.put("last_id", "59");
			stringBuffer.put("training", mJSONObjTraining);
			
			JSONObject mJSONObjFeedback = new JSONObject();
			mJSONObjFeedback.put("last_id", ApplicationLoader.getPreferences().getLastFeedbackId());
//			mJSONObjFeedback.put("last_id", "0");
			stringBuffer.put("feedback", mJSONObjFeedback);
			
			JSONObject mJSONObjAwards = new JSONObject();
			mJSONObjAwards.put("last_id", ApplicationLoader.getPreferences().getLastAwardsId());
//			mJSONObjAwards.put("last_id", "58");
			stringBuffer.put("awards", mJSONObjAwards);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostFailMessage() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("success", false);
			stringBuffer.put("message", "Please Try Again!");
			stringBuffer.put("error", "Please Try Again!");
			stringBuffer.put("messageError", "Please Try Again!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

}
