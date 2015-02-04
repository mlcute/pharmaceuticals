package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DeleteFile;
import com.mobcast.util.Utilities;

public class TrainingListView extends Activity {

	private AnnounceDBAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private TrainingAdapter dataAdapter;
	TextView detail;
	ListView listView;
	ImageView symbol;
	ImageButton search, sample_refresh;
	String response;
	TextView nomobcast;
	AnnounceDBAdapter announce;
	RecruitTask asyncHttpPost;
	String contentExpiry = "9999-99-99";
	static ProgressDialog pDialog;
	
	private static final String TAG = TrainingListView.class.getSimpleName(); 

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Toast.makeText(getApplicationContext(), "received",
			// Toast.LENGTH_SHORT);
			onPause();
			onResume();

		}
	};

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		/*unregisterReceiver(receiver);*/
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
		getPreferences(MODE_PRIVATE)
				.edit()
				.putString("listScroll",
						listView.getFirstVisiblePosition() + "").commit();
		Log.v("temp on pause", listView.getFirstVisiblePosition() + "");
		super.onPause();
		if ((pDialog != null) && pDialog.isShowing())
	        pDialog.dismiss();
	    pDialog = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trainlist);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		search = (ImageButton) findViewById(R.id.sample_button);
		search.setOnTouchListener(myhandler2);
		nomobcast = (TextView) findViewById(R.id.noNew);
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(TrainingListView.this, Search.class);
				i.putExtra("tablename", "Training");
				startActivity(i);

			}
		});

		sample_refresh.setOnTouchListener(myhandler2);
		sample_refresh.setOnClickListener(myhandler11);
		announce = new AnnounceDBAdapter(this);
		announce.open();
		if (announce.fetchAllTraining().getCount() < 1) {
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());

			Boolean isInternetPresent = cd.isConnectingToInternet(); //
			if (isInternetPresent) {
				myhandler11.onClick(sample_refresh);
			}
		}
		announce.close();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if((pDialog!=null)&&(pDialog.isShowing()))
		{pDialog.dismiss();
			pDialog=null;
		}
		super.onDestroy();
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

	View.OnClickListener myhandler11 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(), "refreshList",
			// Toast.LENGTH_SHORT).show();
			RotateAnimation ranim = (RotateAnimation)AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotateanim);
			sample_refresh.startAnimation(ranim);
