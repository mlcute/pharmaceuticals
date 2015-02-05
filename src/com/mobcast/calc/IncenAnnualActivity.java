package com.mobcast.calc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.AssetManager;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.calc.IncenDashBoardActivity.AsyncDownloadPdfDataFromApi;
import com.mobcast.calc.IncenDashBoardActivity.AsyncDownloadPdfPathFromApi;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.mobcast.view.AccordionView;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class IncenAnnualActivity extends FragmentActivity {

	private AccordionView mAccordionTotal;
	private AccordionView mAccordionQuarter;
	private AccordionView mAccordionAnnual;

	private CheckBox mCheckBoxAnnual;

	private CheckBox mCheckBoxQuarter1;
	private CheckBox mCheckBoxQuarter2;
	private CheckBox mCheckBoxQuarter3;
	private CheckBox mCheckBoxQuarter4;

	private TextView mQuarterRsSy1;
	private TextView mQuarterRsSy2;
	private TextView mQuarterRsSy3;
	private TextView mQuarterRsSy4;

	private TextView mQuarterTotal1;
	private TextView mQuarterTotal2;
	private TextView mQuarterTotal3;
	private TextView mQuarterTotal4;

	private TextView mTotalAnnual;
	private TextView mTotalAnnualRsSy;

	private TextView mTotalTv;

	private TextView mTotalRsSy;
	private Typeface mTypeFace;

	private ImageView mInceIcon;
	private ImageView mIncenOverFlow;

	private TextView mPopUpMenuPdf;
	private RelativeLayout mLayout;

	private String mQuarterIsCheck1;
	private String mQuarterIsCheck2;
	private String mQuarterIsCheck3;
	private String mQuarterIsCheck4;
	private String mAnnualIsCheck;

	private int mQuarterAchieved = 0;

	private int mTotalAnnualArr = 0;

	private int mBusinessFourQuarter = 0;
	private int mBusinessThreeQuarter = 0;
	private int mBusinessTwoQuarter = 0;

	/*
	 * Additonal Variables for BIO - Surgery
	 */
	private AccordionView mAccordionBioSurgery;
	private CheckBox mCheckBoxBioSurgery1;
	private CheckBox mCheckBoxBioSurgery2;
	private TextView mTotalBioSurgery;
	private TextView mTotalBioSurgeryRs;

	private static final String TAG = IncenAnnualActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_annual);
		initUi();
		setRupeeFont();
		setCheckBoxGroup();
		setCheckedToGetValues();
		restoreValuesFromPreferences();
		getBaseJSON();
		businessIncenLogic();
		setUiListener();
	}

	private void initUi() {
		mAccordionTotal = (AccordionView) findViewById(R.id.incen_annual_total_acc_view);
		mAccordionTotal.toggleSection(0);
		mAccordionTotal.setSectionHeaders("Total");

		mAccordionQuarter = (AccordionView) findViewById(R.id.incen_quarter_acc_view);
		mAccordionQuarter.toggleSection(0);
		mAccordionQuarter.setSectionHeaders("Quarter");

		mAccordionAnnual = (AccordionView) findViewById(R.id.incen_annual_acc_view);
		mAccordionAnnual.toggleSection(0);
		mAccordionAnnual.setSectionHeaders("Annual");

		mTotalRsSy = (TextView) findViewById(R.id.incen_annual_total_rs_sy);

		mTotalAnnual = (TextView) findViewById(R.id.incen_annual_incen);
		mTotalAnnualRsSy = (TextView) findViewById(R.id.incen_annual_rs_sy);

		mTotalTv = (TextView) findViewById(R.id.incen_annual_total_incen);

		mCheckBoxQuarter1 = (CheckBox) findViewById(R.id.incen_annual_switchView1);
		mCheckBoxQuarter2 = (CheckBox) findViewById(R.id.incen_annual_switchView2);
		mCheckBoxQuarter3 = (CheckBox) findViewById(R.id.incen_annual_switchView3);
		mCheckBoxQuarter4 = (CheckBox) findViewById(R.id.incen_annual_switchView4);

		mCheckBoxAnnual = (CheckBox) findViewById(R.id.incen_annual_switchView_);

		mQuarterRsSy1 = (TextView) findViewById(R.id.incen_annual_quarter1_rs_sy);
		mQuarterRsSy2 = (TextView) findViewById(R.id.incen_annual_quarter2_rs_sy);
		mQuarterRsSy3 = (TextView) findViewById(R.id.incen_annual_quarter3_rs_sy);
		mQuarterRsSy4 = (TextView) findViewById(R.id.incen_annual_quarter4_rs_sy);

		mQuarterTotal1 = (TextView) findViewById(R.id.incen_annual_quarter1);
		mQuarterTotal2 = (TextView) findViewById(R.id.incen_annual_quarter2);
		mQuarterTotal3 = (TextView) findViewById(R.id.incen_annual_quarter3);
		mQuarterTotal4 = (TextView) findViewById(R.id.incen_annual_quarter4);

		mInceIcon = (ImageView) findViewById(R.id.titleBackIcon);
		mIncenOverFlow = (ImageView) findViewById(R.id.incen_overflow);

		mLayout = (RelativeLayout) findViewById(R.id.mLayout);

		if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()) {
			addInitUi();
		}
	}

	private void addInitUi() {
		mAccordionBioSurgery = (AccordionView) findViewById(R.id.incen_bio_surgery_acc_view);

		mCheckBoxBioSurgery1 = (CheckBox) findViewById(R.id.incen_bio_surgery_switchView1);
		mCheckBoxBioSurgery2 = (CheckBox) findViewById(R.id.incen_bio_surgery_switchView2);

		mTotalBioSurgery = (TextView) findViewById(R.id.incen_bio_surgery_incen);

		mTotalBioSurgeryRs = (TextView) findViewById(R.id.incen_bio_surgery_rs_sy);
		
		mAccordionBioSurgery.setVisibility(View.VISIBLE);
		mAccordionBioSurgery.toggleSection(0);
		mAccordionBioSurgery.setSectionHeaders("ESI Incentive");
	}

	private void setUiListener() {
		mInceIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mIncenOverFlow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showPopUpMenu();
			}
		});
	}

	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mTotalRsSy.setTypeface(mTypeFace);
		mTotalAnnualRsSy.setTypeface(mTypeFace);
		mQuarterRsSy1.setTypeface(mTypeFace);
		mQuarterRsSy2.setTypeface(mTypeFace);
		mQuarterRsSy3.setTypeface(mTypeFace);
		mQuarterRsSy4.setTypeface(mTypeFace);

		mTotalRsSy.setText("`");
		mTotalAnnualRsSy.setText("`");
		mQuarterRsSy1.setText("`");
		mQuarterRsSy2.setText("`");
		mQuarterRsSy3.setText("`");
		mQuarterRsSy4.setText("`");

		if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()) {
			addSetRupeeFont();
		}
	}

	private void addSetRupeeFont() {
		mTotalBioSurgeryRs.setTypeface(mTypeFace);

		mTotalBioSurgeryRs.setText("`");
	}

	private void setCheckBoxGroup() {
		mCheckBoxQuarter1
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						String monthTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getQuarterTotal1()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQuarterTotal1();
						String kpiTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getKPITotalValue1()) ? "0"
								: ApplicationLoader.getPreferences()
										.getKPITotalValue1();
						String productTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getProductTotalValue1()) ? "0"
								: ApplicationLoader.getPreferences()
										.getProductTotalValue1();
						String quarterTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getQValue1()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQValue1();

						String quarter1Total = String.valueOf(Integer
								.parseInt(monthTotal)
								+ Integer.parseInt(kpiTotal)
								+ Integer.parseInt(productTotal)
								+ Integer.parseInt(quarterTotal));
						mQuarterTotal1.setText(quarter1Total);
						if (isChecked) {
							ApplicationLoader.getPreferences()
									.setAnnualQCheck1("1");
							mQuarterAchieved++;
						} else {
							mQuarterAchieved--;
						}
						businessIncenLogic();
					}
				});

		mCheckBoxQuarter2
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						String monthTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getQuarterTotal2()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQuarterTotal2();
						String kpiTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getKPITotalValue2()) ? "0"
								: ApplicationLoader.getPreferences()
										.getKPITotalValue2();
						String productTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getProductTotalValue2()) ? "0"
								: ApplicationLoader.getPreferences()
										.getProductTotalValue2();
						String quarterTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getQValue2()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQValue2();

						String quarter1Total = String.valueOf(Integer
								.parseInt(monthTotal)
								+ Integer.parseInt(kpiTotal)
								+ Integer.parseInt(productTotal)
								+ Integer.parseInt(quarterTotal));
						mQuarterTotal2.setText(quarter1Total);
						if (isChecked) {
							ApplicationLoader.getPreferences()
									.setAnnualQCheck2("1");
							mQuarterAchieved++;
						} else {
							mQuarterAchieved--;
						}
						businessIncenLogic();
					}
				});

		mCheckBoxQuarter3
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						String monthTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getQuarterTotal3()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQuarterTotal3();
						String kpiTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getKPITotalValue3()) ? "0"
								: ApplicationLoader.getPreferences()
										.getKPITotalValue3();
						String productTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getProductTotalValue3()) ? "0"
								: ApplicationLoader.getPreferences()
										.getProductTotalValue3();
						String quarterTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getQValue3()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQValue3();

						String quarter1Total = String.valueOf(Integer
								.parseInt(monthTotal)
								+ Integer.parseInt(kpiTotal)
								+ Integer.parseInt(productTotal)
								+ Integer.parseInt(quarterTotal));
						mQuarterTotal3.setText(quarter1Total);
						if (isChecked) {
							ApplicationLoader.getPreferences()
									.setAnnualQCheck3("1");
							mQuarterAchieved++;
						} else {
							mQuarterAchieved--;
						}
						businessIncenLogic();
					}
				});

		mCheckBoxQuarter4
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						String monthTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getQuarterTotal4()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQuarterTotal4();
						String kpiTotal = TextUtils.isEmpty(ApplicationLoader
								.getPreferences().getKPITotalValue4()) ? "0"
								: ApplicationLoader.getPreferences()
										.getKPITotalValue4();
						String productTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getProductTotalValue4()) ? "0"
								: ApplicationLoader.getPreferences()
										.getProductTotalValue4();
						String quarterTotal = TextUtils
								.isEmpty(ApplicationLoader.getPreferences()
										.getQValue4()) ? "0"
								: ApplicationLoader.getPreferences()
										.getQValue4();

						String quarter1Total = String.valueOf(Integer
								.parseInt(monthTotal)
								+ Integer.parseInt(kpiTotal)
								+ Integer.parseInt(productTotal)
								+ Integer.parseInt(quarterTotal));
						mQuarterTotal4.setText(quarter1Total);
						if (isChecked) {
							ApplicationLoader.getPreferences()
									.setAnnualQCheck4("1");
							mQuarterAchieved++;
						} else {
							mQuarterAchieved--;
						}
						businessIncenLogic();
					}
				});

		mCheckBoxAnnual
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							ApplicationLoader.getPreferences().setAnnualCheck(
									"1");
						} else {
							ApplicationLoader.getPreferences().setAnnualCheck(
									"0");
						}
						businessIncenLogic();
					}
				});
		if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()) {
			addSetCheckBoxGroup();
		}
	}

	private void addSetCheckBoxGroup() {
		mCheckBoxBioSurgery1
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean isChecked) {
						// TODO Auto-generated method stub
						businessIncenLogic();
					}
				});

		mCheckBoxBioSurgery2
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						businessIncenLogic();
					}
				});
	}

	private void setCheckedToGetValues() {
		getQuarter1Value();
		getQuarter2Value();
		getQuarter3Value();
		getQuarter4Value();
	}

	private void restoreValuesFromPreferences() {
		mQuarterIsCheck1 = ApplicationLoader.getPreferences()
				.getAnnualQCheck1();
		mQuarterIsCheck2 = ApplicationLoader.getPreferences()
				.getAnnualQCheck2();
		mQuarterIsCheck3 = ApplicationLoader.getPreferences()
				.getAnnualQCheck3();
		mQuarterIsCheck4 = ApplicationLoader.getPreferences()
				.getAnnualQCheck4();
		mAnnualIsCheck = ApplicationLoader.getPreferences().getAnnualCheck();

		checkValuesFromPreferencesNullOrNot();
		setValuesFromPreferences();
	}

	private void checkValuesFromPreferencesNullOrNot() {
		if (mQuarterIsCheck1 == null) {
			mQuarterIsCheck1 = "0";
		}

		if (mQuarterIsCheck2 == null) {
			mQuarterIsCheck2 = "0";
		}

		if (mQuarterIsCheck3 == null) {
			mQuarterIsCheck3 = "0";
		}

		if (mQuarterIsCheck4 == null) {
			mQuarterIsCheck4 = "0";
		}

		if (mAnnualIsCheck == null) {
			mAnnualIsCheck = "0";
		}
	}

	private void setValuesFromPreferences() {
		if (mQuarterIsCheck1.equalsIgnoreCase("0")) {
			mCheckBoxQuarter1.setChecked(false);
		} else {
			mCheckBoxQuarter1.setChecked(true);
		}

		if (mQuarterIsCheck2.equalsIgnoreCase("0")) {
			mCheckBoxQuarter2.setChecked(false);
		} else {
			mCheckBoxQuarter2.setChecked(true);
		}

		if (mQuarterIsCheck3.equalsIgnoreCase("0")) {
			mCheckBoxQuarter3.setChecked(false);
		} else {
			mCheckBoxQuarter3.setChecked(true);
		}

		if (mQuarterIsCheck4.equalsIgnoreCase("0")) {
			mCheckBoxQuarter4.setChecked(false);
		} else {
			mCheckBoxQuarter4.setChecked(true);
		}

		if (mAnnualIsCheck.equalsIgnoreCase("0")) {
			mCheckBoxAnnual.setChecked(false);
		} else {
			mCheckBoxAnnual.setChecked(true);
		}

	}

	private void businessIncenLogic() {
		try {
			if (mCheckBoxAnnual.isChecked()) {
				if (mQuarterAchieved > 3) {
					mTotalAnnualArr = mBusinessFourQuarter;
				} else if (mQuarterAchieved == 3) {
					mTotalAnnualArr = mBusinessThreeQuarter;
				} else if (mQuarterAchieved == 2) {
					mTotalAnnualArr = mBusinessTwoQuarter;
				} else {
					mTotalAnnualArr = 0;
				}
			} else {
				mTotalAnnualArr = 0;
			}

			mTotalAnnual.setText(String.valueOf(mTotalAnnualArr));

			int mQ1 = 0, mQ2 = 0, mQ3 = 0, mQ4 = 0;

			if (!TextUtils.isEmpty(mQuarterTotal1.getText().toString())) {
				mQ1 = Integer.parseInt(mQuarterTotal1.getText().toString());
			}

			if (!TextUtils.isEmpty(mQuarterTotal2.getText().toString())) {
				mQ2 = Integer.parseInt(mQuarterTotal2.getText().toString());
			}

			if (!TextUtils.isEmpty(mQuarterTotal3.getText().toString())) {
				mQ3 = Integer.parseInt(mQuarterTotal3.getText().toString());
			}

			if (!TextUtils.isEmpty(mQuarterTotal4.getText().toString())) {
				mQ4 = Integer.parseInt(mQuarterTotal4.getText().toString());
			}
			
			if(ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()){
				addBusinessIncenLogic();
			}

			mTotalTv.setText(String.valueOf(mQ1 + mQ2 + mQ3 + mQ4
					+ mTotalAnnualArr));
			saveAnnual();
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private void addBusinessIncenLogic(){
		if(mCheckBoxBioSurgery1.isChecked()){
			mTotalAnnualArr =  mTotalAnnualArr + 10000;
			mTotalBioSurgery.setText("10000");
		}
		
		if(mCheckBoxBioSurgery2.isChecked()){
			mTotalAnnualArr =  mTotalAnnualArr + 10000;
			mTotalBioSurgery.setText("10000");
		}
		
		if(mCheckBoxBioSurgery1.isChecked()  && mCheckBoxBioSurgery2.isChecked()){
			mTotalBioSurgery.setText("20000");
		}else if(!mCheckBoxBioSurgery1.isChecked()  && !mCheckBoxBioSurgery2.isChecked()){
			mTotalBioSurgery.setText("0");
		}
	}

	public void saveAnnual() {
		ApplicationLoader.getPreferences().setAnnualQCheck1(
				mCheckBoxQuarter1.isChecked() ? "1" : "0");
		ApplicationLoader.getPreferences().setAnnualQCheck2(
				mCheckBoxQuarter2.isChecked() ? "1" : "0");
		ApplicationLoader.getPreferences().setAnnualQCheck3(
				mCheckBoxQuarter3.isChecked() ? "1" : "0");
		ApplicationLoader.getPreferences().setAnnualQCheck4(
				mCheckBoxQuarter4.isChecked() ? "1" : "0");
		ApplicationLoader.getPreferences().setAnnualCheck(
				mCheckBoxAnnual.isChecked() ? "1" : "0");
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
			JSONArray mJSONArray = mJSONObj.getJSONArray("annual");
			for (int i = 0; i < mJSONArray.length(); i++) {
				JSONObject mInnerJSONObj = mJSONArray.getJSONObject(i);

				mBusinessFourQuarter = Integer.parseInt(mInnerJSONObj
						.getString("fourquarter"));
				mBusinessThreeQuarter = Integer.parseInt(mInnerJSONObj
						.getString("threequarter"));
				mBusinessTwoQuarter = Integer.parseInt(mInnerJSONObj
						.getString("twoquarter"));
			}
			try {
				ApplicationLoader.getPreferences().setIncenTeamName(mJSONObj.getString("team"));
				if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("") == 0){
					ApplicationLoader.getPreferences().setIncenBioSurgeryTeam(true);
				}else if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("") == 0){
					ApplicationLoader.getPreferences().setIncenRenealTeam(true);
				}else if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("") == 0){
					ApplicationLoader.getPreferences().setIncenHeritageTeam(true);
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenAnnualActivity.this);

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
											IncenAnnualActivity.this,
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
				mProgress = new ProgressDialog(IncenAnnualActivity.this);
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
				JSONArray mJSONArray = mJSONObj.getJSONArray("annual");
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

	private void getQuarter1Value() {
		String monthTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQuarterTotal1()) ? "0" : ApplicationLoader
				.getPreferences().getQuarterTotal1();
		String kpiTotal = TextUtils.isEmpty(ApplicationLoader.getPreferences()
				.getKPITotalValue1()) ? "0" : ApplicationLoader
				.getPreferences().getKPITotalValue1();
		String productTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getProductTotalValue1()) ? "0"
				: ApplicationLoader.getPreferences().getProductTotalValue1();
		String quarterTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQValue1()) ? "0" : ApplicationLoader
				.getPreferences().getQValue1();

		String quarter1Total = String.valueOf(Integer.parseInt(monthTotal)
				+ Integer.parseInt(kpiTotal) + Integer.parseInt(productTotal)
				+ Integer.parseInt(quarterTotal));
		mQuarterTotal1.setText(quarter1Total);
	}

	private void getQuarter2Value() {
		String monthTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQuarterTotal2()) ? "0" : ApplicationLoader
				.getPreferences().getQuarterTotal2();
		String kpiTotal = TextUtils.isEmpty(ApplicationLoader.getPreferences()
				.getKPITotalValue2()) ? "0" : ApplicationLoader
				.getPreferences().getKPITotalValue2();
		String productTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getProductTotalValue2()) ? "0"
				: ApplicationLoader.getPreferences().getProductTotalValue2();
		String quarterTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQValue2()) ? "0" : ApplicationLoader
				.getPreferences().getQValue2();

		String quarter1Total = String.valueOf(Integer.parseInt(monthTotal)
				+ Integer.parseInt(kpiTotal) + Integer.parseInt(productTotal)
				+ Integer.parseInt(quarterTotal));
		mQuarterTotal2.setText(quarter1Total);
	}

	private void getQuarter3Value() {
		String monthTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQuarterTotal3()) ? "0" : ApplicationLoader
				.getPreferences().getQuarterTotal3();
		String kpiTotal = TextUtils.isEmpty(ApplicationLoader.getPreferences()
				.getKPITotalValue3()) ? "0" : ApplicationLoader
				.getPreferences().getKPITotalValue3();
		String productTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getProductTotalValue3()) ? "0"
				: ApplicationLoader.getPreferences().getProductTotalValue3();
		String quarterTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQValue3()) ? "0" : ApplicationLoader
				.getPreferences().getQValue3();

		String quarter1Total = String.valueOf(Integer.parseInt(monthTotal)
				+ Integer.parseInt(kpiTotal) + Integer.parseInt(productTotal)
				+ Integer.parseInt(quarterTotal));
		mQuarterTotal3.setText(quarter1Total);
	}

	private void getQuarter4Value() {
		String monthTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQuarterTotal4()) ? "0" : ApplicationLoader
				.getPreferences().getQuarterTotal4();
		String kpiTotal = TextUtils.isEmpty(ApplicationLoader.getPreferences()
				.getKPITotalValue4()) ? "0" : ApplicationLoader
				.getPreferences().getKPITotalValue4();
		String productTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getProductTotalValue4()) ? "0"
				: ApplicationLoader.getPreferences().getProductTotalValue4();
		String quarterTotal = TextUtils.isEmpty(ApplicationLoader
				.getPreferences().getQValue4()) ? "0" : ApplicationLoader
				.getPreferences().getQValue4();

		String quarter1Total = String.valueOf(Integer.parseInt(monthTotal)
				+ Integer.parseInt(kpiTotal) + Integer.parseInt(productTotal)
				+ Integer.parseInt(quarterTotal));
		mQuarterTotal4.setText(quarter1Total);
	}

	@SuppressLint("NewApi")
	public void showPopUpMenu() {
		final Dialog dialog = new Dialog(IncenAnnualActivity.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f;
			lp.gravity = Gravity.TOP | Gravity.RIGHT;
			lp.y = mIncenOverFlow.getHeight() + 37;
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			// dialog.getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.incen_pop_up_menu_annual);

		mPopUpMenuPdf = (TextView) dialog
				.findViewById(R.id.pop_up_menu_incen_pdf);

		if (Build.VERSION.SDK_INT < 11) {
			final AlphaAnimation animation = new AlphaAnimation(1.0f, 0.5f);
			animation.setDuration(1000);
			animation.setFillAfter(true);
			mLayout.startAnimation(animation);
		} else {
			mLayout.setAlpha(0.5f);
		}

		mPopUpMenuPdf.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showPdf();
			}
		});

		dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (Build.VERSION.SDK_INT < 11) {
					final AlphaAnimation animation = new AlphaAnimation(0.5f,
							1.0f);
					animation.setDuration(1000);
					animation.setFillAfter(true);
					mLayout.startAnimation(animation);
				} else {
					mLayout.setAlpha(1.0f);
				}
			}
		});
		dialog.show();
	}

	private void showPdf() {
//		File file1 = new File("/sdcard/incen.pdf");
//		if (!file1.exists()) {
//			copyPdfFromAssets();
//		}
		
		if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
				.getIncenPdfPath())) {
			File mFile = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ getString(R.string.pdf)
					+ "/"
					+ ApplicationLoader.getPreferences().getIncenPdfPath());
			if (!mFile.exists()) {
				showDownloadDialog();
			} else {
				showOpenPdfDialog();
			}
		} else {
			showDownloadDialog();
		}
	}

	private void copyPdfFromAssets() {
		try {
			AssetManager assetManager = getAssets();

			InputStream in = null;
			OutputStream out = null;
			File file = new File(getFilesDir(), "incen.pdf");
			try {
				in = assetManager.open("incen.pdf");
				out = openFileOutput(file.getName(),
						Context.MODE_WORLD_READABLE);

				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				Log.e("tag", e.getMessage());
			}

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(
					Uri.parse("file://" + getFilesDir() + "/incen.pdf"),
					"application/pdf");

			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(IncenAnnualActivity.this,
					"No Pdf Reader Application Found!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
	
	private void showOpenPdfDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenAnnualActivity.this);
		// set title
		alertDialogBuilder
				.setTitle(getResources().getString(R.string.app_name));

		// set dialog message
		alertDialogBuilder
				.setMessage(
						getResources().getString(
								R.string.download_alert_message))
				.setCancelable(true)
				.setPositiveButton("Open",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									openPDFReaderIntent(ApplicationLoader
											.getPreferences().getIncenPdfPath());
								} catch (Exception e) {
									Toast.makeText(IncenAnnualActivity.this,
											"No PdfReader Application Found!",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenAnnualActivity.this);
		// set title
		alertDialogBuilder
				.setTitle(getResources().getString(R.string.app_name));

		// set dialog message
		alertDialogBuilder
				.setMessage(
						getResources().getString(
								R.string.download_alert_message))
				.setCancelable(true)
				.setPositiveButton("Download",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								downloadPdfFromUrl();
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void downloadPdfFromUrl() {
		if (Utilities.isInternetConnected()) {
			new AsyncDownloadPdfPathFromApi(true).execute();
		} else {
			Toast.makeText(IncenAnnualActivity.this,
					"Please check your internet connection!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public class AsyncDownloadPdfPathFromApi extends
			AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isPdfAvailable = false;
		private boolean forceUser = false;
		private String pdfPath;

		public AsyncDownloadPdfPathFromApi(boolean forceUser) {
			this.forceUser = forceUser;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (forceUser) {
				mProgress = new ProgressDialog(IncenAnnualActivity.this);
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
			formValue.put(com.mobcast.util.Constants.user_id, temp1);

			String str = RestClient.postData(Constants.INCEN_SCHEME_PDF,
					formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				pdfPath = mJSONObj.getString("pdfPath");
				if (!TextUtils.isEmpty(pdfPath)) {
					isPdfAvailable = true;
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

			if (isPdfAvailable) {
				new AsyncDownloadPdfDataFromApi(true, pdfPath).execute(pdfPath);
			} else {
				Toast.makeText(IncenAnnualActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class AsyncDownloadPdfDataFromApi extends
			AsyncTask<String, String, String> {
		private ProgressDialog mProgress;
		private boolean forceUser = false;
		private String pdfPath;
		private String mFileName;

		public AsyncDownloadPdfDataFromApi(boolean forceUser, String pdfPath) {
			this.forceUser = forceUser;
			this.pdfPath = pdfPath;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (forceUser) {
				mProgress = new ProgressDialog(IncenAnnualActivity.this);
				mProgress.setMessage("Downloading...");
				mProgress.setCanceledOnTouchOutside(false);
				mProgress.setCancelable(false);
				mProgress.setMax(100);
				mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				Drawable customDrawable = getResources().getDrawable(
						R.drawable.custom_progressbar);
				mProgress.setProgressDrawable(customDrawable);
				mProgress.show();
			}
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			// TODO Auto-generated method stub
			// super.onProgressUpdate(values);
			mProgress.setProgress(Integer.parseInt(progress[0]));
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				URL mURL = new URL(pdfPath);
				String mRoot = Environment.getExternalStorageDirectory()
						.toString();
				String foldername = "";
				foldername = getResources().getString(R.string.pdf);

				File mDir = new File(mRoot + "/.mobcast/" + foldername);

				mFileName = Utilities.getFileNameFromURL(pdfPath);

				ApplicationLoader.getPreferences().setIncenPdfPath(mFileName);

				mDir.mkdirs();

				File mFile = new File(mDir, mFileName);
				if (mFile.exists())
					mFile.delete();

				URLConnection mConnection = mURL.openConnection();
				InputStream inputStream = mConnection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
				byte data[] = new byte[1024];
				int current = 0;
				long total = 0;
				int lenghtOfFile = mConnection.getContentLength();
				while ((current = bis.read()) != -1) {
					total += current;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					mFileOutputStream.write(data, 0, current);
				}
				mFileOutputStream.flush();
				mFileOutputStream.close();
				inputStream.close();
			} catch (Exception e) {
			}
			return "false";
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (forceUser) {
				if (mProgress != null)
					mProgress.dismiss();
			}

			if (!TextUtils.isEmpty(mFileName)) {
				try {
					openPDFReaderIntent(mFileName);
				} catch (Exception e) {
					Toast.makeText(IncenAnnualActivity.this,
							"No PdfReader Application Found!",
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, e.toString());
				}
			} else {
				Toast.makeText(IncenAnnualActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void openPDFReaderIntent(String mName) {
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ Constants.APP_FOLDER
				+ getString(R.string.pdf) + "/" + mName);
		if (file.exists())
			Log.d("file", "exists");
		else
			Log.e("file", "does not exists");
		double bytes = file.length() / 500;
		int time = (int) bytes;
		Log.e("file length", time + "");
		// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
		File folder = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.APP_FOLDER_TEMP);
		folder.mkdirs();
		File file1 = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.APP_FOLDER_TEMP + mName);
		if (file1.exists())
			file1.delete();

		try {

			InputStream in = new FileInputStream(file);
			OutputStream out = new FileOutputStream(file1);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent show = new Intent(Intent.ACTION_VIEW);
		show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		show.setAction(Intent.ACTION_VIEW);
		show.setDataAndType(Uri.fromFile(file1), "application/pdf");
		startActivity(show);
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
