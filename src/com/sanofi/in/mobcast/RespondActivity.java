package com.sanofi.in.mobcast;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.sanofi.in.mobcast.R;

public class RespondActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.respond);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String category = pref.getString("category", "");
		
		if(category.equals("Announcements")){
			
		}
		else if(category.equals("News")){
			
		}
		else if(category.equals("Events")){
			
		}
		else if(category.equals("Training")){
	
		}
		else if(category.equals("Recruitment")){
			
		}
		else if(category.equals("Awards")){
			
		}
		else{
			
		}
		
		
		
	}

}
