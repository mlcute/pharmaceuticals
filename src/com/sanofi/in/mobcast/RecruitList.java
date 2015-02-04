package com.sanofi.in.mobcast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.regex.*;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class RecruitList extends Activity {
	Intent i;
	static final String RECRUIT_DATA = "recruitData",
			KEY_JOB = "job",
			KEY_DESIGNATION = "jobDesignation",
			KEY_ID = "jobID",
			KEY_JOBDATE="jobDate",
			KEY_TITLE = "jobTitle",
			KEY_LOC = "jobLocation",
			KEY_MINEXP = "jobExperience",
			KEY_SKILL = "jobSkills",
			KEY_JOBDESC = "jobDescription", 
			KEY_QUAL = "jobQualification",
			KEY_CTC = "jobCTC", 
			KEY_CONTACTNAME = "contactName",
			KEY_CONTACTEMAIL = "contactEmail",
			KEY_CONTACTNUMBER = "contactPhone",
			KEY_CONTACTDESIGNATION = "contactDesignation",
			KEY_JOBDETAIL = "jobDetail" ,
			KEY_CONTENT_EXPIRAY = "contentExpiry",
			KEY_SHARECONTACT_ALLOWED = "shareContact_allowed",
			KEY_SHARING_ALLOWED ="sharing_allowed";
			

	ListView list;
	JSONArray details;
	ArrayList<RecruitItem> ral;
	ImageButton sample_refresh;

	RecruitAdapter ra;
	AsyncHttpPost asyncHttpPost;
	TextView noNew;
	ArrayList<String> al;

	String recruitData;
	int list_length;

	ArrayList<String> jobID = new ArrayList<String>();
	ArrayList<String> jobTitle = new ArrayList<String>();
	ArrayList<String> jobDate = new ArrayList<String>();
	ArrayList<String> jobDesignation = new ArrayList<String>();
	ArrayList<String> jobLocation = new ArrayList<String>();
	ArrayList<String> jobExperience = new ArrayList<String>();
	ArrayList<String> jobSkills = new ArrayList<String>();
	ArrayList<String> jobDescription = new ArrayList<String>();
	ArrayList<String> jobQualification = new ArrayList<String>();
	ArrayList<String> jobCTC = new ArrayList<String>();
	ArrayList<String> contactName = new ArrayList<String>();
	ArrayList<String> contactDesignation = new ArrayList<String>();
	ArrayList<String> contactEmail = new ArrayList<String>();
	ArrayList<String> contactNumber = new ArrayList<String>();
	ArrayList<String> contentExpiry = new ArrayList<String>();
	ArrayList<String> sharedContact_Allowed = new ArrayList<String>();
	ArrayList<String> sharing_allowed = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recruitlist);
		// EmployeeCommunication.pDialog.getCurrentFocus();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll(); 
		Log.d("RecruitList","Inside");

		ral = new ArrayList<RecruitItem>();
		
		Intent intent= getIntent(); if(!intent.equals(null))
			recruitData=intent.getStringExtra("recruitData");
		
		noNew = (TextView) findViewById(R.id.noNew);

		if (!(recruitData == null || (recruitData.equals(null)))) {
			if(recruitData.length() > 2){
				populateData();
				noNew.setVisibility(View.GONE);
			}
		} else {
			Log.d("recruitData", "Null");
			noNew.setVisibility(View.VISIBLE);
		}
		
		list = (ListView) findViewById(R.id.recruitList);
		// al=new ArrayList<String>();
//		SA ADDED RECRUITLIST
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		sample_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RotateAnimation ranim = (RotateAnimation) AnimationUtils
						.loadAnimation(getApplicationContext(),
								R.anim.rotateanim);
				sample_refresh.startAnimation(ranim);
			}
		});
