/*HISTORY
 * CATEGORY			 :- BROADCAST RECEIVER
 * DEVELOPER			 :- VIKALP PATEL
 * AIM 				 :- WAKE UP SERVICES ON BOOT
 * DESCRIPTION 		 :- SETTING ALARM SERVICE ON BOOT UP - AS BOOTING FLUSHES ALARM MANAGER - AUTOSYNC
 * 
 * S - START E- END C- COMMENTED U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX		 DEVELOPER		 DATE		 FUNCTION			DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * A0001     VIKALP PATEL  03/01/2015     QUARTER         
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.mobcast.util;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.Home1;
import com.sanofi.in.mobcast.LoginV2;

public class AppPreferences {

	SharedPreferences sharedPreferences;
	Editor editor;

	public AppPreferences(Context context) {
		// TODO Auto-generated constructor stub
		sharedPreferences = context.getSharedPreferences("Cache",
				Context.MODE_PRIVATE);
	}

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	// Constructor

	/**
	 * Create login session
	 * */
	public void createLoginSession(String name) {
		// Storing login value as TRUE
		editor = sharedPreferences.edit();
		editor.putBoolean(IS_LOGIN, true);
		// Storing name in pref
		editor.putString(KEY_NAME, name);
		// commit changes
		editor.commit();
	}

	public void setLoggedIn(boolean isLoggedIn) {
		editor = sharedPreferences.edit();
		editor.putBoolean(IS_LOGIN, isLoggedIn);
		editor.commit();
	}

	public Context getContext() {
		return ApplicationLoader.getApplication().getApplicationContext();
	}

	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public boolean checkSession() {
		return isLoggedIn();
	}

	public void checkLogin() {
		// Check login status
		if (!isLoggedIn()) {
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(getContext(), LoginV2.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			getContext().startActivity(i);
		} else if (isLoggedIn()) {
			// if session is not set
			/*
			 * Intent openEmployeeCommunicationPoint = new Intent(
			 * "www.mobcast.in/ncp.EmployeeCommunication");// activity/action
			 * name // from manifest // file
			 * startActivity(openEmployeeCommunicationPoint);
			 */

			// ===============================ACTUAL
			// CODE=========================
			Intent i = new Intent(getContext(), Home1.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			getContext().startActivity(i);
			setLoggedIn(true);
			// ===============================ACTUAL
			// CODE=========================

			/*
			 * //TEST CODE Intent i = new Intent(_context, Home1.class);
			 * _context.startActivity(i);
			 */

		}

	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, sharedPreferences.getString(KEY_NAME, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser(boolean startActivity) {

		// Clearing all data from Shared Preferences
		editor.clear();

		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, false);

		// Storing name in pref
		editor.putString(KEY_NAME, null);

		// commit changes
		editor.commit();

		// editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(getContext(), LoginV2.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		if (startActivity) {
			getContext().startActivity(i);
		}
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return sharedPreferences.getBoolean(IS_LOGIN, false);

	}

	/*
	 * PREFERENCES : API DETAILS
	 */

	public void setMobileNumber(String str) {
		editor = sharedPreferences.edit();
		editor.putString("mobilenumerid", str);
		editor.commit();
	}

	public String getMobileNumber() {
		String flag = sharedPreferences.getString("mobilenumerid", null);
		return flag;
	}

	public void setName(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_name", str);
		editor.commit();
	}

	public String getName() {
		String flag = sharedPreferences.getString("_name", null);
		return flag;
	}

	public void setEmailAddress(String str) {
		editor = sharedPreferences.edit();
		editor.putString("_emailAddress", str);
		editor.commit();
	}

	public String getEmailAddress() {
		String flag = sharedPreferences.getString("_emailAddress", null);
		return flag;
	}

	public void setApiKey(String str) {
		editor = sharedPreferences.edit();
		editor.putString("API_KEY", str);
		editor.commit();
	}

	public String getApiKey() {
		String flag = sharedPreferences.getString("API_KEY", null);
		return flag;
	}

	public void setRegId(String str) {
		editor = sharedPreferences.edit();
		editor.putString("regId", str);
		editor.commit();
	}

	public String getRegId() {
		String flag = sharedPreferences.getString("regId", null);
		return flag;
	}

	/*
	 * PUSH NOTIFICATION : GCM
	 */
	public void setRegisteredGCMToServer(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("setRegisteredGCMToServer", value);
		editor.commit();
	}

	public boolean getRegisteredGCMToServer() {
		return sharedPreferences.getBoolean("setRegisteredGCMToServer", false);
	}

	/*
	 * SAVE COACH MARKS
	 */

	public void setCoachMarksFirstTime(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isCoachMarksFirstTime", value);
		editor.commit();
	}

	public boolean isCoachMarksFirstTime() {
		return sharedPreferences.getBoolean("isCoachMarksFirstTime", false);
	}

	/*
	 * FETCH WHETHER USER BELONGS TO SALES OR NOT
	 */

	public void setIncenModuleLayoutEnable(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isIncenModuleLayoutEnable", value);
		editor.commit();
	}

	public boolean isIncenModuleLayoutEnable() {
		return sharedPreferences.getBoolean("isIncenModuleLayoutEnable", false);
	}

	public void setIncenModuleEnable(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isIncenModuleEnable", value);
		editor.commit();
	}

	public boolean isIncenModuleEnable() {
		return sharedPreferences.getBoolean("isIncenModuleEnable", false);
	}

	/*
	 * FETCH DATA FROM API : PRODUCT DATA
	 */

	public void setIncenFirstTime(boolean value) {
		editor = sharedPreferences.edit();
		editor.putBoolean("isIncenFirstTime", value);
		editor.commit();
	}

	public boolean isIncenFirstTime() {
		return sharedPreferences.getBoolean("isIncenFirstTime", false);
	}
	
	public void setIncenPdfPath(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("incenPdfPath", str);
		editor.commit();
	}

	public String getIncenPdfPath() { // JAN-FEB-MAR
		return sharedPreferences.getString("incenPdfPath", null);
	}

	/*
	 * SAVE MONTHLY
	 */

	public void setQuarter1(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("month1", str[0]);
		editor.putString("month2", str[1]);
		editor.putString("month3", str[2]);
		editor.commit();
	}

	public String[] getQuarter1() { // JAN-FEB-MAR
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("month1", null);
		mStr[1] = sharedPreferences.getString("month2", null);
		mStr[2] = sharedPreferences.getString("month3", null);
		return mStr;
	}

	public void setQuarterValue1(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("monthValue1", str[0]);
		editor.putString("monthValue2", str[1]);
		editor.putString("monthValue3", str[2]);
		editor.commit();
	}

	public String[] getQuarterValue1() { // JAN-FEB-MAR
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthValue1", null);
		mStr[1] = sharedPreferences.getString("monthValue2", null);
		mStr[2] = sharedPreferences.getString("monthValue3", null);
		return mStr;
	}

	public void setQuarterCheck1(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("monthCheck1", str[0]);
		editor.putString("monthCheck2", str[1]);
		editor.putString("monthCheck3", str[2]);
		editor.commit();
	}

	public String[] getQuarterCheck1() { // JAN-FEB-MAR
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthCheck1", null);
		mStr[1] = sharedPreferences.getString("monthCheck2", null);
		mStr[2] = sharedPreferences.getString("monthCheck3", null);
		return mStr;
	}

	public void setQuarterTotal1(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("quarterTotal1", str);
		editor.commit();
	}

	public String getQuarterTotal1() { // JAN-FEB-MAR
		return sharedPreferences.getString("quarterTotal1", null);
	}

	public void setQuarter2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("month4", str[0]);
		editor.putString("month5", str[1]);
		editor.putString("month6", str[2]);
		editor.commit();
	}

	public String[] getQuarter2() {// APR-MAY-JUN
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("month4", null);
		mStr[1] = sharedPreferences.getString("month5", null);
		mStr[2] = sharedPreferences.getString("month6", null);
		return mStr;
	}

	public void setQuarterValue2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("monthValue4", str[0]);
		editor.putString("monthValue5", str[1]);
		editor.putString("monthValue6", str[2]);
		editor.commit();
	}

	public String[] getQuarterValue2() { // APR-MAY-JUN
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthValue4", null);
		mStr[1] = sharedPreferences.getString("monthValue5", null);
		mStr[2] = sharedPreferences.getString("monthValue6", null);
		return mStr;
	}

	public void setQuarterCheck2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("monthCheck4", str[0]);
		editor.putString("monthCheck5", str[1]);
		editor.putString("monthCheck6", str[2]);
		editor.commit();
	}

	public String[] getQuarterCheck2() { // APR-MAY-JUN
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthCheck4", null);
		mStr[1] = sharedPreferences.getString("monthCheck5", null);
		mStr[2] = sharedPreferences.getString("monthCheck6", null);
		return mStr;
	}

	public void setQuarterTotal2(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("quarterTotal2", str);
		editor.commit();
	}

	public String getQuarterTotal2() { // JAN-FEB-MAR
		return sharedPreferences.getString("quarterTotal2", null);
	}

	public void setQuarter3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("month7", str[0]);
		editor.putString("month8", str[1]);
		editor.putString("month9", str[2]);
		editor.commit();
	}

	public String[] getQuarter3() {// JUL-AUG-SEP
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("month7", null);
		mStr[1] = sharedPreferences.getString("month8", null);
		mStr[2] = sharedPreferences.getString("month9", null);
		return mStr;
	}

	public void setQuarterValue3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("monthValue7", str[0]);
		editor.putString("monthValue8", str[1]);
		editor.putString("monthValue9", str[2]);
		editor.commit();
	}

	public String[] getQuarterValue3() { // JUL-AUG-SEP
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthValue7", null);
		mStr[1] = sharedPreferences.getString("monthValue8", null);
		mStr[2] = sharedPreferences.getString("monthValue9", null);
		return mStr;
	}

	public void setQuarterCheck3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("monthCheck7", str[0]);
		editor.putString("monthCheck8", str[1]);
		editor.putString("monthCheck9", str[2]);
		editor.commit();
	}

	public String[] getQuarterCheck3() { // JUL-AUG-SEP
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthCheck7", null);
		mStr[1] = sharedPreferences.getString("monthCheck8", null);
		mStr[2] = sharedPreferences.getString("monthCheck9", null);
		return mStr;
	}

	public void setQuarterTotal3(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("quarterTotal3", str);
		editor.commit();
	}

	public String getQuarterTotal3() { // JAN-FEB-MAR
		return sharedPreferences.getString("quarterTotal3", null);
	}

	public void setQuarter4(String[] str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("month10", str[0]);
		editor.putString("month11", str[1]);
		editor.putString("month12", str[2]);
		editor.commit();
	}

	public String[] getQuarter4() { // OCT-NOV-DEC
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("month10", null);
		mStr[1] = sharedPreferences.getString("month11", null);
		mStr[2] = sharedPreferences.getString("month12", null);
		return mStr;
	}

	public void setQuarterValue4(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("monthValue10", str[0]);
		editor.putString("monthValue11", str[1]);
		editor.putString("monthValue12", str[2]);
		editor.commit();
	}

	public String[] getQuarterValue4() { // JAN-FEB-MAR
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthValue10", null);
		mStr[1] = sharedPreferences.getString("monthValue11", null);
		mStr[2] = sharedPreferences.getString("monthValue12", null);
		return mStr;
	}

	public void setQuarterCheck4(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("monthCheck10", str[0]);
		editor.putString("monthCheck11", str[1]);
		editor.putString("monthCheck12", str[2]);
		editor.commit();
	}

	public String[] getQuarterCheck4() { // JAN-FEB-MAR
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("monthCheck10", null);
		mStr[1] = sharedPreferences.getString("monthCheck11", null);
		mStr[2] = sharedPreferences.getString("monthCheck12", null);
		return mStr;
	}

	public void setQuarterTotal4(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("quarterTotal4", str);
		editor.commit();
	}

	public String getQuarterTotal4() { // JAN-FEB-MAR
		return sharedPreferences.getString("quarterTotal4", null);
	}

	/*
	 * SAVE PRODUCT JSON
	 */
	public void setProductJSON(String str) {// Product JSON
		editor = sharedPreferences.edit();
		editor.putString("productJSON", str);
		editor.commit();
	}

	public String getProductJSON() { // Product JSON
		return sharedPreferences.getString("productJSON", null);
	}

	/*
	 * SAVE MONTH & QUARTER JSON
	 */
	public void setMonthQuarterJSON(String str) {// Month & Quarter JSON
		editor = sharedPreferences.edit();
		editor.putString("monthQuarterJSON", str);
		editor.commit();
	}

	public String getMonthQuarterJSON() { // Month & Quarter JSON
		return sharedPreferences.getString("monthQuarterJSON", null);
	}

	/*
	 * SAVE PRODUCT VALUE - QUARTER WISE
	 */

	public void setProductNumber(String str) {
		editor = sharedPreferences.edit();
		editor.putString("productTotalNumber", str);
		editor.commit();
	}

	public String getProductNumber() { // JAN-FEB-MAR
		return sharedPreferences.getString("productTotalNumber", null);
	}

	public void setProduct1(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("product1" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProduct1(int noOfSize) { // JAN-FEB-MAR
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("product1" + i, null);
		}
		return mStr;
	}

	public void setProductValue1(String[] str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("productValue1" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProductValue1(int noOfSize) { // JAN-FEB-MAR
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("productValue1" + i, null);
		}
		return mStr;
	}

	public void setProduct2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("product2" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProduct2(int noOfSize) { // APRIL- MAY - JUNE
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("product2" + i, null);
		}
		return mStr;
	}

	public void setProductValue2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("productValue2" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProductValue2(int noOfSize) { // APRIL- MAY - JUNE
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("productValue2" + i, null);
		}
		return mStr;
	}

	public void setProduct3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("product3" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProduct3(int noOfSize) { // JUL-AUG-SEP
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("product3" + i, null);
		}
		return mStr;
	}

	public void setProductValue3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("productValue3" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProductValue3(int noOfSize) { // JUL-AUG-SEP
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("productValue3" + i, null);
		}
		return mStr;
	}

	public void setProduct4(String[] str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("product4" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProduct4(int noOfSize) { // OCT-NOV-DEC
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("product4" + i, null);
		}
		return mStr;
	}

	public void setProductValue4(String[] str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("productValue4" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getProductValue4(int noOfSize) { // OCT-NOV-DEC
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("productValue4" + i, null);
		}
		return mStr;
	}

	public void setProductTotalValue1(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("productTotalValue1", str);
		editor.commit();
	}

	public String getProductTotalValue1() { // JAN-FEB-MAR
		return sharedPreferences.getString("productTotalValue1", null);
	}

	public void setProductTotalValue2(String str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("productTotalValue2", str);
		editor.commit();
	}

	public String getProductTotalValue2() { // APR-MAY-JUN
		return sharedPreferences.getString("productTotalValue2", null);
	}

	public void setProductTotalValue3(String str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("productTotalValue3", str);
		editor.commit();
	}

	public String getProductTotalValue3() { // JUL-AUG-SEP
		return sharedPreferences.getString("productTotalValue3", null);
	}

	public void setProductTotalValue4(String str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("productTotalValue4", str);
		editor.commit();
	}

	public String getProductTotalValue4() { // OCT-NOV-DEC
		return sharedPreferences.getString("productTotalValue4", null);
	}

	/*
	 * QUARTER WISE SAVING VALUE
	 */

	public void setQValue1(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("qValue1", str);
		editor.commit();
	}

	public String getQValue1() { // JAN-FEB-MAR
		return sharedPreferences.getString("qValue1", null);
	}

	public String getQ1() { // JAN-FEB-MAR
		return sharedPreferences.getString("q1", null);
	}

	public void setQ1(String str) {// JAN-FEB-MAR
		editor = sharedPreferences.edit();
		editor.putString("q1", str);
		editor.commit();
	}

	public void setQValue2(String str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("qValue2", str);
		editor.commit();
	}

	public String getQValue2() { // APR-MAY-JUN
		return sharedPreferences.getString("qValue2", null);
	}

	public void setQ2(String str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("q2", str);
		editor.commit();
	}

	public String getQ2() { // APR-MAY-JUN
		return sharedPreferences.getString("q2", null);
	}

	public void setQValue3(String str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("qValue3", str);
		editor.commit();
	}

	public String getQValue3() { // JUL-AUG-SEP
		return sharedPreferences.getString("qValue3", null);
	}

	public void setQ3(String str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("q3", str);
		editor.commit();
	}

	public String getQ3() { // JUL-AUG-SEP
		return sharedPreferences.getString("q3", null);
	}

	public void setQValue4(String str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("qValue4", str);
		editor.commit();
	}

	public String getQValue4() { // OCT-NOV-DEC
		return sharedPreferences.getString("qValue4", null);
	}

	public void setQ4(String str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("q4", str);
		editor.commit();
	}

	public String getQ4() { // OCT-NOV-DEC
		return sharedPreferences.getString("q4", null);
	}

	/*
	 * KPI- QUARTER WISE SAVING
	 */

	public void setKPI1(String[] str) {// JAN-FEB-MAR : CPA
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("KPI1" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getKPI1(int noOfSize) { // JAN-FEB-MAR : CPA
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("KPI1" + i, null);
		}
		return mStr;
	}

	public void setKPIQ1(String[] str) {// JAN-FEB-MAR : COVERAGE
		editor = sharedPreferences.edit();
		editor.putString("KPIQ1", str[0]);
		editor.putString("KPIQ2", str[1]);
		editor.putString("KPIQ3", str[2]);
		editor.commit();
	}

	public String[] getKPIQ1() { // JAN-FEB-MAR : COVERAGE
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("KPIQ1", null);
		mStr[1] = sharedPreferences.getString("KPIQ2", null);
		mStr[2] = sharedPreferences.getString("KPIQ3", null);
		return mStr;
	}

	public void setKPIValue1(String str) {// JAN-FEB-MAR :CPA AVERAGE : 60 %
		editor = sharedPreferences.edit();
		editor.putString("KPIValue1", str);
		editor.commit();
	}

	public String getKPIValue1() { // JAN-FEB-MAR : CPA AVERAGE : 60 %
		return sharedPreferences.getString("KPIQValue1", null);
	}

	public void setKPIQValue1(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPIQValue1", str);
		editor.commit();
	}

	public String getKPIQValue1() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPIQValue1", null);
	}

	public void setKPITotalValue1(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPITotalValue1", str);
		editor.commit();
	}

	public String getKPITotalValue1() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPITotalValue1", null);
	}

	public void setKPI2(String[] str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("KPI2" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getKPI2(int noOfSize) { // APR-MAY-JUN
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("KPI2" + i, null);
		}
		return mStr;
	}

	public void setKPIQ2(String[] str) {// APR_MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("KPIQ4", str[0]);
		editor.putString("KPIQ5", str[1]);
		editor.putString("KPIQ6", str[2]);
		editor.commit();
	}

	public String[] getKPIQ2() { // APR-MAY-JUN
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("KPIQ4", null);
		mStr[1] = sharedPreferences.getString("KPIQ5", null);
		mStr[2] = sharedPreferences.getString("KPIQ6", null);
		return mStr;
	}

	public void setKPIValue2(String str) {// APR-MAY-JUN
		editor = sharedPreferences.edit();
		editor.putString("KPIValue2", str);
		editor.commit();
	}

	public String getKPIValue2() { // APR-MAY-JUN
		return sharedPreferences.getString("KPIValue2", null);
	}

	public void setKPIQValue2(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPIQValue2", str);
		editor.commit();
	}

	public String getKPIQValue2() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPIQValue2", null);
	}

	public void setKPITotalValue2(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPITotalValue2", str);
		editor.commit();
	}

	public String getKPITotalValue2() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPITotalValue2", null);
	}

	public void setKPI3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("KPI3" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getKPI3(int noOfSize) { // JUL-AUG-SEP
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("KPI3" + i, null);
		}
		return mStr;
	}

	public void setKPIQ3(String[] str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("KPIQ7", str[0]);
		editor.putString("KPIQ8", str[1]);
		editor.putString("KPIQ9", str[2]);
		editor.commit();
	}

	public String[] getKPIQ3() { // JUL-AUG-SEP
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("KPIQ7", null);
		mStr[1] = sharedPreferences.getString("KPIQ8", null);
		mStr[2] = sharedPreferences.getString("KPIQ9", null);
		return mStr;
	}

	public void setKPIValue3(String str) {// JUL-AUG-SEP
		editor = sharedPreferences.edit();
		editor.putString("KPIValue3", str);
		editor.commit();
	}

	public String getKPIValue3() { // JUL-AUG-SEP
		return sharedPreferences.getString("KPIValue3", null);
	}

	public void setKPIQValue3(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPIQValue3", str);
		editor.commit();
	}

	public String getKPIQValue3() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPIQValue3", null);
	}

	public void setKPITotalValue3(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPITotalValue3", str);
		editor.commit();
	}

	public String getKPITotalValue3() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPITotalValue3", null);
	}

	public void setKPI4(String[] str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		for (int i = 0; i < str.length; i++) {
			editor.putString("KPI4" + i, str[i]);
		}
		editor.commit();
	}

	public String[] getKPI4(int noOfSize) { // OCT-NOV-DEC
		String[] mStr = new String[noOfSize];
		for (int i = 0; i < noOfSize; i++) {
			mStr[i] = sharedPreferences.getString("KPI4" + i, null);
		}
		return mStr;
	}

	public void setKPIQ4(String[] str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("KPIQ10", str[0]);
		editor.putString("KPIQ11", str[1]);
		editor.putString("KPIQ12", str[2]);
		editor.commit();
	}

	public String[] getKPIQ4() { // OCT-NOV-DEC
		String[] mStr = new String[3];
		mStr[0] = sharedPreferences.getString("KPIQ10", null);
		mStr[1] = sharedPreferences.getString("KPIQ11", null);
		mStr[2] = sharedPreferences.getString("KPIQ12", null);
		return mStr;
	}

	public void setKPIValue4(String str) {// OCT-NOV-DEC
		editor = sharedPreferences.edit();
		editor.putString("KPIValue4", str);
		editor.commit();
	}

	public String getKPIValue4() { // OCT-NOV-DEC
		return sharedPreferences.getString("KPIValue4", null);
	}

	public void setKPIQValue4(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPIQValue4", str);
		editor.commit();
	}

	public String getKPIQValue4() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPIQValue4", null);
	}

	public void setKPITotalValue4(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("KPITotalValue4", str);
		editor.commit();
	}

	public String getKPITotalValue4() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("KPITotalValue4", null);
	}

	public void setSummaryValue1(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("summaryValue1", str);
		editor.commit();
	}

	public String getSummarValue1() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("summaryValue1", null);
	}

	public void setSummaryValue2(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("summaryValue2", str);
		editor.commit();
	}

	public String getSummarValue2() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("summaryValue2", null);
	}

	public void setSummaryValue3(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("summaryValue3", str);
		editor.commit();
	}

	public String getSummarValue3() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("summaryValue3", null);
	}

	public void setSummaryValue4(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("summaryValue4", str);
		editor.commit();
	}

	public String getSummarValue4() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("summaryValue4", null);
	}

	/*
	 * ANNUAL
	 */
	public void setAnnualQ1(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQ1", str);
		editor.commit();
	}

	public String getAnnualQ1() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQ1", null);
	}

	public void setAnnualQCheck1(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQCheck1", str);
		editor.commit();
	}

	public String getAnnualQCheck1() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQCheck1", null);
	}

	public void setAnnualQ2(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQ2", str);
		editor.commit();
	}

	public String getAnnualQ2() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQ2", null);
	}

	public void setAnnualQCheck2(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQCheck2", str);
		editor.commit();
	}

	public String getAnnualQCheck2() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQCheck2", null);
	}

	public void setAnnualQ3(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQ3", str);
		editor.commit();
	}

	public String getAnnualQ3() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQ3", null);
	}

	public void setAnnualQCheck3(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQCheck3", str);
		editor.commit();
	}

	public String getAnnualQCheck3() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQCheck3", null);
	}

	public void setAnnualQ4(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQ4", str);
		editor.commit();
	}

	public String getAnnualQ4() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQ4", null);
	}

	public void setAnnualQCheck4(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualQCheck4", str);
		editor.commit();
	}

	public String getAnnualQCheck4() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQCheck4", null);
	}

	public void setAnnualCheck(String str) {// JAN-FEB-MAR : COVERAGE : 40 %
		editor = sharedPreferences.edit();
		editor.putString("annualCheck", str);
		editor.commit();
	}

	public String getAnnualCheck() { // JAN-FEB-MAR : COVERAGE : 40 %
		return sharedPreferences.getString("annualQCheck", null);
	}
}