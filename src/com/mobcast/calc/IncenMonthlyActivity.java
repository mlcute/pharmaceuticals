/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- INCENTIVE MONTHLY ACTIVITY
 * DESCRIPTION 		:- SHOWS INCENTIVE MONTH SCREEN
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001       VIKALP PATEL    01/01/2015       				CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
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
import android.view.WindowManager.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class IncenMonthlyActivity extends FragmentActivity {

	private AccordionView mAccordionMon1;
	private AccordionView mAccordionMon2;
	private AccordionView mAccordionMon3;

	private AccordionView mAccordionMonTotal;

	private Typeface mTypeFace;

	private TextView mTotal1RsSy;
	private TextView mTotal2RsSy;
	private TextView mTotal3RsSy;
	private TextView mTotalRsSy;

	private TextView mTotalMonthName1;
	private TextView mTotalMonthName2;
	private TextView mTotalMonthName3;

	private Slider mMonthSlider1;
	private Slider mMonthSlider2;
	private Slider mMonthSlider3;

	private EditText mMonthPer1;
	private EditText mMonthPer2;
	private EditText mMonthPer3;

	private CheckBox mMidMonthCheckBox1;
	private CheckBox mMidMonthCheckBox2;
	private CheckBox mMidMonthCheckBox3;

	private TextView mTotalMonth1;
	private TextView mTotalMonth2;
	private TextView mTotalMonth3;

	private TextView mTotalMonth;

	private Toast mToast;

	private int whichQuarter;
	private String[] mArrMonthName;
	private String[] mArrTempMonthName;

	private String[] mSliderValueArr;
	private String[] mPerValueArr;
	private String[] mMonthTotalValueArr;
	private String[] mMonthCheckValueArr;

	private String mTotalArr;

	private int mMidMonthBusinessLogic = 0;
	private int INCENMONTHMAP[];

	private static final String TAG = IncenMonthlyActivity.class
			.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_month);
		setSecurity();
		initUi();
		getIntentData();
		setAccordionHeader();
		setRupeeFont();
		setTextAccordingToQuarter();
		restoreValueFromPreferences();
		getBaseJSON();
		setListener();
	}

	/**
	 * Initialize Ui elements
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void initUi() {
		mAccordionMon1 = (AccordionView) findViewById(R.id.incen_month_acc_view_1);
		mAccordionMon2 = (AccordionView) findViewById(R.id.incen_month_acc_view_2);
		mAccordionMon3 = (AccordionView) findViewById(R.id.incen_month_acc_view_3);

		mAccordionMonTotal = (AccordionView) findViewById(R.id.incen_month_total_acc_view);

		mTotal1RsSy = (TextView) findViewById(R.id.incen_month_1_total_rs_sy);
		mTotal2RsSy = (TextView) findViewById(R.id.incen_month_2_total_rs_sy);
		mTotal3RsSy = (TextView) findViewById(R.id.incen_month_3_total_rs_sy);
		mTotalRsSy = (TextView) findViewById(R.id.incen_month_total_rs_sy);

		mTotalMonthName1 = (TextView) findViewById(R.id.incen_month_1_total_tv);
		mTotalMonthName2 = (TextView) findViewById(R.id.incen_month_2_total_tv);
		mTotalMonthName3 = (TextView) findViewById(R.id.incen_month_3_total_tv);

		mMonthSlider1 = (Slider) findViewById(R.id.activity_month_1_slider);
		mMonthSlider2 = (Slider) findViewById(R.id.activity_month_2_slider);
		mMonthSlider3 = (Slider) findViewById(R.id.activity_month_3_slider);

		mMonthPer1 = (EditText) findViewById(R.id.incen_month_1_slider_per);
		mMonthPer2 = (EditText) findViewById(R.id.incen_month_2_slider_per);
		mMonthPer3 = (EditText) findViewById(R.id.incen_month_3_slider_per);

		mMidMonthCheckBox1 = (CheckBox) findViewById(R.id.incen_month_mid_1_switchView);
		mMidMonthCheckBox2 = (CheckBox) findViewById(R.id.incen_month_mid_2_switchView);
		mMidMonthCheckBox3 = (CheckBox) findViewById(R.id.incen_month_mid_3_switchView);

		mTotalMonth1 = (TextView) findViewById(R.id.incen_month_1_total_incen);
		mTotalMonth2 = (TextView) findViewById(R.id.incen_month_2_total_incen);
		mTotalMonth3 = (TextView) findViewById(R.id.incen_month_3_total_incen);

		mTotalMonth = (TextView) findViewById(R.id.incen_month_total_incen);
	}

	/**
	 * Intent : Get Intent Data : Which Quarter
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void getIntentData() {
		whichQuarter = Integer.parseInt(getIntent().getStringExtra(
				IncenDashBoardActivity.INTENT_QUARTER));
		mArrMonthName = new String[13];
		mArrTempMonthName = new String[12];
		mArrTempMonthName = getResources().getStringArray(R.array.month);
		for (int i = 0; i < mArrTempMonthName.length; i++) {
			mArrMonthName[i + 1] = mArrTempMonthName[i];
		}
	}

	/**
	 * Ui : Sets Accordion Header
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setAccordionHeader() {
		mAccordionMon1.toggleSection(0);
		mAccordionMonTotal.toggleSection(0);

		mAccordionMon1.setSectionHeaders(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter)]); // 1: 123, 2:456, 3:789 4:
		mAccordionMon2.setSectionHeaders(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter) + 1]);
		mAccordionMon3.setSectionHeaders(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter) + 2]);

		mAccordionMonTotal.setSectionHeaders("Total");
	}

	/**
	 * Ui : Sets Accordion Total Header
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setTextAccordingToQuarter() {
		mTotalMonthName1.setText("Total "
				+ mArrMonthName[Utilities.getCurrentMonth(whichQuarter)]
				+ " : ");
		mTotalMonthName2.setText("Total "
				+ mArrMonthName[Utilities.getCurrentMonth(whichQuarter) + 1]
				+ " : ");
		mTotalMonthName3.setText("Total "
				+ mArrMonthName[Utilities.getCurrentMonth(whichQuarter) + 2]
				+ " : ");
	}

	/**
	 * Ui : Sets Ui Listener
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setListener() {

		mMonthSlider1.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					if (value < 90) {
						mMonthPer1.setText("0");
					} else {
						mMonthPer1.setText(String.valueOf(value));
					}
					mMonthTotalValueArr[0] = String.valueOf(value);
					businessIncenLogic(false, 1);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mMonthSlider2.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					if (value < 90) {
						mMonthPer2.setText("0");
					} else {
						mMonthPer2.setText(String.valueOf(value));
					}
					mMonthTotalValueArr[1] = String.valueOf(value);
					businessIncenLogic(false, 2);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mMonthSlider3.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					if (value < 90) {
						mMonthPer3.setText("0");
					} else {
						mMonthPer3.setText(String.valueOf(value));
					}

					mMonthTotalValueArr[2] = String.valueOf(value);
					businessIncenLogic(false, 3);
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mMonthPer1.addTextChangedListener(new TextWatcher() {

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

		mMonthPer2.addTextChangedListener(new TextWatcher() {

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
				validateEditBox(mString.toString(), 2);
			}
		});

		mMonthPer3.addTextChangedListener(new TextWatcher() {

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
				validateEditBox(mString.toString(), 3);
			}
		});

		mMidMonthCheckBox1
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton button,
							boolean isChecked) {
						// TODO Auto-generated method stub
						// businessIncenLogic();
						businessIncenLogic(false, 1);
					}
				});
		mMidMonthCheckBox2
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						// businessIncenLogic();
						businessIncenLogic(false, 2);
					}
				});
		mMidMonthCheckBox3
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						// businessIncenLogic();
						businessIncenLogic(false, 3);
					}
				});
	}

	/**
	 * Validation : Validate Incentive Percentage
	 * 
	 * @param mString
	 * @param whichSlider
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	public void validateEditBox(String mString, int whichSlider) {
		try {
			if (!TextUtils.isEmpty(mString.toString())) {
				if (mString.toString().length() >= 2) {
					if (Integer.parseInt(mString.toString()) >= 90
							&& Integer.parseInt(mString.toString()) <= 110) {
						switch (whichSlider) {
						case 1:
							mMonthSlider1.setValue(Integer.parseInt(mString
									.toString()));
							businessIncenLogic(false, 1);
							break;
						case 2:
							mMonthSlider2.setValue(Integer.parseInt(mString
									.toString()));
							businessIncenLogic(false, 2);
							break;
						case 3:
							mMonthSlider3.setValue(Integer.parseInt(mString
									.toString()));
							businessIncenLogic(false, 3);
							break;
						}
						// businessIncenLogic();
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
							switch (whichSlider) {
							case 1:
								mMonthSlider1.setValue(89);
								// mMonthPer1.setText("90");
								// mMonthPer1.setText("0");
								businessIncenLogic(true, 1);
								break;
							case 2:
								mMonthSlider2.setValue(89);
								// mMonthPer2.setText("90");
								// mMonthPer2.setText("0");
								businessIncenLogic(true, 2);
								break;
							case 3:
								mMonthSlider3.setValue(89);
								// mMonthPer3.setText("90");
								// mMonthPer3.setText("0");
								businessIncenLogic(true, 3);
								break;
							}
						} else if (Integer.parseInt(mString) > 110) {

							switch (whichSlider) {
							case 1:
								mMonthSlider1.setValue(110);
								// mMonthPer1.setText("110");
								businessIncenLogic(false, 1);
								break;
							case 2:
								mMonthSlider2.setValue(110);
								// mMonthPer2.setText("110");
								businessIncenLogic(false, 2);
								break;
							case 3:
								mMonthSlider3.setValue(110);
								// mMonthPer3.setText("110");
								businessIncenLogic(false, 3);
								break;
							}
						} /*
						 * else if (Integer.parseInt(mString) > 9 &&
						 * Integer.parseInt(mString) <= 20) { switch
						 * (whichSlider) { case 1: mMonthSlider1.setValue(89);
						 * businessIncenLogic(true, 1); break; case 2:
						 * mMonthSlider2.setValue(89); businessIncenLogic(true,
						 * 2); break; case 3: mMonthSlider3.setValue(89);
						 * businessIncenLogic(true, 3); break; }
						 * 
						 * }
						 */

					}
				} else if (mString.toString().length() == 1) {
					if (Integer.parseInt(mString) == 0) {
						switch (whichSlider) {
						case 1:
							mMonthSlider1.setValue(89);
							businessIncenLogic(true, 1);
							break;
						case 2:
							mMonthSlider2.setValue(89);
							businessIncenLogic(true, 2);
							break;
						case 3:
							mMonthSlider3.setValue(89);
							businessIncenLogic(true, 3);
							break;
						}
					} else if (Integer.parseInt(mString) > 0
							&& Integer.parseInt(mString) <= 10) {
						switch (whichSlider) {
						case 1:
							mMonthSlider1.setValue(89);
							businessIncenLogic(true, 1);
							break;
						case 2:
							mMonthSlider2.setValue(89);
							businessIncenLogic(true, 2);
							break;
						case 3:
							mMonthSlider3.setValue(89);
							businessIncenLogic(true, 3);
							break;
						}
					}
				}
			} else {
				// if (mToast == null
				// || mToast.getView().getWindowVisibility() != View.VISIBLE) {
				// mToast.makeText(IncenMonthlyActivity.this,
				// "Please enter digits only!", Toast.LENGTH_SHORT)
				// .show();
				// }

				if (Integer.parseInt(mString) == 0) {
					switch (whichSlider) {
					case 1:
						mMonthSlider1.setValue(89);
						businessIncenLogic(true, 1);
						break;
					case 2:
						mMonthSlider2.setValue(89);
						businessIncenLogic(true, 2);
						break;
					case 3:
						mMonthSlider3.setValue(89);
						businessIncenLogic(true, 3);
						break;
					}
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	/**
	 * BusinessLogic : Apply Incentive Business Logic
	 * 
	 * @param isPointToZero
	 * @param month
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void businessIncenLogic(boolean isPointToZero, int month) {
		try {
			if (mMidMonthCheckBox1.isChecked()
					&& mMonthSlider1.getValue() >= 100 && !isPointToZero) {
				mTotalMonth1.setText(String.valueOf(mMidMonthBusinessLogic
						+ INCENMONTHMAP[mMonthSlider1.getValue() - 90]));
			} else {
				if (!isPointToZero && month == 1) {
					mTotalMonth1
							.setText(String.valueOf(INCENMONTHMAP[mMonthSlider1
									.getValue() - 90]));
				} else if (isPointToZero && month == 1) {
					mTotalMonth1.setText("0");
				}
			}

			if (mMidMonthCheckBox2.isChecked()
					&& mMonthSlider2.getValue() >= 100 && !isPointToZero) {
				mTotalMonth2.setText(String.valueOf(mMidMonthBusinessLogic
						+ INCENMONTHMAP[mMonthSlider2.getValue() - 90]));
			} else {
				if (!isPointToZero && month == 2) {
					mTotalMonth2
							.setText(String.valueOf(INCENMONTHMAP[mMonthSlider2
									.getValue() - 90]));
				} else if (isPointToZero && month == 2) {
					mTotalMonth2.setText("0");
				}
			}

			if (mMidMonthCheckBox3.isChecked()
					&& mMonthSlider3.getValue() >= 100 && !isPointToZero) {
				mTotalMonth3.setText(String.valueOf(mMidMonthBusinessLogic
						+ INCENMONTHMAP[mMonthSlider3.getValue() - 90]));
			} else {
				if (!isPointToZero && month == 3) {
					mTotalMonth3
							.setText(String.valueOf(INCENMONTHMAP[mMonthSlider3
									.getValue() - 90]));
				} else if (isPointToZero && month == 3) {
					mTotalMonth3.setText("0");
				}
			}

			if (isPointToZero) {
				if (mMonthSlider1.getValue() < 90) {
					mTotalMonth1.setText("0");
				}
				if (mMonthSlider2.getValue() < 90) {
					mTotalMonth2.setText("0");
				}
				if (mMonthSlider3.getValue() < 90) {
					mTotalMonth3.setText("0");
				}
			}

			mSliderValueArr[0] = String.valueOf(mMonthSlider1.getValue());
			mSliderValueArr[1] = String.valueOf(mMonthSlider2.getValue());
			mSliderValueArr[2] = String.valueOf(mMonthSlider3.getValue());

			mMonthTotalValueArr[0] = String.valueOf(mTotalMonth1.getText()
					.toString());
			mMonthTotalValueArr[1] = String.valueOf(mTotalMonth2.getText()
					.toString());
			mMonthTotalValueArr[2] = String.valueOf(mTotalMonth3.getText()
					.toString());

			mMonthCheckValueArr[0] = String.valueOf(mMidMonthCheckBox1
					.isChecked());
			mMonthCheckValueArr[1] = String.valueOf(mMidMonthCheckBox2
					.isChecked());
			mMonthCheckValueArr[2] = String.valueOf(mMidMonthCheckBox3
					.isChecked());

			mTotalMonth.setText(String.valueOf(Integer
					.parseInt(mMonthTotalValueArr[0])
					+ Integer.parseInt(mMonthTotalValueArr[1])
					+ Integer.parseInt(mMonthTotalValueArr[2])));

			saveMonthlyValues(whichQuarter);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	/**
	 * Preferences : Save values in Preferences
	 * 
	 * @param whichQuarter
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void saveMonthlyValues(int whichQuarter) {
		switch (whichQuarter) {
		case 1:
			ApplicationLoader.getPreferences().setQuarter1(mSliderValueArr);
			ApplicationLoader.getPreferences().setQuarterValue1(
					mMonthTotalValueArr);
			ApplicationLoader.getPreferences().setQuarterCheck1(
					mMonthCheckValueArr);
			ApplicationLoader.getPreferences().setQuarterTotal1(
					mTotalMonth.getText().toString());
			break;
		case 2:
			ApplicationLoader.getPreferences().setQuarter2(mSliderValueArr);
			ApplicationLoader.getPreferences().setQuarterValue2(
					mMonthTotalValueArr);
			ApplicationLoader.getPreferences().setQuarterCheck2(
					mMonthCheckValueArr);
			ApplicationLoader.getPreferences().setQuarterTotal2(
					mTotalMonth.getText().toString());
			break;
		case 3:
			ApplicationLoader.getPreferences().setQuarter3(mSliderValueArr);
			ApplicationLoader.getPreferences().setQuarterValue3(
					mMonthTotalValueArr);
			ApplicationLoader.getPreferences().setQuarterCheck3(
					mMonthCheckValueArr);
			ApplicationLoader.getPreferences().setQuarterTotal3(
					mTotalMonth.getText().toString());
			break;
		case 4:
			ApplicationLoader.getPreferences().setQuarter4(mSliderValueArr);
			ApplicationLoader.getPreferences().setQuarterValue4(
					mMonthTotalValueArr);
			ApplicationLoader.getPreferences().setQuarterCheck4(
					mMonthCheckValueArr);
			ApplicationLoader.getPreferences().setQuarterTotal4(
					mTotalMonth.getText().toString());
			break;
		}

	}

	/**
	 * Restore : Values from Preferences
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void restoreValueFromPreferences() {
		mMonthCheckValueArr = new String[] { "false", "false", "false" };
		mMonthTotalValueArr = new String[] { "0", "0", "0" };
		mSliderValueArr = new String[] { "89", "89", "89" };
		mPerValueArr = new String[] { "0", "0", "0" };
		mTotalArr = "0";

		switch (whichQuarter) {
		case 1:
			mMonthCheckValueArr = ApplicationLoader.getPreferences()
					.getQuarterCheck1();
			mMonthTotalValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue1();
			mSliderValueArr = ApplicationLoader.getPreferences().getQuarter1();
			mTotalArr = ApplicationLoader.getPreferences().getQuarterTotal1();
			break;
		case 2:
			mMonthCheckValueArr = ApplicationLoader.getPreferences()
					.getQuarterCheck2();
			mMonthTotalValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue2();
			mSliderValueArr = ApplicationLoader.getPreferences().getQuarter2();
			mTotalArr = ApplicationLoader.getPreferences().getQuarterTotal2();
			break;
		case 3:
			mMonthCheckValueArr = ApplicationLoader.getPreferences()
					.getQuarterCheck3();
			mMonthTotalValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue3();
			mSliderValueArr = ApplicationLoader.getPreferences().getQuarter3();
			mTotalArr = ApplicationLoader.getPreferences().getQuarterTotal3();
			break;
		case 4:
			mMonthCheckValueArr = ApplicationLoader.getPreferences()
					.getQuarterCheck4();
			mMonthTotalValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue4();
			mSliderValueArr = ApplicationLoader.getPreferences().getQuarter4();
			mTotalArr = ApplicationLoader.getPreferences().getQuarterTotal4();
			break;
		}
		checkValuesFromPreferencesNullOrNot();
		setValuesFromPreferences();
	}

	/**
	 * Restore : Check values retrieve from Preferences whether its null or not
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void checkValuesFromPreferencesNullOrNot() {
		for (int i = 0; i < 3; i++) {
			if (mMonthCheckValueArr[i] == null) {
				mMonthCheckValueArr[i] = "false";
			}
		}

		for (int i = 0; i < 3; i++) {
			if (mMonthTotalValueArr[i] == null) {
				mMonthTotalValueArr[i] = "0";
			}
		}

		for (int i = 0; i < 3; i++) {
			if (mSliderValueArr[i] == null) {
				mSliderValueArr[i] = "89";
			}
		}

		if (mTotalArr == null) {
			mTotalArr = "0";
		}

		try {
			for (int i = 0; i < 3; i++) {
				try {
					mPerValueArr[i] = mSliderValueArr[i];
					if (mSliderValueArr[i] == null
							|| Integer.parseInt(mSliderValueArr[i]) == 89) {
						mPerValueArr[i] = "0";
					}
				} catch (Exception e) {
					mPerValueArr[i] = "0";
				}
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}

	}

	/**
	 * Ui & Preferences : Sets values to Ui Elements
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setValuesFromPreferences() {
		mMonthPer1.setText(mPerValueArr[0]);
		mMonthPer2.setText(mPerValueArr[1]);
		mMonthPer3.setText(mPerValueArr[2]);

		mMonthSlider1.setValue(Integer.parseInt(mSliderValueArr[0]));
		mMonthSlider2.setValue(Integer.parseInt(mSliderValueArr[1]));
		mMonthSlider3.setValue(Integer.parseInt(mSliderValueArr[2]));

		mMidMonthCheckBox1.setChecked(Boolean.valueOf(mMonthCheckValueArr[0]));
		mMidMonthCheckBox2.setChecked(Boolean.valueOf(mMonthCheckValueArr[1]));
		mMidMonthCheckBox3.setChecked(Boolean.valueOf(mMonthCheckValueArr[2]));

		mTotalMonth1.setText(mMonthTotalValueArr[0]);
		mTotalMonth2.setText(mMonthTotalValueArr[1]);
		mTotalMonth3.setText(mMonthTotalValueArr[2]);

		mTotalMonth.setText(mTotalArr);
	}

	/**
	 * Ui : Sets Rupee Font
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mTotal1RsSy.setTypeface(mTypeFace);
		mTotal2RsSy.setTypeface(mTypeFace);
		mTotal3RsSy.setTypeface(mTypeFace);
		mTotalRsSy.setTypeface(mTypeFace);

		mTotal1RsSy.setText("`");
		mTotal2RsSy.setText("`");
		mTotal3RsSy.setText("`");
		mTotalRsSy.setText("`");
	}

	/**
	 * Security : Couldn't capture ScreenShot
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
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

	/**
	 * API : Get Base JSON
	 * @author Vikalp Patel(VikalpPatelCE)
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

	/**
	 * Parse : Parse JSON
	 * @param str
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	public void parseJSON(String str) {
		getMonthBase(str);
	}

	/**
	 * Parse : Parse JSON from Preferences
	 * @param str
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	public void getMonthBase(String str) {
		try {
			JSONObject mJSONObj = new JSONObject(str);
			JSONArray mJSONArray = mJSONObj.getJSONArray("month");

			mMidMonthBusinessLogic = Integer.parseInt(mJSONObj
					.getString("midmonth"));

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
				INCENMONTHMAP = new int[mSlab.length];
				System.arraycopy(mSlab, 0, INCENMONTHMAP, 0, mSlab.length);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Dialog : Show Dialog if no data found in Preferences
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenMonthlyActivity.this);

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
											IncenMonthlyActivity.this,
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

	/**
	 * Async Task : Get Incentive Base value from Preferences
	 * @author Vikalp Patel(VikalpPatelCE)
	 *
	 */
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
				mProgress = new ProgressDialog(IncenMonthlyActivity.this);
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
				JSONArray mJSONArray = mJSONObj.getJSONArray("month");
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
