package com.mobcast.util;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Log;

import com.sanofi.in.mobcast.ApplicationLoader;

public class RequestBuilder {

	private static final String TAG = RequestBuilder.class.getSimpleName();
	
	public static JSONObject getPostPullAlarmService() {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("regID", ApplicationLoader.getPreferences().getRegId());
			stringBuffer.put(Constants.user_id, ApplicationLoader.getPreferences().getEmailAddress());
			stringBuffer.put("deviceType", "android");
			stringBuffer.put("device", "android");
			
			JSONObject mJSONObjAnn = new JSONObject();
			mJSONObjAnn.put("last_id", ApplicationLoader.getPreferences().getLastAnnouncementId());
			stringBuffer.put("announcement", mJSONObjAnn);
			
			JSONObject mJSONObjEve = new JSONObject();
			mJSONObjEve.put("last_id", ApplicationLoader.getPreferences().getLastEventsId());
			stringBuffer.put("events", mJSONObjEve);
			
			JSONObject mJSONObjNews = new JSONObject();
			mJSONObjNews.put("last_id", ApplicationLoader.getPreferences().getLastNewsId());
			stringBuffer.put("news", mJSONObjNews);
			
			JSONObject mJSONObjTraining = new JSONObject();
			mJSONObjTraining.put("last_id", ApplicationLoader.getPreferences().getLastTrainingId());
			stringBuffer.put("training", mJSONObjTraining);
			
			JSONObject mJSONObjFeedback = new JSONObject();
			mJSONObjFeedback.put("last_id", ApplicationLoader.getPreferences().getLastFeedbackId());
			stringBuffer.put("feedback", mJSONObjFeedback);
			
			JSONObject mJSONObjAwards = new JSONObject();
			mJSONObjAwards.put("last_id", ApplicationLoader.getPreferences().getLastAwardsId());
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
	
	public static JSONObject getErrorMessageFromStatusCode(String mResponseCode, String mResponseMessage){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.API_KEY_PARAMETER.errorMessage,
					mResponseCode + " " + mResponseMessage);
			stringBuffer.put(Constants.API_KEY_PARAMETER.success, false);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getErrorMessageFromApi(String mErrorMessage){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.API_KEY_PARAMETER.errorMessage,mErrorMessage);
			stringBuffer.put(Constants.API_KEY_PARAMETER.success, false);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostMyPerfUserHierarchy(String mEmailAddress){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.user_id, mEmailAddress);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostMyPerfSfkpi(String mEmailAddress, String mQuarter, String mYear){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.user_id, mEmailAddress);
			stringBuffer.put("quarter", mQuarter);
			stringBuffer.put("year", mYear);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostMyPerfMyEarnings(String mEmailAddress, String mQuarter, String mYear){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.user_id, mEmailAddress);
			stringBuffer.put("quarter", mQuarter);
			stringBuffer.put("year", mYear);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	public static JSONObject getPostMyPerfConsistency(String mEmailAddress, String mYear){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.user_id, mEmailAddress);
			stringBuffer.put("year", mYear);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}
	
	
	public static JSONObject getPostMyPerfSalesPerformance(String mEmailAddress, String mYear, String mQuarter, String mMonth){
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put(Constants.user_id, mEmailAddress);
			stringBuffer.put("year", mYear);
			stringBuffer.put("quarter", mQuarter);
			stringBuffer.put("month", mMonth);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return stringBuffer;
	}

}
