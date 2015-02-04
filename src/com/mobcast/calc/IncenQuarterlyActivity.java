package com.mobcast.calc;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.mobcast.view.AccordionView;
import com.mobcast.view.Slider;
import com.mobcast.view.Slider.OnValueChangedListener;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class IncenQuarterlyActivity extends FragmentActivity {

	private AccordionView mAccordionMon1;

	private TextView mTotalRsSy;
	private TextView mTotalQuartelLabel;
	private TextView mWhichQuarterLabel;

	private EditText mQuarterPer;
	private Slider mQuarterSlider;
	private TextView mQuarterTotal;

	private Typeface mTypeFace;
	private int whichQuarter;

	private Toast mToast;

	private String mQuarterSliderArr;
	private String mQuarterPerArr;
	private String mQuarterTotalArr;

	private int INCENQUARTERMAP[];

	private static final String TAG = IncenQuarterlyActivity.class
			.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_quarter);
		setSecurity();
		initUi();
		getIntentData();
		setTextAccordingToQuarter();
		setRupeeFont();
		restoreValueFromPreferences();
		getBaseJSON();
		setListener();
	}

	private void initUi() {
		mAccordionMon1 = (AccordionView) findViewById(R.id.incen_quarter_acc_view);
		mAccordionMon1.toggleSection(0);

		mTotalQuartelLabel = (TextView) findViewById(R.id.incen_quarter_total_tv);
		mWhichQuarterLabel = (TextView) findViewById(R.id.incen_quarter_text);

		mTotalRsSy = (TextView) findViewById(R.id.incen_quarter_rs_sy);

		mQuarterPer = (EditText) findViewById(R.id.incen_quarter_slider_per);
		mQuarterSlider = (Slider) findViewById(R.id.activity_quarter_slider);
		mQuarterTotal = (TextView) findViewById(R.id.incen_quarter_total_incen);
	}

	private void getIntentData() {
		whichQuarter = Integer.parseInt(getIntent().getStringExtra(
				IncenDashBoardActivity.INTENT_QUARTER));
	}

	private void setListener() {

		mQuarterSlider.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					if(value < 90){
						mQuarterPer.setText("0");	
					}else{
						mQuarterPer.setText(String.valueOf(value));
					}
					
					businessIncenLogic(false);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mQuarterPer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable mString) {
				// TODO Auto-generated method stub
				validateEditBox(mString.toString(), 1);
			}
		});

	}

	public void validateEditBox(String mString, int whichSlider) {
		try {
			if (!TextUtils.isEmpty(mString.toString())) {
				if (mString.toString().length() >= 2) {
					if (Integer.parseInt(mString.toString()) >= 90
							&& Integer.parseInt(mString.toString()) <= 110) {
						mQuarterSlider.setValue(Integer.parseInt(mString));
						businessIncenLogic(false);
					} else {
						// if (mToast == null
						// || mToast.getView().getWindowVisibility() !=
						// View.VISIBLE) {
						// mToast.makeText(IncenMonthlyActivity.this,
						// "Please enter value between 90 & 110",
						// Toast.LENGTH_SHORT).show();
						// }
						if (Integer.parseInt(mString) < 90
						/* && Integer.parseInt(mString) > 20 */) {
							mQuarterSlider.setValue(89);
							// mQuarterPer.setText("90");
							// mQuarterPer.setText("0");
							businessIncenLogic(true);
						} else if (Integer.parseInt(mString) > 110) {
							mQuarterSlider.setValue(110);
							// mQuarterPer.setText("110");
							businessIncenLogic(false);
						} /*
						 * else if (Integer.parseInt(mString) > 9 &&
						 * Integer.parseInt(mString) <= 20) {
						 * mQuarterSlider.setValue(89);
						 * businessIncenLogic(true); }
						 */
					}
				} else if (mString.toString().length() == 1) {
					if (Integer.parseInt(mString) == 0) {
						mQuarterSlider.setValue(89);
						businessIncenLogic(true);
					} else if (Integer.parseInt(mString) > 0
							&& Integer.parseInt(mString) < 10) {
						mQuarterSlider.setValue(89);
						businessIncenLogic(true);
					}
				}
			} else {
				// if (mToast == null
				// || mToast.getView().getWindowVisibility() != View.VISIBLE) {
				// mToast.makeText(IncenMonthlyActivity.this,
				// "Please enter digits only!", Toast.LENGTH_SHORT).show();
				// }
				if (Integer.parseInt(mString) == 0) {
					mQuarterSlider.setValue(89);
					businessIncenLogic(true);
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void setTextAccordingToQuarter() {
		mAccordionMon1.setSectionHeaders("Quarter " + whichQuarter);
		mTotalQuartelLabel.setText("Total Quarter " + whichQuarter + " : ");

		switch (whichQuarter) {
		case 1:
			mWhichQuarterLabel.setText(getResources().getString(
					R.string.quarter_month_1));
			break;
		case 2:
			mWhichQuarterLabel.setText(getResources().getString(
					R.string.quarter_month_2));
			break;
		case 3:
			mWhichQuarterLabel.setText(getResources().getString(
					R.string.quarter_month_3));
			break;
		case 4:
			mWhichQuarterLabel.setText(getResources().getString(
					R.string.quarter_month_4));
			break;

		}
	}

	private void businessIncenLogic(boolean isPointToZero) {
		try {
			if (!isPointToZero) {
				mQuarterTotal
						.setText(String.valueOf(INCENQUARTERMAP[mQuarterSlider
								.getValue() - 90]));
			} else {
				mQuarterTotal.setText("0");
			}
			saveMonthlyValues(whichQuarter);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void restoreValueFromPreferences() {
		switch (whichQuarter) {
		case 1:
			mQuarterSliderArr = ApplicationLoader.getPreferences().getQ1();
			mQuarterTotalArr = ApplicationLoader.getPreferences().getQValue1();
			break;
		case 2:
			mQuarterSliderArr = ApplicationLoader.getPreferences().getQ2();
			mQuarterTotalArr = ApplicationLoader.getPreferences().getQValue2();
			break;
		case 3:
			mQuarterSliderArr = ApplicationLoader.getPreferences().getQ3();
			mQuarterTotalArr = ApplicationLoader.getPreferences().getQValue3();
			break;
		case 4:
			mQuarterSliderArr = ApplicationLoader.getPreferences().getQ4();
			mQuarterTotalArr = ApplicationLoader.getPreferences().getQValue4();
			break;
		}
		checkValuesFromPreferencesNullOrNot();
		setValuesFromPreferences();
	}

	private void checkValuesFromPreferencesNullOrNot() {
		if (mQuarterSliderArr == null) {
			mQuarterSliderArr = "89";
		}

		if (mQuarterTotalArr == null) {
			mQuarterTotalArr = "0";
		}

		try {
			mQuarterPerArr = mQuarterSliderArr;
			if (mQuarterSliderArr == null
					|| Integer.parseInt(mQuarterSliderArr) == 89) {
				mQuarterPerArr = "0";
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
			mQuarterPerArr = "0";
		}
	}

	private void setValuesFromPreferences() {
		mQuarterSlider.setValue(Integer.parseInt(mQuarterSliderArr));
		mQuarterPer.setText(mQuarterPerArr);
		mQuarterTotal.setText(mQuarterTotalArr);
	}

	private void saveMonthlyValues(int whichQuarter) {
		switch (whichQuarter) {
		case 1:
			ApplicationLoader.getPreferences().setQ1(
					String.valueOf(mQuarterSlider.getValue()));
			ApplicationLoader.getPreferences().setQValue1(
					mQuarterTotal.getText().toString());
			break;
		case 2:
			ApplicationLoader.getPreferences().setQ2(
					String.valueOf(mQuarterSlider.getValue()));
			ApplicationLoader.getPreferences().setQValue2(
					mQuarterTotal.getText().toString());
			break;
		case 3:
			ApplicationLoader.getPreferences().setQ3(
					String.valueOf(mQuarterSlider.getValue()));
			ApplicationLoader.getPreferences().setQValue3(
					mQuarterTotal.getText().toString());
			break;
		case 4:
			ApplicationLoader.getPreferences().setQ4(
					String.valueOf(mQuarterSlider.getValue()));
			ApplicationLoader.getPreferences().setQValue4(
					mQuarterTotal.getText().toString());
			break;
		}

	}

	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mTotalRsSy.setTypeface(mTypeFace);

		mTotalRsSy.setText("`");
	}

	private void setSecurity() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
	}

	/*
	 * Get Data From JSON
	 */

	public void getBaseJSON() {
		if (!BuildVars.debug) {
			if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
					.getMonthQuarterJSON())) {
				parseJSON(ApplicationLoader.getPreferences()
						.getMonthQuarterJSON());
			} else {
				showAlertDialog();
			}
		} else {
			String str = Utilities.readFile("data1.json");
			parseJSON(str);
		}
	}

	public void parseJSON(String str) {
		getMonthBase(str);
	}

	public void getMonthBase(String str) {
		try {
			JSONObject mJSONObj = new JSONObject(str);
			JSONArray mJSONArray = mJSONObj.getJSONArray("quarter");

			for (int i = 0; i < mJSONArray.length(); i++) {
				JSONObject mInnerJSONObj = mJSONArray.getJSONObject(i);

				int[] mSlab = new int[(Integer.parseInt(mInnerJSONObj
						.getString("max")) - Integer.parseInt(mInnerJSONObj
						.getString("min"))) + 1];
				for (int j = 0; j < (Integer.parseInt(mInnerJSONObj
						.getString("max"))
						- (Integer.parseInt(mInnerJSONObj.getString("min"))) + 1); j++) {
					mSlab[j] = Integer.parseInt(mInnerJSONObj.getString("slab"
							+ (j + 1)));
				}
				INCENQUARTERMAP = new int[mSlab.length];
				System.arraycopy(mSlab, 0, INCENQUARTERMAP, 0, mSlab.length);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenQuarterlyActivity.this);

		// set title
		alertDialogBuilder.setTitle("Sanofi Mobcast");

		// set dialog message
		alertDialogBuilder
				.setMessage("Will try again to get your incentive data")
				.setCancelable(false)
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// ApplicationLoader.getPreferences().setIncenFirstTime(true);
								// finish();
								if (Utilities.isInternetConnected()) {
									new AsyncBaseDataFromApi(true).execute();
								} else {
									Toast.makeText(
											IncenQuarterlyActivity.this,
											"Please check your internet connection",
											Toast.LENGTH_SHORT).show();
									showAlertDialog();
								}
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public class AsyncBaseDataFromApi extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isMonthQuarterData = false;
		private boolean forceUser = false;
		private String strJSONData;

		public AsyncBaseDataFromApi(boolean forceUser) {
			this.forceUser = forceUser;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (forceUser) {
				mProgress = new ProgressDialog(IncenQuarterlyActivity.this);
				mProgress.setMessage("Please wait");
				mProgress.setCanceledOnTouchOutside(false);
				mProgress.setCancelable(false);
				mProgress.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String temp1 = pref.getString("name", "tushar@mobcast.in");

			HashMap<String, String> formValue = new HashMap<String, String>();
			formValue.put("device", "android");
			if (!BuildVars.debug) {
				formValue.put(com.mobcast.util.Constants.user_id, temp1);
			} else {
				formValue.put(com.mobcast.util.Constants.user_id,
						"tushar@mobcast.in");
			}

			String str = RestClient.postData(Constants.INCEN_BASE, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray mJSONArray = mJSONObj.getJSONArray("quarter");
				if (mJSONArray.length() > 0) {
					isMonthQuarterData = true;
					strJSONData = str;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (forceUser) {
				if (mProgress != null)
					mProgress.dismiss();
			}

			if (isMonthQuarterData) {
				ApplicationLoader.getPreferences().setMonthQuarterJSON(
						strJSONData);
				ApplicationLoader.getPreferences().setIncenFirstTime(true);
			}
			getBaseJSON();
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
}
