/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- INCENTIVE SUMMARY ACTIVITY
 * DESCRIPTION 		:- SHOWS INCENTIVE SUMMARY SCREEN
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001       VIKALP PATEL    01/01/2015       				CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.mobcast.calc;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;
import com.mobcast.view.AccordionView;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class IncenSummaryActivity extends FragmentActivity {

	private AccordionView mAccordionView;

	private TextView mMonthLabel1;
	private TextView mMonthLabel2;
	private TextView mMonthLabel3;

	private TextView mMonthTotal1;
	private TextView mMonthTotal2;
	private TextView mMonthTotal3;

	private TextView mMonthRsSy1;
	private TextView mMonthRsSy2;
	private TextView mMonthRsSy3;

	private TextView mQuarterLabel;
	private TextView mQuarterTotal;
	private TextView mQuarterRsSy;

	private TextView mKPILabel;
	private TextView mKPITotal;
	private TextView mKPITotalRsSy;

	private TextView mProductLabel;
	private TextView mProductTotal;
	private TextView mProductTotalRsSy;

	private TextView mTotalLabel;
	private TextView mTotal;
	private TextView mTotalRsSy;

	private ImageView mImageViewBack;

	private Typeface mTypeFace;

	private int whichQuarter;
	private String[] mArrMonthName;
	private String[] mArrTempMonthName;

	private String[] mMonthValueArr;

	private String mMonthValueArr1;
	private String mMonthValueArr2;
	private String mMonthValueArr3;

	private String mQuarterValueArr;
	private String mKPIValueArr;
	private String mProductValueArr;

	private String mTotalValueArr;

	private static final String TAG = IncenSummaryActivity.class
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
		setContentView(R.layout.incen_calc_summary);
		setSecurity();
		initUi();
		getIntentData();
		setTextAccordingToQuarter();
		setRupeeFont();
		setAccordionHeader();
		restoreValueFromPreferences();
		setUiListener();
	}

	/**
	 * Ui : Initialize Ui Elements
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void initUi() {
		mAccordionView = (AccordionView) findViewById(R.id.incen_summary_acc_view);

		mMonthLabel1 = (TextView) findViewById(R.id.incen_summary_month1);
		mMonthLabel2 = (TextView) findViewById(R.id.incen_summary_month2);
		mMonthLabel3 = (TextView) findViewById(R.id.incen_summary_month3);

		mMonthTotal1 = (TextView) findViewById(R.id.incen_summary_month1_total);
		mMonthTotal2 = (TextView) findViewById(R.id.incen_summary_month2_total);
		mMonthTotal3 = (TextView) findViewById(R.id.incen_summary_month3_total);

		mMonthRsSy1 = (TextView) findViewById(R.id.incen_summary_month1_total_rs_sy);
		mMonthRsSy2 = (TextView) findViewById(R.id.incen_summary_month2_total_rs_sy);
		mMonthRsSy3 = (TextView) findViewById(R.id.incen_summary_month3_total_rs_sy);

		mQuarterLabel = (TextView) findViewById(R.id.incen_summary_quarter);
		mQuarterRsSy = (TextView) findViewById(R.id.incen_summary_quarter_total_rs_sy);
		mQuarterTotal = (TextView) findViewById(R.id.incen_summary_quarter_total);

		mKPILabel = (TextView) findViewById(R.id.incen_summary_kpi);
		mKPITotal = (TextView) findViewById(R.id.incen_summary_kpi_total);
		mKPITotalRsSy = (TextView) findViewById(R.id.incen_summary_kpi_total_rs_sy);

		mProductLabel = (TextView) findViewById(R.id.incen_summary_product);
		mProductTotal = (TextView) findViewById(R.id.incen_summary_product_total);
		mProductTotalRsSy = (TextView) findViewById(R.id.incen_summary_product_total_rs_sy);

		mTotal = (TextView) findViewById(R.id.incen_summary_earning);
		mTotalLabel = (TextView) findViewById(R.id.incen_summary_earning_total);
		mTotalRsSy = (TextView) findViewById(R.id.incen_summary_earning_total_rs_sy);

		mImageViewBack = (ImageView) findViewById(R.id.titleBackIcon);
	}

	/**
	 * Intent : Get Intent Data
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
	 * Ui : Sets Ui Listener
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setUiListener() {
		mImageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	/**
	 * Ui : Sets Header of AccordionView
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setTextAccordingToQuarter() {
		mMonthLabel1.setText(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter)]);
		mMonthLabel2.setText(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter) + 1]);
		mMonthLabel3.setText(mArrMonthName[Utilities
				.getCurrentMonth(whichQuarter) + 2]);
	}

	/**
	 * Ui: Sets Header of AccordionView
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setAccordionHeader() {
		mAccordionView.setSectionHeaders("Summary Quarter " + whichQuarter);
		mAccordionView.toggleSection(0);
	}

	/**
	 * Ui : Sets Rupee Font
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mMonthRsSy1.setTypeface(mTypeFace);
		mMonthRsSy2.setTypeface(mTypeFace);
		mMonthRsSy3.setTypeface(mTypeFace);
		mQuarterRsSy.setTypeface(mTypeFace);
		mKPITotalRsSy.setTypeface(mTypeFace);
		mProductTotalRsSy.setTypeface(mTypeFace);
		mTotalRsSy.setTypeface(mTypeFace);

		mMonthRsSy1.setText("`");
		mMonthRsSy2.setText("`");
		mMonthRsSy3.setText("`");
		mQuarterRsSy.setText("`");
		mKPITotalRsSy.setText("`");
		mProductTotalRsSy.setText("`");
		mTotalRsSy.setText("`");
	}

	/**
	 * Restore : Restore Value from Preferences
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void restoreValueFromPreferences() {
		switch (whichQuarter) {
		case 1:
			mMonthValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue1();
			mMonthValueArr1 = mMonthValueArr[0];
			mMonthValueArr2 = mMonthValueArr[1];
			mMonthValueArr3 = mMonthValueArr[2];

			mQuarterValueArr = ApplicationLoader.getPreferences().getQValue1();
			mProductValueArr = ApplicationLoader.getPreferences()
					.getProductTotalValue1();
			mKPIValueArr = ApplicationLoader.getPreferences()
					.getKPITotalValue1();
			mTotalValueArr = ApplicationLoader.getPreferences()
					.getSummarValue1();
			break;
		case 2:
			mMonthValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue2();
			mMonthValueArr1 = mMonthValueArr[0];
			mMonthValueArr2 = mMonthValueArr[1];
			mMonthValueArr3 = mMonthValueArr[2];

			mQuarterValueArr = ApplicationLoader.getPreferences().getQValue2();
			mProductValueArr = ApplicationLoader.getPreferences()
					.getProductTotalValue2();
			mKPIValueArr = ApplicationLoader.getPreferences()
					.getKPITotalValue2();
			mTotalValueArr = ApplicationLoader.getPreferences()
					.getSummarValue1();

			break;
		case 3:
			mMonthValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue3();
			mMonthValueArr1 = mMonthValueArr[0];
			mMonthValueArr2 = mMonthValueArr[1];
			mMonthValueArr3 = mMonthValueArr[2];

			mQuarterValueArr = ApplicationLoader.getPreferences().getQValue3();
			mProductValueArr = ApplicationLoader.getPreferences()
					.getProductTotalValue3();
			mKPIValueArr = ApplicationLoader.getPreferences()
					.getKPITotalValue3();
			mTotalValueArr = ApplicationLoader.getPreferences()
					.getSummarValue1();
			break;
		case 4:
			mMonthValueArr = ApplicationLoader.getPreferences()
					.getQuarterValue4();
			mMonthValueArr1 = mMonthValueArr[0];
			mMonthValueArr2 = mMonthValueArr[1];
			mMonthValueArr3 = mMonthValueArr[2];

			mQuarterValueArr = ApplicationLoader.getPreferences().getQValue4();
			mProductValueArr = ApplicationLoader.getPreferences()
					.getProductTotalValue4();
			mKPIValueArr = ApplicationLoader.getPreferences()
					.getKPITotalValue4();
			mTotalValueArr = ApplicationLoader.getPreferences()
					.getSummarValue1();
			break;
		}
		checkValuesFromPreferencesNullOrNot();
		setValuesFromPreferences();
	}

	/**
	 * Restore : Check whether values retrieved from Preferences are null or
	 * not.
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void checkValuesFromPreferencesNullOrNot() {
		if (mMonthValueArr1 == null)
			mMonthValueArr1 = "0";

		if (mMonthValueArr2 == null)
			mMonthValueArr2 = "0";

		if (mMonthValueArr3 == null)
			mMonthValueArr3 = "0";

		if (mQuarterValueArr == null)
			mQuarterValueArr = "0";

		if (mKPIValueArr == null)
			mKPIValueArr = "0";

		if (mProductValueArr == null)
			mProductValueArr = "0";

		mTotalValueArr = String.valueOf(Integer.parseInt(mMonthValueArr1)
				+ Integer.parseInt(mMonthValueArr2)
				+ Integer.parseInt(mMonthValueArr3)
				+ Integer.parseInt(mKPIValueArr)
				+ Integer.parseInt(mProductValueArr)
				+ Integer.parseInt(mQuarterValueArr));
	}

	/**
	 * Preferences & Ui : Set Value to Ui Elements from Preferences
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setValuesFromPreferences() {
		mMonthTotal1.setText(mMonthValueArr1);
		mMonthTotal2.setText(mMonthValueArr2);
		mMonthTotal3.setText(mMonthValueArr3);

		mQuarterTotal.setText(mQuarterValueArr);
		mProductTotal.setText(mProductValueArr);
		mKPITotal.setText(mKPIValueArr);
		mTotalLabel.setText(mTotalValueArr);

		switch (whichQuarter) {
		case 1:
			ApplicationLoader.getPreferences().setSummaryValue1(mTotalValueArr);
			break;
		case 2:
			ApplicationLoader.getPreferences().setSummaryValue2(mTotalValueArr);
			break;
		case 3:
			ApplicationLoader.getPreferences().setSummaryValue3(mTotalValueArr);
			break;
		case 4:
			ApplicationLoader.getPreferences().setSummaryValue4(mTotalValueArr);
			break;
		}
	}

	/**
	 * Security : Stop capture screenshots
	 * 
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