//		EA ADDED RECRUITLIST
		ra = new RecruitAdapter(this, R.layout.recruitlist_tem, ral);
		// aa=new ArrayAdapter<String>(RecruitList.this,
		// R.layout.recruitlist_tem,al);
		list.setAdapter(ra);
		Log.d("List Length","");
		
		// asyncHttpPost.onPostExecute(recruitData);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
				// String contact=contactDesignation.get(pos)+"  "
				// +contactName.get(pos)+"  "
				// +contactEmail.get(pos)+"  "
				// +contactNumber.get(pos);
				// Toast.makeText(getApplicationContext(), ,
				// Toast.LENGTH_LONG).show();
				// Toast.makeText(getApplicationContext(), jobID.get(pos),
				// Toast.LENGTH_LONG).show();
				// Toast.makeText(getApplicationContext(), jobSkills.get(pos),
				// Toast.LENGTH_LONG).show();
				pos = list_length - pos - 1;
				i = new Intent(RecruitList.this, Recruitment.class);

				i.putExtra(KEY_ID, jobID.get(pos));
				i.putExtra(KEY_TITLE, jobTitle.get(pos));
				i.putExtra(KEY_JOBDATE, jobDate.get(pos));
				i.putExtra(KEY_DESIGNATION, jobDesignation.get(pos));
				i.putExtra(KEY_LOC, jobLocation.get(pos));
				i.putExtra(KEY_MINEXP, jobExperience.get(pos));
				i.putExtra(KEY_SKILL, jobSkills.get(pos));
				i.putExtra(KEY_JOBDESC, jobDescription.get(pos));
				i.putExtra(KEY_QUAL, jobQualification.get(pos));
				i.putExtra(KEY_CTC, jobCTC.get(pos));
				i.putExtra(KEY_CONTACTNAME, contactName.get(pos));
				i.putExtra(KEY_CONTACTDESIGNATION, contactDesignation.get(pos));
				i.putExtra(KEY_CONTACTEMAIL, contactEmail.get(pos));
				i.putExtra(KEY_CONTACTNUMBER, contactNumber.get(pos));
				i.putExtra(KEY_SHARECONTACT_ALLOWED, sharedContact_Allowed.get(pos));
				i.putExtra(KEY_SHARING_ALLOWED, sharing_allowed.get(pos));
				i.putExtra(KEY_CONTENT_EXPIRAY, contentExpiry.get(pos));								
				startActivity(i);
				
			}// end of onItemClick
		});// end of setOnItemClickListener

	}// onCreate ends here
	void populateData()
	{
		if(recruitData!= null)
		{
			Log.d("RecruitData",recruitData);
			try{
				details=new JSONArray(recruitData);
				for(int i=0;i<details.length();i++)
				{
					JSONObject d=details.getJSONObject(i);
					Log.d("J"+i,""+d);
					jobID.add(i, d.getString(KEY_ID));
					jobTitle.add(i, d.getString(KEY_TITLE));
					jobDate.add(i,d.getString(KEY_JOBDATE));
					jobDesignation.add(i, d.getString(KEY_DESIGNATION));
					jobLocation.add(i, d.getString(KEY_LOC));
					jobExperience.add(i, d.getString(KEY_MINEXP));
					jobSkills.add(i, d.getString(KEY_SKILL));
					jobDescription.add(i, d.getString(KEY_JOBDESC));
					jobQualification.add(i, d.getString(KEY_QUAL));
					jobCTC.add(i, d.getString(KEY_CTC));
					contactName.add(i, d.getString(KEY_CONTACTNAME));
					contactDesignation.add(i, d.getString(KEY_CONTACTDESIGNATION));
					contactEmail.add(i, d.getString(KEY_CONTACTEMAIL));
					contactNumber.add(i, d.getString(KEY_CONTACTNUMBER));
					sharedContact_Allowed.add(i, d.getString(KEY_SHARECONTACT_ALLOWED));
					sharing_allowed.add(i,d.getString(KEY_SHARING_ALLOWED));
					contentExpiry.add(i, KEY_CONTENT_EXPIRAY);
					ral.add(i, new RecruitItem(d.getString(KEY_JOBDATE),d.getString(KEY_TITLE),d.getString(KEY_JOBDESC)));
				}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
//			Collections.reverse(ral);
			list_length = ral.size();
		}
	}
	
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

}// end of RecruitList
