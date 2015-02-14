/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- INCENTIVE DASHBOARD ACTIVITY
 * DESCRIPTION 		:- SHOWS INCENTIVE DASHBOARD SCREEN
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.mobcast.view.ButtonFlat;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

/**
 * @author Vikalp Patel
 * 
 */
public class IncenDashBoardActivity extends FragmentActivity {

	private ButtonFlat mQuarter1;
	private ButtonFlat mQuarter2;
	private ButtonFlat mQuarter3;
	private ButtonFlat mQuarter4;
	private ButtonFlat mAnnual;

	private ImageView mSummary1;
	private ImageView mSummary2;
	private ImageView mSummary3;
	private ImageView mSummary4;

	private ImageView mIncenOverFlow;
	private RelativeLayout mLayout;
	private TextView mPopUpMenuPdf;

	private static final String TAG = IncenDashBoardActivity.class
			.getSimpleName();

	public static final String INTENT_QUARTER = "whichQuarter";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_calc_dashboard);
		setSecurity();
		initUi();
		setUiListener();
		if (!BuildVars.debug) {
			fetchDataFromApi();
		}
	}

	/**
	 * Initialize UI Elements
	 * 
	 * @author Vikalp Patel
	 */
	private void initUi() {
		mQuarter1 = (ButtonFlat) findViewById(R.id.quarterBtn1);
		mQuarter2 = (ButtonFlat) findViewById(R.id.quarterBtn2);
		mQuarter3 = (ButtonFlat) findViewById(R.id.quarterBtn3);
		mQuarter4 = (ButtonFlat) findViewById(R.id.quarterBtn4);
		mAnnual = (ButtonFlat) findViewById(R.id.annualBtn);

		mSummary1 = (ImageView) findViewById(R.id.summary1);
		mSummary2 = (ImageView) findViewById(R.id.summary2);
		mSummary3 = (ImageView) findViewById(R.id.summary3);
		mSummary4 = (ImageView) findViewById(R.id.summary4);

		mIncenOverFlow = (ImageView) findViewById(R.id.incen_overflow);
		mLayout = (RelativeLayout) findViewById(R.id.mLayout);
	}

	/**
	 * Set Listener to UI Elements
	 * 
	 * @author Vikalp Patel
	 */
	private void setUiListener() {
		mQuarter1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenCalActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "1");
				startActivity(mIntent);
			}
		});

		mQuarter2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenCalActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "2");
				startActivity(mIntent);
			}
		});

		mQuarter3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenCalActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "3");
				startActivity(mIntent);
			}
		});

		mQuarter4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenCalActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "4");
				startActivity(mIntent);
			}
		});

		mAnnual.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenAnnualActivity.class);
				startActivity(mIntent);
			}
		});

		mSummary1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenSummaryActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "1");
				startActivity(mIntent);
			}
		});

		mSummary2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenSummaryActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "2");
				startActivity(mIntent);
			}
		});

		mSummary3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenSummaryActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "3");
				startActivity(mIntent);
			}
		});

		mSummary4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(IncenDashBoardActivity.this,
						IncenSummaryActivity.class);
				mIntent.putExtra(INTENT_QUARTER, "4");
				startActivity(mIntent);
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

	/**
	 * Pulls Data from WebService
	 */
	private void fetchDataFromApi() {
		if (!ApplicationLoader.getPreferences().isIncenFirstTime()) { // FIRST
																		// TIME
			if (Utilities.isInternetConnected()) {
				new AsyncDataFromApi(true).execute();
			} else {
				Toast.makeText(
						ApplicationLoader.getApplication()
								.getApplicationContext(),
						"Please check your internet connection!",
						Toast.LENGTH_SHORT).show();
				showAlertDialog();
			}
		} else {
			new AsyncDataFromApi(false).execute();
		}
	}

	/**
	 * @author Vikalp Patel Async Task to fetch data from WebServices :
	 *         Incentive Product
	 */
	public class AsyncDataFromApi extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isProductData = false;
		private boolean forceUser = false;
		private String strJSONData;

		public AsyncDataFromApi(boolean forceUser) {
			this.forceUser = forceUser;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (forceUser) {
				mProgress = new ProgressDialog(IncenDashBoardActivity.this);
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

			String str = RestClient
					.postData(Constants.INCEN_PRODUCT, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray mJSONArray = mJSONObj.getJSONArray("products");
				if (mJSONArray.length() > 0) {
					isProductData = true;
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

			if (isProductData) {
				ApplicationLoader.getPreferences().setProductJSON(strJSONData);
				// ApplicationLoader.getPreferences().setIncenFirstTime(false);

				if (!ApplicationLoader.getPreferences().isIncenFirstTime()) { // FIRST
																				// TIME
					if (Utilities.isInternetConnected()) {
						new AsyncBaseDataFromApi(true).execute();
					} else {
						Toast.makeText(
								ApplicationLoader.getApplication()
										.getApplicationContext(),
								"Please check your internet connection!",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new AsyncBaseDataFromApi(false).execute();
				}
			}
		}
	}

	/**
	 * @author Vikalp Patel Async Task : Fetch data from WebServices : Incentive
	 *         Base
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
				mProgress = new ProgressDialog(IncenDashBoardActivity.this);
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
		}
	}

	/**
	 * Show alert dialog if no incentive data found in Preferences
	 */
	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenDashBoardActivity.this);

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
									new AsyncDataFromApi(true).execute();
								} else {
									Toast.makeText(
											IncenDashBoardActivity.this,
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
	 * Security : Couldn't let you capture ScreenShot
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
	 * Show OverFlow Menu : Incentive Scheme
	 */
	@SuppressLint("NewApi")
	public void showPopUpMenu() {
		final Dialog dialog = new Dialog(IncenDashBoardActivity.this);
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

	/**
	 * Show Pdf of Incentive From OverFlow Menu
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
	 * Copy Pdf from Assests Folder
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
			Toast.makeText(IncenDashBoardActivity.this,
					"No Pdf Reader Application Found!", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * @param in
	 *            : InputStream
	 * @param out
	 *            : OutPutStream
	 * @throws IOException
	 *             Copy File
	 */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	/**
	 * Show Open Pdf Dialog, If PDF is already downloaded.
	 */
	private void showOpenPdfDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenDashBoardActivity.this);
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
									Toast.makeText(IncenDashBoardActivity.this,
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
	 * Show PDF Download Dialog
	 */
	private void showDownloadDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenDashBoardActivity.this);
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
	 * Fetch PDF Path from WebServices
	 */
	public void downloadPdfFromUrl() {
		if (Utilities.isInternetConnected()) {
			new AsyncDownloadPdfPathFromApi(true).execute();
		} else {
			Toast.makeText(IncenDashBoardActivity.this,
					"Please check your internet connection!",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * @author Vikalp Patel Async Task : Fetch PDF Path from WebServices
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
				mProgress = new ProgressDialog(IncenDashBoardActivity.this);
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
				Toast.makeText(IncenDashBoardActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * <i>Async Task : Downloads Pdf from Path</i>
	 * 
	 * @author Vikalp Patel
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
				mProgress = new ProgressDialog(IncenDashBoardActivity.this);
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
					Toast.makeText(IncenDashBoardActivity.this,
							"No PdfReader Application Found!",
							Toast.LENGTH_SHORT).show();
					Log.i(TAG, e.toString());
				}
			} else {
				Toast.makeText(IncenDashBoardActivity.this,
						"Incentive Scheme Pdf not available!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * <i>Intent Pdf capable application</i>
	 * 
	 * @param mName
	 *            : File Name
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
	 * Flurry Analytics (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
