package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class EventListView extends Activity {

	private AnnounceDBAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private SpecialAdapter dataAdapter;
	ListView listView;
	ImageButton search, sample_refresh;
	String response;
	TextView nomobcast;
	AnnounceDBAdapter announce;
	static ProgressDialog pDialog;
	private static final String TAG = EventListView.class.getSimpleName();

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if ((pDialog != null) && (pDialog.isShowing())) {
			pDialog.dismiss();
			pDialog = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
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
		setContentView(R.layout.eventlist);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		dbHelper = new AnnounceDBAdapter(this);
		dbHelper.open();
		// IMPORTANT CONCEPT
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		// Clean all data
		// dbHelper.deleteAllCountries();
		// Add some data
		nomobcast = (TextView) findViewById(R.id.noNew);
		// dbHelper.insertSomeCountries();
		displayListView();

		dbHelper.close();
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		search = (ImageButton) findViewById(R.id.sample_button);
		search.setOnTouchListener(myhandler2);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(EventListView.this, Search.class);
				i.putExtra("tablename", "Event");
				startActivity(i);

			}
		});

		sample_refresh.setOnTouchListener(myhandler2);
		sample_refresh.setOnClickListener(myhandler11);
		announce = new AnnounceDBAdapter(this);
		announce.open();
		if (announce.fetchAllEvents().getCount() < 1) {
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());

			Boolean isInternetPresent = cd.isConnectingToInternet(); //
			if (isInternetPresent) {
				myhandler11.onClick(sample_refresh);
			}
		}
		announce.close();

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

			// TODO Auto-generated method stub
			// Toast.makeText(getApplicationContext(), "refreshList",
			// Toast.LENGTH_SHORT).show();
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());

			Boolean isInternetPresent = cd.isConnectingToInternet(); //
			if (isInternetPresent) {
				RotateAnimation ranim = (RotateAnimation) AnimationUtils
						.loadAnimation(getApplicationContext(),
								R.anim.rotateanim);
				sample_refresh.startAnimation(ranim);
				// SA VIKALP
				try {
					refreshData();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// EA VIKALP

				if (listView != null && listView.getCount() < 1) {
					dbHelper = new AnnounceDBAdapter(getApplicationContext());
					dbHelper.open();
					displayListView();
					dbHelper.close();
				}
			}

		}
	};

	private void refreshData() {

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("mName", "events");
		params.put("companyID", getResources().getString(R.string.companyID));

		SharedPreferences pref;
		pref = getSharedPreferences("MobCastPref", 0);
		String temp1 = pref.getString("name", "akshay@bombil.com");
		String fid = pref.getString("lastEvent", "0");

		params.put("fID", fid);

		params.put("emailID", temp1);
		params.put(com.mobcast.util.Constants.user_id, temp1);
		Updatefeeds asyncHttpPost = new Updatefeeds(params);
		asyncHttpPost.execute(com.mobcast.util.Constants.REFRESH_FEEDS);
	}

	protected void onResume() {
		dbHelper.open();

		// Generate ListView from SQLite Database
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

		AnnounceDBAdapter announce = new AnnounceDBAdapter(EventListView.this);
		announce.open();
		// delete code
		announce.deleterow(_id, "Event");
		announce.close();

		onPause();
		onResume();

	}

	public void itemRead(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(EventListView.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, "Event");

		announce.close();
		onPause();
		onResume();

	}

	public void itemUnread(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(EventListView.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, "Event");

		announce.close();
		onPause();
		onResume();

	}

	public void itemView(int position) {
		// Get the cursor, positioned to the corresponding row in the result set
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		startActivity(new OpenContent(EventListView.this,
				AnnounceDBAdapter.SQLITE_EVENT, _id).itemView());
	}

	// context menu code ends here

	private void displayListView() {
		dbHelper.expireEvents();// ADDED VIKALP CONTENT EXPIRY
		Cursor cursor = dbHelper.fetchAllEvents();

		// The desired columns to be bound
		String[] columns = new String[] { EventDBAdapter.KEY_TITLE,
				EventDBAdapter.KEY_DATE, EventDBAdapter.KEY_TIME,
				EventDBAdapter.KEY_VENUE, EventDBAdapter.KEY_SUMMARY, "Rtime" };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.tvListTitle, R.id.tvListDate,
				R.id.tvListTime, R.id.tvListWhere, R.id.tvListDetails,
				R.id.tvEventADetail };

		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		dataAdapter = new SpecialAdapter(this, R.layout.eventlisting, cursor,
				columns, to, 0);

		/*
		 * dataAdapter = new SimpleCursorAdapter( this, R.layout.eventlisting,
		 * cursor, columns, to, 0);
		 */

		listView = (ListView) findViewById(R.id.listView1);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				itemView(position);
			}
		});

	}

	private class RecruitTask extends AsyncTask<String, Void, String> {

		String id, title, date, venue, day, time, desc,
				contentExpiry = "9999-99-99", calenderEnabled, rsvpNeeded;
		String endTime = "";

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

		@Override
		protected String doInBackground(String... params1) {

			try {

				JSONArray jsonArray = new JSONArray(response);
				// Log.d("Number of entries" , jsonArray.length());
				Log.d("Number of entries", jsonArray.length() + "");

				for (int i = 0; i < jsonArray.length(); i++) {

					JSONObject jObject = jsonArray.getJSONObject(i);

					id = jObject.getString("ID");
					title = jObject.getString("title");
					date = jObject.getString("date");
					day = jObject.getString("day");
					venue = jObject.getString("venue");
					time = jObject.getString("time");
					desc = jObject.getString("desc");
					String details = jObject.getString("detail");
					calenderEnabled = jObject.getString("calendarEnabled");
					rsvpNeeded = jObject.getString("rsvpNeeded");

					String readStatus = jObject.getString("readStatus");
					try {
						contentExpiry = Utilities.convertdate(jObject
								.getString("expiry")); // ADDED VIKALP
														// CONTENT
														// EXPIRY
					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
					// SA ADDED VIKALP EVENT END TIME
					try {
						endTime = jObject.getString("endTime");
					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
					// EA ADDED VIKALP EVENT END TIME

					title = title.replaceAll("\\\\", "");
					desc = desc.replaceAll("\\\\", "");
					venue = venue.replaceAll("\\\\", "");
					announce.open();
					long x = announce.createEvent(title, date, day, time,
							venue, desc, id, "yes", contentExpiry, rsvpNeeded,
							calenderEnabled, endTime);
					{
						SharedPreferences pref;
						pref = getSharedPreferences("MobCastPref", 0);
						pref.edit().putString("lastEvent", id).commit();

					}

					// verticle date logic

					String year = details.substring(6, 10);
					String day11 = details.substring(0, 2);
					String month11 = (details.substring(3, 5));
					String date = year + "-" + month11 + "-" + day11;

					// verticle date logic ends

					announce.eventDetails(date, id);

					if (readStatus.contentEquals("true")) {
						announce.readrow(x + "", AnnounceDBAdapter.SQLITE_EVENT);
					}
					announce.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.e("response", response);

			return null;
		}// End of doInBackgrounf

		@Override
		protected void onPostExecute(String result) {
			if ((pDialog != null) && (pDialog.isShowing())) {
				pDialog.dismiss();
				pDialog = null;
			}
			// wl.release();
			onPause();
			onResume();
			Utilities.dev(EventListView.this);
		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(EventListView.this);
			pDialog.setMessage(Html.fromHtml("Please Wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					onPause();
					onResume();
				}
			});
			// wl.acquire();
		}// end of onPreExecute

	}// end of RecruitTask class

	class Updatefeeds extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;

		/**
		 * constructor
		 */

		public Updatefeeds(HashMap<String, String> data) {
			mData = data;
		}

		public Updatefeeds(HashMap<String, String> data, Activity activity) {

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
			Log.d("1", str);

			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				if ((pDialog != null) && (pDialog.isShowing())) {
					pDialog.dismiss();
					pDialog = null;
				}
				Log.e("2", response);

			} catch (Exception e) {
				// nothing
			}

			if (response
					.contentEquals("No new feeds or updates at this moment.")) {
				if (listView.getCount() < 1)
					nomobcast.setVisibility(TextView.VISIBLE);
			} else {
				try {
					RecruitTask asyncHttpPost = new RecruitTask();
					asyncHttpPost.execute();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
