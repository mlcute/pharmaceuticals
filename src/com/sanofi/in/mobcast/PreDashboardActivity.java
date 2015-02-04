package com.sanofi.in.mobcast;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.AsyncHttpPost.OnPostExecuteListener;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class PreDashboardActivity extends Activity {

	EditText editName;
	EditText editEmail;
	
	Button buttonSubmit;
	Activity activity;
	String mobileNumber,name;
	String email;
	TextView buttonSkip;
	SharedPreferences prefs;
	Intent i;
	
	private static final String TAG = PreDashboardActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		mobileNumber = "";
		

		try {
			i = getIntent();
			mobileNumber = prefs.getString("mobileNumber", "0");
			if(mobileNumber.equalsIgnoreCase("0")){
				mobileNumber = ApplicationLoader.getPreferences().getMobileNumber();
			}
			Log.d("mobile", "mobile: "+mobileNumber);
			//mobileNumber = i.getExtras().getString(com.mobcast.util.Constants.user_id);
			Log.e("email",getIntent().getStringExtra("email"));
			
		} catch (Exception e) {

		}

		setContentView(R.layout.pre_dashboard);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}

		editName = (EditText) findViewById(R.id.editName);
		editEmail = (EditText)findViewById(R.id.editEmail);
		//editName.setText(i.getStringExtra("name"));
		
		//email = i.getStringExtra("email");
		//editEmail.setText(email);
		setData();
		name = editName.getText().toString().trim();
		email = editEmail.getText().toString().trim();
		buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
		buttonSkip = (TextView) findViewById(R.id.buttonSkip);

		buttonSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				validateAndSubmit();
			}
		});
		

		buttonSkip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startHomeActivity();
			}
		});
	}

	private void validateAndSubmit() {

		if (TextUtils.isEmpty(editEmail.getText().toString()) && TextUtils.isEmpty(editName.getText().toString())) {
			Toast.makeText(activity, "Invalid Fields", Toast.LENGTH_SHORT)
					.show();
		}else if(!isValidEmail(editEmail.getText().toString())){
			Toast.makeText(activity, "Enter Valid Email Id!", Toast.LENGTH_SHORT)
			.show();
		}
			else {
			HashMap<String, String> params = new HashMap<String, String>();
			
			Log.d("name", "hehe "+editName.getText().toString().trim());
			
			Log.d("mobilenumber", "hoho "+mobileNumber);
			params.put("name", editName.getText().toString().trim());
			//params.put(com.mobcast.util.Constants.user_id, editEmail.getText().toString().trim());
			params.put("mobileNumber", mobileNumber);
			params.put("emailAddress", editEmail.getText().toString());
			params.put("regID", ApplicationLoader.getPreferences().getRegId());

			AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
			final ProgressDialog pd = new ProgressDialog(activity);
			pd.setMessage("Please Wait");
			pd.setIndeterminate(true);
			pd.setCancelable(false);
			pd.setCanceledOnTouchOutside(false);
			pd.show();
			asyncHttpPost.execute(Constants.UPDATE_PRE_DASHBOARD);
			asyncHttpPost.setOnPostExecuteListener(new OnPostExecuteListener() {
				@Override
				public void onPostExecute(String result) {
					pd.dismiss();
					// if (result != null) {
					// TODO
					// if (result.equals("Update successful.")) {
					startHomeActivity();
					// } else {
					// Toast.makeText(activity, "Please Try Again!",
					// Toast.LENGTH_SHORT).show();
					// }
					// } else {
					// Toast.makeText(activity, "Please Try Again!",
					// Toast.LENGTH_SHORT).show();
					// }
				}
			});
		}
	}

	
	private boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	
	
	private void setData(){
		try{
			editName.setText(ApplicationLoader.getPreferences().getName());
			editEmail.setText(ApplicationLoader.getPreferences().getEmailAddress());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void startHomeActivity() {
		prefs.edit().putBoolean(activity.getString(R.string.isloggedin), true)
				.commit();
		Intent i = new Intent(activity, Home1.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(i); 
		//SessionManagement sm = new SessionManagement(PreDashboardActivity.this);
		//sm.checkLogin();
		finish();
	
	}

	@Override
	public void onBackPressed() {
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
}