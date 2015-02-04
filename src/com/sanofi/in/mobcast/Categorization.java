package com.sanofi.in.mobcast;

import android.app.Activity;
import android.os.Bundle;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.Constants;

public class Categorization extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.recruit);
		/*
		 * actionbar = getSupportActionBar();
		 * 
		 * actionbar.addTab(actionbar.newTab().setText("Unreads").setTabListener(
		 * this));
		 * actionbar.addTab(actionbar.newTab().setText("Starred").setTabListener
		 * (this));
		 * actionbar.addTab(actionbar.newTab().setText("All Items").setTabListener
		 * (this));
		 * 
		 * showTabsNav();
		 */
	}

	/*
	 * private void showTabsNav() { ActionBar ab = getSupportActionBar(); if
	 * (ab.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS) {
	 * ab.setDisplayShowTitleEnabled(false);
	 * ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS); } }
	 * 
	 * 
	 * @Override public void onTabSelected(Tab tab, FragmentTransaction ft) { //
	 * TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {
	 * // TODO Auto-generated method stub
	 * 
	 * }
	 */
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
