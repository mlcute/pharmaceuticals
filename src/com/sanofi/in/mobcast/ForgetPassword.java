package com.sanofi.in.mobcast;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AsyncHttpPost.OnPostExecuteListener;

public class ForgetPassword extends Activity {

	private EditText mEmail;
	private Button mResetPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpassword);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		initUi();
		getIntentData();
		setListener();
		setPressedEffect();
	}

	private void initUi() {
		mEmail = (EditText) findViewById(R.id.email);
		mResetPassword = (Button) findViewById(R.id.resetPassword);
	}

	private void getIntentData(){
		if(!TextUtils.isEmpty(getIntent().getExtras().getString("emailAddress"))){
			mEmail.setText(getIntent().getExtras().getString("emailAddress"));
		}
	}
	
	private void setListener() {
		mResetPassword.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (validate()) {
					send();
				} else {
					Toast.makeText(ForgetPassword.this,
							"Please enter valid email", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}
	
	private void setPressedEffect(){
		mResetPassword.setOnTouchListener(myhandler2);
	}
	
	

	private boolean validate() {
		if (!TextUtils.isEmpty(mEmail.getText().toString())) {
			if (isValidEmail(mEmail.getText().toString().trim())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	void send() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("emailAddress", mEmail.getText().toString().trim());
		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
		asyncHttpPost.execute(Constants.RESET_PASSWORD);

		asyncHttpPost.setOnPostExecuteListener(new OnPostExecuteListener() {
			@Override
			public void onPostExecute(String result) {
				// TODO Auto-generated method stub
				try {
					JSONObject jsonObj = new JSONObject(result);
					if (jsonObj.getString("exists").equalsIgnoreCase("true")) {
						Toast.makeText(getApplicationContext(),
								jsonObj.getString("message"), Toast.LENGTH_LONG)
								.show();
						finish();
					} else {
						Toast.makeText(ForgetPassword.this,
								jsonObj.getString("message"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(0.5f);}
				v.getBackground().setAlpha(70);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
				v.getBackground().setAlpha(255);
			}

			return false;
		}

	};
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}
 
	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
		FlurryAgent.onEndSession(this);
	}
}
