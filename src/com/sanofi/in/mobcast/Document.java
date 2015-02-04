package com.sanofi.in.mobcast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

public class Document extends Activity {

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		File file1 = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ Constants.APP_FOLDER_TEMP);
		DeleteRecursive(file1);
//		if(isToRate){
//			showRateDialog();
//			isToRate = false;
//		}
	}

	ImageView im;
	TextView maintitle, atitle, doctime, docby, summarytv, det;
	Button btn, share;
	String mtitle, title, time, by, summary, type, name, ename, aid;
	int _id;
	static ProgressDialog pDialog;
	ScrollView sv;
	Reports reports;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
//	private boolean isToRate = false;
	
	HashMap<String, String> params = new HashMap<String, String>();
	
	private static final String TAG = Document.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.train);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		reports = new Reports(getApplicationContext(), "Training");
		maintitle = (TextView) findViewById(R.id.maintitle);
		det = (TextView) findViewById(R.id.tvDetails);
		det.setOnTouchListener(otl);
		atitle = (TextView) findViewById(R.id.tvATtitle);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		share = (Button) findViewById(R.id.iv6);
		sv.setOnTouchListener(otl);
		atitle.setOnTouchListener(otl);
		doctime = (TextView) findViewById(R.id.tvDoctime);
		doctime.setOnTouchListener(otl);
		// docby = (TextView) findViewById(R.id.tvDocBy);
		summarytv = (TextView) findViewById(R.id.tvATsummary);
		summarytv.setOnTouchListener(otl);
		im = (ImageView) findViewById(R.id.iconimage);
		btn = (Button) findViewById(R.id.BtnOpenFile);
		btn.setOnTouchListener(myhandler2);
		onNewIntent(getIntent());
		btn.setOnClickListener(myhandler1);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reports.updateShare(aid + "");
				AnnounceDBAdapter adb = new AnnounceDBAdapter(Document.this);
				adb.open();
				String link = adb.getlink("training", _id + "");
				adb.close();

				Intent sharingIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				sharingIntent
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
				// sharingIntent.setType("message/rfc822");
				String shareBody = title + "\n" + summary + "\n" + link;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title);
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));

			}
		});
	}

	OnTouchListener otl = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				historicX = event.getX();
				historicY = event.getY();
				break;

			case MotionEvent.ACTION_UP:
				if (event.getX() - historicX < -DELTA) {
					// FunctionDeleteRowWhenSlidingLeft();
					return true;
				} else if (event.getX() - historicX > DELTA) {
					// FunctionDeleteRowWhenSlidingRight();
					return true;
				}
				break;
			default:
				return false;
			}
			return false;
		}
	};

	void FunctionDeleteRowWhenSlidingRight() {
		Toast.makeText(getApplicationContext(), "right", Toast.LENGTH_SHORT)
				.show();

		/*
		 * AnnounceDBAdapter announce = new AnnounceDBAdapter(Document.this);
		 * announce.open(); Cursor cursor =
		 * announce.getRowPriviousDocuments(_id);
		 * Log.v("Right slide cursor has ", cursor.getCount()+"");
		 * cursor.moveToFirst();
		 * 
		 * String title =
		 * cursor.getString(cursor.getColumnIndexOrThrow("title")); String
		 * summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
		 * 
		 * Log.v("TITLE", title); Log.v("Summary", summary); itemView(
		 * cursor,1);
		 * 
		 * announce.close();
		 */

	}

	void FunctionDeleteRowWhenSlidingLeft() {
		Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT)
				.show();
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			// Toast.makeText(getBaseContext(), "clicked",
			// Toast.LENGTH_SHORT).show();
			try {
//				isToRate = true;
				if (type.equals("pdf"))
				{
					
					
					/*
					 * Log.v("training", "in pdf"); Intent show;
					 * Log.v("training", "symbol set with"); File file = new
					 * File
					 * (Environment.getExternalStorageDirectory().getAbsolutePath
					 * () + "/.mobcast/"+getString(R.string.pdf)+"/"+name);
					 * 
					 * show = new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file),
					 * "application/pdf"); startActivity(show);
					 */

					File file = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER
							+ getString(R.string.pdf)
							+ "/"
							+ ename);
					if (file.exists())
						Log.d("file", "exists");
					else
						Log.e("file", "does not exists");
					double bytes = file.length() / 500;
					int time = (int) bytes;
					Log.e("file length", time + "");
					// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
					File folder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+Constants.APP_FOLDER_TEMP);
					folder.mkdirs();
					File file1 = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP + name);
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

					SystemClock.sleep(time);

					Intent show = new Intent(Intent.ACTION_VIEW);
					show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					show.setAction(Intent.ACTION_VIEW);
					show.setDataAndType(Uri.fromFile(file1), "application/pdf");
					startActivity(show);
					
