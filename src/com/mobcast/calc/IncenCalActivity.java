/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- INCENTIVE TAB ACTIVITY
 * DESCRIPTION 		:- CONTAINS INCENTIVE FOUR TABS
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * 10001       VIKALP PATEL    01/01/2015       				CREATED
 * --------------------------------------------------------------------------------------------------------------------
 */
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

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.calc.IncenAnnualActivity.AsyncDownloadPdfDataFromApi;
import com.mobcast.calc.IncenAnnualActivity.AsyncDownloadPdfPathFromApi;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class IncenCalActivity extends TabActivity implements
		OnTabChangeListener {

	private TabHost mTabHost;
	private TabHost.TabSpec mTabMonthSpec;
	private TabHost.TabSpec mTabQuarterSpec;
	private TabHost.TabSpec mTabProductSpec;
	private TabHost.TabSpec mTabKPISpec;

	private ImageView mIncenOverFlow;
	private ImageView mIncenIcon;
	private ImageView mCoachMarks;

	private FrameLayout mLayout;
	private TextView mPopUpMenuSummary;
	private TextView mPopUpMenuPdf;

	private Intent mIntentMonth;
	private Intent mIntentQuarter;
	private Intent mIntentProduct;
	private Intent mIntentKPI;
	private Resources mResources;
	private Intent mIntent;
	private String whichQuarter;
	private int mCoachMarksCounter = 0;

	private Animation mAnimAlpha;

	private static final String TAG = IncenCalActivity.class.getSimpleName();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_incentive_calculator);
		setSecurity();
		getIntentData();
		initUi();
		setUiListener();
	}

	/**
	 * Get Intent Data : From Which Quarter it's been called
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void getIntentData() {
		mIntent = getIntent();
		whichQuarter = mIntent
				.getStringExtra(IncenDashBoardActivity.INTENT_QUARTER);
	}

	/**
	 * Initalize UI Elements
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void initUi() {
		mTabHost = getTabHost();

		mIncenOverFlow = (ImageView) findViewById(R.id.incen_overflow);
		mLayout = (FrameLayout) findViewById(R.id.mLayout);
		mIncenIcon = (ImageView) findViewById(R.id.titleBackIcon);
		mIntentMonth = new Intent(IncenCalActivity.this,
				IncenMonthlyActivity.class);
		mIntentMonth.putExtra(IncenDashBoardActivity.INTENT_QUARTER,
				whichQuarter);

		mIntentQuarter = new Intent(IncenCalActivity.this,
				IncenQuarterlyActivity.class);
		mIntentQuarter.putExtra(IncenDashBoardActivity.INTENT_QUARTER,
				whichQuarter);

		mIntentProduct = new Intent(IncenCalActivity.this,
				IncenProductActivity.class);
		mIntentProduct.putExtra(IncenDashBoardActivity.INTENT_QUARTER,
				whichQuarter);

		mIntentKPI = new Intent(IncenCalActivity.this, IncenKPIActivity.class);
		mIntentKPI
				.putExtra(IncenDashBoardActivity.INTENT_QUARTER, whichQuarter);
		mTabMonthSpec = mTabHost.newTabSpec("Monthly").setContent(mIntentMonth)
				.setIndicator(getIndicator("Monthly", 0));

		mTabQuarterSpec = mTabHost.newTabSpec("Quarterly")
				.setContent(mIntentQuarter)
				.setIndicator(getIndicator("Quarterly", 1));

		mTabProductSpec = mTabHost.newTabSpec("Product")
				.setContent(mIntentProduct)
				.setIndicator(getIndicator("Product", 2));

		mTabKPISpec = mTabHost.newTabSpec("KPI").setContent(mIntentKPI)
				.setIndicator(getIndicator("KPI", 3));

		mTabHost.addTab(mTabMonthSpec);
		mTabHost.addTab(mTabQuarterSpec);
		mTabHost.addTab(mTabProductSpec);
		mTabHost.addTab(mTabKPISpec);

		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(this);
		setCurrentMonthTab();

		mCoachMarks = (ImageView) findViewById(R.id.coachMarks);
		if (!ApplicationLoader.getPreferences().isCoachMarksFirstTime()) {
			mCoachMarks.setVisibility(View.VISIBLE);
			mAnimAlpha = AnimationUtils.loadAnimation(IncenCalActivity.this,
					R.anim.alpha);

			mAnimAlpha.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODOAuto-generated method stub
					mCoachMarks.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODOAuto-generated method stub
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODOAuto-generated method stub
					if (mCoachMarksCounter == 1) {
						mCoachMarks
								.setImageResource(R.drawable.incentive_coach_marks2);
					} else if (mCoachMarksCounter == 2) {
						mCoachMarks
								.setImageResource(R.drawable.incentive_coach_marks3);
					} else if (mCoachMarksCounter == 3) {
						mCoachMarks
								.setImageResource(R.drawable.incentive_coach_marks4);
					}
					mCoachMarks.setVisibility(View.VISIBLE);
				}
			});

			// ApplicationLoader.getPreferences().setCoachMarksFirstTime(true);
		}
	}

	/**
	 * Sets UI Listener
	 * 
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setUiListener() {
		mIncenOverFlow.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showPopUpMenu();
			}
		});

		mIncenIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mCoachMarks.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {

					/*
					 * if (mCoachMarksCounter == 0) { mCoachMarks
					 * .setImageResource(R.drawable.incentive_coach_marks2); //
					 * mCoachMarks.startAnimation(mAnimAlpha); } else if
					 * (mCoachMarksCounter == 1) { mCoachMarks
					 * .setImageResource(R.drawable.incentive_coach_marks3); //
					 * mCoachMarks.startAnimation(mAnimAlpha); } else if
					 * (mCoachMarksCounter == 2) { mCoachMarks
					 * .setImageResource(R.drawable.incentive_coach_marks4); //
					 * mCoachMarks.startAnimation(mAnimAlpha); } else {
					 * ApplicationLoader.getPreferences()
					 * .setCoachMarksFirstTime(true);
					 * mCoachMarks.setVisibility(View.GONE); mCoachMarks
					 * .setImageResource(R.drawable.incentive_coach_marks1); }
					 * mCoachMarksCounter++;
					 */

					if (mCoachMarksCounter >= 3) {
						ApplicationLoader.getPreferences()
								.setCoachMarksFirstTime(true);
						mCoachMarks.setVisibility(View.GONE);
						mCoachMarks
								.setImageResource(R.drawable.incentive_coach_marks1);
					} else {
						mCoachMarks.startAnimation(mAnimAlpha);
					}
					mCoachMarksCounter++;
				} catch (Exception e) {

				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	@Override
	public void onTabChanged(String mString) {
		// TODO Auto-generated method stub
		if (mString.equalsIgnoreCase("Monthly")) {
			setCurrentMonthTab();
		} else if (mString.equalsIgnoreCase("Quarterly")) {

			mTabHost.getTabWidget()
					.getChildAt(0)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_left_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(3)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_right_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(1)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_shape));
			mTabHost.getTabWidget()
					.getChildAt(2)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_white_shape));

			TextView mTextView0 = (TextView) mTabHost.getTabWidget()
					.getChildAt(0).findViewById(0);
			TextView mTextView1 = (TextView) mTabHost.getTabWidget()
					.getChildAt(1).findViewById(R.id.tabMiddle);
			TextView mTextView2 = (TextView) mTabHost.getTabWidget()
					.getChildAt(2).findViewById(R.id.tabMiddle2);
			TextView mTextView3 = (TextView) mTabHost.getTabWidget()
					.getChildAt(3).findViewById(3);

			mTextView0.setTextColor(Color.parseColor("#006999"));
			mTextView1.setTextColor(Color.WHITE);
			mTextView2.setTextColor(Color.parseColor("#006999"));
			mTextView3.setTextColor(Color.parseColor("#006999"));

		} else if (mString.equalsIgnoreCase("Product")) {

			mTabHost.getTabWidget()
					.getChildAt(0)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_left_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(3)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_right_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(1)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_white_shape));
			mTabHost.getTabWidget()
					.getChildAt(2)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_shape));

			TextView mTextView0 = (TextView) mTabHost.getTabWidget()
					.getChildAt(0).findViewById(0);
			TextView mTextView1 = (TextView) mTabHost.getTabWidget()
					.getChildAt(1).findViewById(R.id.tabMiddle);
			TextView mTextView2 = (TextView) mTabHost.getTabWidget()
					.getChildAt(2).findViewById(R.id.tabMiddle2);
			TextView mTextView3 = (TextView) mTabHost.getTabWidget()
					.getChildAt(3).findViewById(3);

			mTextView0.setTextColor(Color.parseColor("#006999"));
			mTextView2.setTextColor(Color.WHITE);
			mTextView1.setTextColor(Color.parseColor("#006999"));
			mTextView3.setTextColor(Color.parseColor("#006999"));

		} else {

			mTabHost.getTabWidget()
					.getChildAt(0)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_left_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(1)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(2)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_middle_white_shape));

			mTabHost.getTabWidget()
					.getChildAt(3)
					.setBackgroundDrawable(
							getResources().getDrawable(
									R.drawable.incen_tab_right_shape));

			TextView mTextView0 = (TextView) mTabHost.getTabWidget()
					.getChildAt(0).findViewById(0);
			TextView mTextView1 = (TextView) mTabHost.getTabWidget()
					.getChildAt(1).findViewById(R.id.tabMiddle);
			TextView mTextView2 = (TextView) mTabHost.getTabWidget()
					.getChildAt(2).findViewById(R.id.tabMiddle2);
			TextView mTextView3 = (TextView) mTabHost.getTabWidget()
					.getChildAt(3).findViewById(3);

			mTextView0.setTextColor(Color.parseColor("#006999"));
			mTextView1.setTextColor(Color.parseColor("#006999"));
			mTextView2.setTextColor(Color.parseColor("#006999"));
			mTextView3.setTextColor(Color.WHITE);
		}

	}

	/**
	 * Sets Tab Position
	 * 
	 * @param mString
	 *            : Tab Name
	 * @param position
	 *            : Tab Position
	 * @return
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private View getIndicator(String mString, int position) {
		LinearLayout mLinearLayout = new LinearLayout(IncenCalActivity.this);
		TextView mTextView = new TextView(IncenCalActivity.this);

		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setGravity(Gravity.CENTER);
		mLinearLayout.setClickable(true);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1);

		mTextView.setText(mString);
		mTextView.setGravity(Gravity.CENTER);
		mTextView.setTextColor(Color.parseColor("#006999"));
		mTextView.setClickable(false);
		mTextView.setLayoutParams(layoutParams);

		switch (position) {
		case 0:
			mTextView.setId(0);
			mLinearLayout.addView(mTextView);
			break;
		case 1:
			View mView = (View) LayoutInflater.from(IncenCalActivity.this)
					.inflate(R.layout.tab_middle, null, true);

			mView.setLayoutParams(layoutParams);
			mLinearLayout.addView(mView);
			break;
		case 2:
			View mView1 = (View) LayoutInflater.from(IncenCalActivity.this)
					.inflate(R.layout.tab_middle_2, null, true);

			mView1.setLayoutParams(layoutParams);
			mLinearLayout.addView(mView1);
			break;
		case 3:
			mTextView.setId(3);
			mLinearLayout.addView(mTextView);

			break;
		}

		return mLinearLayout;
	}

	/**
	 * Sets CurrentMonthTab
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void setCurrentMonthTab() {
		mTabHost.getTabWidget()
				.getChildAt(0)
				.setBackgroundDrawable(
						getResources().getDrawable(
								R.drawable.incen_tab_left_shape));

		mTabHost.getTabWidget()
				.getChildAt(1)
				.setBackgroundDrawable(
						getResources().getDrawable(
								R.drawable.incen_tab_middle_white_shape));

		mTabHost.getTabWidget()
				.getChildAt(2)
				.setBackgroundDrawable(
						getResources().getDrawable(
								R.drawable.incen_tab_middle_white_shape));

		mTabHost.getTabWidget()
				.getChildAt(3)
				.setBackgroundDrawable(
						getResources().getDrawable(
								R.drawable.incen_tab_right_white_shape));

		TextView mTextView0 = (TextView) mTabHost.getTabWidget().getChildAt(0)
				.findViewById(0);
		TextView mTextView1 = (TextView) mTabHost.getTabWidget().getChildAt(1)
				.findViewById(R.id.tabMiddle);
		TextView mTextView2 = (TextView) mTabHost.getTabWidget().getChildAt(2)
				.findViewById(R.id.tabMiddle2);
		TextView mTextView3 = (TextView) mTabHost.getTabWidget().getChildAt(3)
				.findViewById(3);

		mTextView0.setTextColor(Color.WHITE);
		mTextView1.setTextColor(Color.parseColor("#006999"));
		mTextView2.setTextColor(Color.parseColor("#006999"));
		mTextView3.setTextColor(Color.parseColor("#006999"));
	}

	/**
	 * Security : Couldn't Capture ScreenShot
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

	/**
	 * Show OverFlow Menu
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	@SuppressLint("NewApi")
	public void showPopUpMenu() {
		final Dialog dialog = new Dialog(IncenCalActivity.this);
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
		dialog.setContentView(R.layout.incen_pop_up_menu);

		mPopUpMenuSummary = (TextView) dialog
				.findViewById(R.id.pop_up_menu_incen_summary);
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

		mPopUpMenuSummary.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showSummary();
			}
		});

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

	/**
	 * Show Summary
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void showSummary() {
		Intent mIntent = new Intent(IncenCalActivity.this,
				IncenSummaryActivity.class);
		mIntent.putExtra(IncenDashBoardActivity.INTENT_QUARTER,
				String.valueOf(whichQuarter));
		startActivity(mIntent);
	}

	/**
	 * Show Incentive PDF
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void showPdf() {
		// File file1 = new File("/sdcard/incen.pdf");
		// if (!file1.exists()) {
		// copyPdfFromAssets();
		// }

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

	/**
	 * Copy Incentive Pdf from Assets Folder
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
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
			Toast.makeText(IncenCalActivity.this,
					"No Pdf Reader Application Found!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * Show Dialog : Open Incentive PDF
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void showOpenPdfDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenCalActivity.this);
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
									Toast.makeText(IncenCalActivity.this,
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

	/**
	 * Show Dialog : Download Incentive PDF
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenCalActivity.this);
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

	/**
	 * Download PDF From WebServices
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
	public void downloadPdfFromUrl() {
		if (Utilities.isInternetConnected()) {
			new AsyncDownloadPdfPathFromApi(true).execute();
		} else {
			Toast.makeText(IncenCalActivity.this,
					"Please check your internet connection!",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Aysnc Task : Download Incentive PDF Path from WebServices
	 * @author Vikalp Patel(VikalpPatelCE)
	 *
	 */
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
				mProgress = new ProgressDialog(IncenCalActivity.this);
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
				Toast.makeText(IncenCalActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Async Task : Download Incentive PDF from WebServices
	 * @author Vikalp Patel(VikalpPatelCE)
	 *
	 */
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
				mProgress = new ProgressDialog(IncenCalActivity.this);
				mProgress.setMessage("Downloading...");
				mProgress.setCanceledOnTouchOutside(false);
				mProgress.setCancelable(false);
				/*
				 * mProgress.setMax(100);
				 * mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				 * Drawable customDrawable = getResources().getDrawable(
				 * R.drawable.custom_progressbar);
				 * mProgress.setProgressDrawable(customDrawable);
				 */
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
				URL mURL = new URL(pdfPath.replace(" ", "%20"));
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
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				mFileOutputStream.write(baf.toByteArray());
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
					Toast.makeText(IncenCalActivity.this,
							"No PdfReader Application Found!",
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, e.toString());
				}
			} else {
				Toast.makeText(IncenCalActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Trigger Intent : Capable to Read PDF Files
	 * @param mName
	 * @author Vikalp Patel(VikalpPatelCE)
	 */
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
