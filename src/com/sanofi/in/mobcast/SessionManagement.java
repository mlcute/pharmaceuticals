package com.sanofi.in.mobcast;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.sanofi.in.mobcast.R;

public class SessionManagement {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "MobCastPref";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	// Constructor

	public SessionManagement(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * */
	public void createLoginSession(String name) {
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);

		// Storing name in pref
		editor.putString(KEY_NAME, name);

		// commit changes
		editor.commit();
	}

	/**
	 * Check login method wil check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public boolean checkSession() {
		return this.isLoggedIn();
	}

	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn()) {
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginV2.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		} else if (this.isLoggedIn()) {
			// if session is not set
			/*
			 * Intent openEmployeeCommunicationPoint = new Intent(
			 * "www.mobcast.in/ncp.EmployeeCommunication");// activity/action
			 * name // from manifest // file
			 * startActivity(openEmployeeCommunicationPoint);
			 */

			// ===============================ACTUAL
			// CODE=========================
			Intent i = new Intent(_context, Home1.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			ApplicationLoader.getPreferences().setLoggedIn(true);
			
			// Staring Login Activity
			_context.startActivity(i);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(_context);
			prefs.edit()
					.putBoolean(_context.getString(R.string.isloggedin), true)
					.commit();
			
			

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
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));

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
		Intent i = new Intent(_context, LoginV2.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		if (startActivity) {
			_context.startActivity(i);
		}
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);

	}
}