//					SA VIKALP
//					Intent show = new Intent(Document.this, PdfViewerMobCastActivity.class);
//					show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
//					show.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, file1.getPath());
//					startActivity(show);
//					EA VIKALP
					
				}// end of pdf

				if (type.equals("ppt"))

				{

					/*
					 * Intent show;
					 * 
					 * File file = new
					 * File(Environment.getExternalStorageDirectory
					 * ().getAbsolutePath() +
					 * "/.mobcast/"+getString(R.string.ppt)+"/"+name);
					 * 
					 * show = new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file),
					 * "application/vnd.ms-powerpoint"); startActivity(show);
					 */

					// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
					// AsyncTaskRunner runner = new AsyncTaskRunner();
					// runner.execute("ppt");

					// TODO
					File file = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER
							+ getString(R.string.ppt)
							+ "/"
							+ ename);
					if (file.exists())
						Log.d("file", "exists");
					else
						Log.e("file", "does not exists");
					double bytes = file.length() / 500;
					int time = (int) bytes;
					Log.e("file length", time + "");
					// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
					File folder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP);
					folder.mkdirs();
					File file1 = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP + name);
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

					SystemClock.sleep(time);

					Intent show = new Intent(Intent.ACTION_VIEW);
					show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					show.setAction(Intent.ACTION_VIEW);
					show.setDataAndType(Uri.fromFile(file1),
							"application/vnd.ms-powerpoint");
					startActivity(show);

				}// end of ppt

				if (type.equals("doc")) {

					/*
					 * Intent show;
					 * 
					 * File file = new
					 * File(Environment.getExternalStorageDirectory
					 * ().getAbsolutePath() +
					 * "/.mobcast/"+getString(R.string.doc)+"/"+name); show =
					 * new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file),
					 * "application/msword"); startActivity(show);
					 */

					File file = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER
							+ getString(R.string.doc)
							+ "/"
							+ ename);
					if (file.exists())
						Log.d("file", "exists");
					else
						Log.e("file", "does not exists");
					double bytes = file.length() / 500;
					int time = (int) bytes;
					Log.e("file length", time + "");
					// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
					File folder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP);
					folder.mkdirs();
					File file1 = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP + name);
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

					SystemClock.sleep(time);

					Intent show = new Intent(Intent.ACTION_VIEW);
					show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					show.setAction(Intent.ACTION_VIEW);
					show.setDataAndType(Uri.fromFile(file1),
							"application/msword");
					startActivity(show);

				}// end of doc

				if (type.equals("xls")) {

					/*
					 * Intent show;
					 * 
					 * File file = new
					 * File(Environment.getExternalStorageDirectory
					 * ().getAbsolutePath() +
					 * "/.mobcast/"+getString(R.string.xls)+"/"+name); show =
					 * new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file),
					 * "application/vnd.ms-excel"); startActivity(show);
					 */

					File file = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER
							+ getString(R.string.xls)
							+ "/"
							+ ename);
					if (file.exists())
						Log.d("file", "exists");
					else
						Log.e("file", "does not exists");
					double bytes = file.length() / 500;
					int time = (int) bytes;
					Log.e("file length", time + "");
					// file.renameTo(new File(file.getAbsoluteFile()+".pdf"));
					File folder = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP);
					folder.mkdirs();
					File file1 = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ Constants.APP_FOLDER_TEMP + name);
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

					SystemClock.sleep(time);

					Intent show = new Intent(Intent.ACTION_VIEW);
					show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					show.setAction(Intent.ACTION_VIEW);
					show.setDataAndType(Uri.fromFile(file1),
							"application/vnd.ms-excel");
					startActivity(show);

				}// end of xls
			} catch (Exception e) {
			}

		}
	};

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.gradient2);
				int padding_in_dp = 5; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.button_gradient);

				int padding_in_dp = 5; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);
			}

			return false;
		}

	};

	@Override
	public void onNewIntent(Intent intent) {

		mtitle = intent.getStringExtra("mtitle");
		title = intent.getStringExtra("title");
		time = intent.getStringExtra("detail");
		summary = intent.getStringExtra("summary");
		aid = intent.getStringExtra("id");
		type = intent.getStringExtra("type");
		name = intent.getStringExtra("name");
		String sharing = intent.getStringExtra("shareKey");
		ename = intent.getStringExtra("ename");
		Log.d("ename", ename);
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		maintitle.setText(mtitle);
		if (mtitle.equals("Documents"))
			im.setImageResource(R.drawable.icons26);
		atitle.setText(title);
		vdate v = new vdate(time);
		doctime.setText(v.getRDate());
		summarytv.setText(summary);

		if (sharing.contains("off"))
			share.setVisibility(Button.GONE);
		/*
		 * ImageView im; TextView maintitle, atitle, doctime, docby, summarytv;
		 */
		// Toast.makeText(getBaseContext(), "open", Toast.LENGTH_SHORT).show();
		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "Training");
		announce.close();
		reports.updateRead(aid);

		File file = null;
		if (type.contentEquals("pdf")) {
			file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ getString(R.string.pdf)
					+ "/" + ename);
		}

		else if (type.contentEquals("ppt")) {
			file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ getString(R.string.ppt)
					+ "/" + ename);
		} else if (type.contentEquals("doc")) {
			file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ getString(R.string.doc)
					+ "/" + ename);
		} else if (type.contentEquals("xls")) {
			file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ Constants.APP_FOLDER
					+ getString(R.string.xls)
					+ "/" + ename);
		}
		
		if(!file.exists()){
			Download d = new Download(Document.this,
					AnnounceDBAdapter.SQLITE_TRAINING, _id + "");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					//Toast.makeText(getApplicationContext(), "Download Ready", Toast.LENGTH_SHORT).show();

				}
			});
		}
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
	}
	
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	
	public void showRateDialog()
	{
		final Dialog dialog = new Dialog(Document.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.training_pop_up_rate);
		
		final RatingBar mRating1 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating1);
		final RatingBar mRating2 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating2);
		final RatingBar mRating3 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating3);
		final RatingBar mRating4 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating4);
		TextView mCancel = (TextView)dialog.findViewById(R.id.training_pop_up_rate_cancel);
		TextView mSubmit = (TextView)dialog.findViewById(R.id.training_pop_up_rate_submit);

		mCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			dialog.dismiss();	
			}
		});
		
		mSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				sendRateToAPI(mRating1, mRating2, mRating3, mRating4);
			}
		});
		
		dialog.show();
	}
	
	
	private void sendRateToAPI(RatingBar r1, RatingBar r2,RatingBar r3, RatingBar r4) {
		params.put("rating1", String.valueOf(r1.getRating()));
		params.put("rating2", String.valueOf(r2.getRating()));
		params.put("rating3", String.valueOf(r3.getRating()));
		params.put("rating4", String.valueOf(r4.getRating()));
		params.put("trainingID", aid);
		params.put("device", "android");
		params.put("mobileNumber", ApplicationLoader.getPreferences().getMobileNumber());

		// post request to server

		Log.d("device", params.get("device").toString());
		ConnectionDetector cd = new ConnectionDetector(Document.this);
		Boolean isInternetPresent = cd.isConnectingToInternet(); //
		if (isInternetPresent
				&& params.get("device").equalsIgnoreCase("android")) {
			SendRating checkLoginTask = new SendRating(params, Document.this);
			checkLoginTask.execute(com.mobcast.util.Constants.TRAIN_RATE);
		}
	}
	
	
	class SendRating extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;
		String str = "";

		/**
		 * constructor
		 */

		public SendRating(HashMap<String, String> data) {
			mData = data;
		}

		public SendRating(HashMap<String, String> data, Activity activity) {

			this.activity = activity;
			context = activity;
			pDialog = new ProgressDialog(context);
			mData = data;
		}

		/**
		 * background
		 */

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (context != null) {
				pDialog = new ProgressDialog(context);
				pDialog.setMessage(Html.fromHtml("Please Wait..."));
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			byte[] result = null;

			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(params[0]);// in this case, params[0]
													// is
													// URL
			try {
				// set up post data
				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				Iterator<String> it = mData.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					nameValuePair.add(new BasicNameValuePair(key, mData
							.get(key)));
				}

				post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
				HttpResponse response = client.execute(post);

				StringBuilder bodyBuilder = new StringBuilder();
				Iterator<Entry<String, String>> iterator = mData.entrySet()
						.iterator();
				// constructs the POST body using the parameters
				while (iterator.hasNext()) {
					Entry<String, String> param = iterator.next();
					bodyBuilder.append(param.getKey()).append('=')
							.append(param.getValue());
					if (iterator.hasNext()) {
						bodyBuilder.append('&');
					}
				}
				String body = bodyBuilder.toString();
				Log.v("AsyncPost", "Posting '" + body + "' to " + params[0]);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
					result = EntityUtils.toByteArray(response.getEntity());
					str = new String(result, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (Exception e) {
			}
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				pDialog.dismiss();
				pDialog = null;
			} catch (Exception e) {
				// nothing
			}

			Log.d("SendRating result", str);

			JSONObject jObject;
			try {
				jObject = new JSONObject(str);

				String exists = jObject.getString("updated");
				// if(exists.contains("true")){verifywithoutsms();}
				if (exists.contains("true")) {
					Log.i(TAG, "updated");
				} else {
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}
	 
	@Override
	protected void onStop()
	{
		super.onStop();		
		FlurryAgent.onEndSession(this);
	}
}