//			SA VIKALP
			try {
				refreshData();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			EA VIKALP
			if(listView!=null && listView.getCount()<1){
				dbHelper = new AnnounceDBAdapter(getApplicationContext());
				dbHelper.open();
				displayListView();
				dbHelper.close();
			}
		}
	};
	
	private void refreshData(){

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("mName", "training");
		params.put("companyID",
				getResources().getString(R.string.companyID));

		SharedPreferences pref;
		pref = getSharedPreferences("MobCastPref", 0);
		String temp1 = pref.getString("name", "akshay@bombil.com");

		// params.put(com.mobcast.util.Constants.user_id, temp1);
		params.put(com.mobcast.util.Constants.user_id, temp1);
		params.put("fID", pref.getString("lastTraining", "0"));
		UpdateFeeds asyncHttpPost = new UpdateFeeds(params);
		asyncHttpPost.execute(com.mobcast.util.Constants.REFRESH_FEEDS);

		// response = response.replace("[", "");
		// response = response.replace("]", "");
	}

	protected void onResume() {
		
		File file1 = new File(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ Constants.APP_FOLDER_TEMP);
		DeleteRecursive(file1);

		dbHelper = new AnnounceDBAdapter(this);
		dbHelper.open();
		displayListView();
		dbHelper.close();

		int temp = Integer.parseInt((getPreferences(MODE_PRIVATE).getString(
				"listScroll", 0 + "")));
		Log.v("temp on resume", temp + "");
		listView.setSelection(temp);

		if (listView.getCount() < 1) {

			nomobcast.setVisibility(TextView.VISIBLE);

		} else {
			nomobcast.setVisibility(TextView.GONE);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction("MyGCMMessageReceived");
		registerReceiver(receiver, filter);
		super.onResume();

	}

	// context menu code
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);

		// menu.setHeaderTitle("Please Select");
		// menu.setHeaderIcon(R.drawable.ic_launcher);

	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.read:
			itemRead(info.position);

			return true;
		case R.id.unread:

			itemUnread(info.position);
			return true;
		case R.id.delete:

			itemDelete(info.position);

			return true;
		case R.id.view:

			itemView(info.position);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void itemDelete(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete fot announceid = " +
		// _id, Toast.LENGTH_SHORT).show();
		
		new DeleteFile(getApplicationContext(),
				AnnounceDBAdapter.SQLITE_TRAINING, _id).Delete();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				TrainingListView.this);
		announce.open();
		// delete code
		announce.deleterow(_id, "Training");
		announce.close();

		onPause();
		onResume();

	}

	public void itemRead(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				TrainingListView.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, "Training");

		announce.close();
		onPause();
		onResume();

	}

	public void itemUnread(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				TrainingListView.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, "Training");

		announce.close();
		onPause();
		onResume();

	}

	// context menu code ends here

	public void itemView(int position) {
		// Get the cursor, positioned to the corresponding row in the result set
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);

		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		startActivity(new OpenContent(TrainingListView.this,
				AnnounceDBAdapter.SQLITE_TRAINING, _id).itemView());

	}

	private void displayListView() {
		
		dbHelper.expireTraining();//ADDED VIKALP CONTENT EXPIRY
		Cursor cursor = dbHelper.fetchAllTraining();

		// The desired columns to be bound
		String[] columns = new String[] { AnnounceDBAdapter.KEY_TITLE,
				AnnounceDBAdapter.KEY_DETAIL, AnnounceDBAdapter.KEY_SUMMARY };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.tvTListTitle, R.id.tvTListDetail,
				R.id.tvListSummary };

		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		dataAdapter = new TrainingAdapter(this, R.layout.trainlisting, cursor,
				columns, to);

		listView = (ListView) findViewById(R.id.listViewT);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				itemView(position);
			}
		});// onClick

	}// end of displayList

	private class RecruitTask extends AsyncTask<String, Void, String> {

		String id, title, summary, link, name, detail, type, ss;

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

		@Override
		protected String doInBackground(String... params1) {

			try {

				if (this.isCancelled()) {
					Log.e("background task", "canceled");

				} else {

					JSONArray jsonArray = new JSONArray(response);
					Log.d("Number of entries", jsonArray.length() + "");
					JSONObject jObject1 = jsonArray.getJSONObject(jsonArray
							.length()-1);
					id = jObject1.getString("ID");

					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastTraining", id).commit();
					for (int i = 0; i < jsonArray.length(); i++)
					{
						if (this.isCancelled()) {
							Log.e("background task", "canceled");
							break;

						} else {

							// do your work here

							JSONObject jObject = jsonArray.getJSONObject(i);

							id = jObject.getString("ID");
							title = jObject.getString("title");
//							SU VIKALP DATE ORDER ISSUE
//							detail = jObject.getString("detail");
							detail = Utilities.convertdate(jObject.getString("detail"));
//							EU VIKALP DATE ORDER ISSUE
							ss = jObject.getString("ss");
							name = jObject.getString("name");
							type = jObject.getString("type");
							summary = jObject.getString("summary");
							link = jObject.getString("link");

							String readStatus = jObject.getString("readStatus");
							try{
								contentExpiry = Utilities.convertdate(jObject
										.getString("expiry")); // ADDED VIKALP
																// CONTENT
																// EXPIRY	
							}catch(Exception e){
								Log.i(TAG, e.toString());
							}

							title = title.replaceAll("\\\\", "");
							summary = summary.replaceAll("\\\\", "");
							link = link.replaceAll(" ", "%20");

							Log.v("Training ", "in downloadforTraining");
							String ename = System.currentTimeMillis() + "";

							if(false){try {
								URL url = new URL(link);
								String root = Environment
										.getExternalStorageDirectory()
										.toString();
								Log.v("type is ", type.toString());

								String foldername = "";
								if (type.contentEquals("pdf"))
									foldername = getString(R.string.pdf);
								else if (type.contentEquals("ppt"))
									foldername = getString(R.string.ppt);
								else if (type.contentEquals("doc"))
									foldername = getString(R.string.doc);
								else if (type.contentEquals("xls"))
									foldername = getString(R.string.xls);
								else if (type.contentEquals("video"))
									foldername = "mobcast_videos";
								else if (type.contentEquals("audio"))
									foldername = "mobcast_audio";

								File myDir = new File(root + Constants.APP_FOLDER
										+ foldername);

								String fname = ename;

								myDir.mkdirs();

								if ((type.contentEquals("audio"))
										|| (type.contentEquals("video"))) {
									fname = name;
								}

								File file = new File(myDir, fname);
								if (file.exists())
									file.delete();
								URLConnection ucon = url.openConnection();
								InputStream is = ucon.getInputStream();
								BufferedInputStream bis = new BufferedInputStream(
										is);
								ByteArrayBuffer baf = new ByteArrayBuffer(50);
								int current = 0;
								while ((current = bis.read()) != -1) {
									baf.append((byte) current);
								}
								FileOutputStream fos = new FileOutputStream(
										file);
								fos.write(baf.toByteArray());
								fos.close();
								announce.open();
								long x = announce.createTraining(title, detail, name,
										summary, type, ename, id, ss,
										contentExpiry, link);
								if (readStatus.contentEquals("true")) {
									announce.readrow(x + "", AnnounceDBAdapter.SQLITE_TRAINING);
								}
								announce.close();	

							} catch (Exception e) {
								continue;
							}}
							announce.open();
							long x = announce.createTraining(title, detail, name,
									summary, type, ename, id, ss,
									contentExpiry, link);
							if (readStatus.contentEquals("true")) {
								announce.readrow(x + "", AnnounceDBAdapter.SQLITE_TRAINING);
							}
							announce.close();	

						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.e("response", response);

			return null;
		}// End of doInBackgrounf

		@Override
		protected void onPostExecute(String result) {
			if((pDialog!=null)&&(pDialog.isShowing()))
			{pDialog.dismiss();
				pDialog=null;
			}
			wl.release();
			onPause();
			onResume();

		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(TrainingListView.this);
			pDialog.setMessage(Html.fromHtml("Downloading Content, Please Wait..."));
			pDialog.setIndeterminate(false);
			// pDialog.setCancelable(false);
			pDialog.show();

			pDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					asyncHttpPost.cancel(true);
					onPause();
					onResume();
				}
			});
			wl.acquire();
		}
	}

	class UpdateFeeds extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;

		/**
		 * constructor
		 */

		public UpdateFeeds(HashMap<String, String> data) {
			mData = data;
		}

		public UpdateFeeds(HashMap<String, String> data, Activity activity) {

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
			String str = "";
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
			response = str;
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				if((pDialog!=null)&&(pDialog.isShowing()))
				{pDialog.dismiss();
					pDialog=null;
				}
			} catch (Exception e) {
				// nothing
			}
			Log.e("response", response);

			if (response
					.contentEquals("No new feeds or updates at this moment.")) {
				if (listView.getCount() < 1)
					nomobcast.setVisibility(TextView.VISIBLE);
			} else {
				try {
					asyncHttpPost = new RecruitTask();
					asyncHttpPost.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
	void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
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