package com.sanofi.in.mobcast;

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
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
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
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DeleteFile;
import com.mobcast.util.Utilities;

public class AnnounceListView extends Activity implements OnScrollListener {

	private AnnounceDBAdapter dbHelper;
	// private SimpleCursorAdapter dataAdapter;
	private SuperAdapter dataAdapter;
	int noofitems;

	AnnounceDBAdapter announce;
	static ProgressDialog pDialog;
	RecruitTask asyncHttpPost;
	ListView listView;
	String response = "No new feeds or updates at this moment.";
	TextView nomobcast;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;

	private static final String TAG = AnnounceListView.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	ImageButton search, sample_refresh;

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
		setContentView(R.layout.announcelist);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}

		search = (ImageButton) findViewById(R.id.sample_button);
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		search.setOnTouchListener(myhandler2);
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		announce = new AnnounceDBAdapter(this);
		nomobcast = (TextView) findViewById(R.id.noNew);

		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AnnounceListView.this, Search.class);
				i.putExtra("tablename", "Announce");
				startActivity(i);

			}
		});

		sample_refresh.setOnTouchListener(myhandler2);
		sample_refresh.setOnClickListener(refreshhandler);

		announce.open();
		if (announce.fetchAllAnnouncements().getCount() < 1) {
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());

			Boolean isInternetPresent = cd.isConnectingToInternet(); //
			if (isInternetPresent) {
				refreshhandler.onClick(sample_refresh);

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

	OnClickListener refreshhandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			RotateAnimation ranim = (RotateAnimation) AnimationUtils
					.loadAnimation(getApplicationContext(), R.anim.rotateanim);
			sample_refresh.startAnimation(ranim);

			if (listView != null && listView.getCount() < 1) {
				dbHelper = new AnnounceDBAdapter(getApplicationContext());
				dbHelper.open();
				displayListView();
				dbHelper.close();
				// sample_refresh.clearAnimation();
			} else {
				try {

					HashMap<String, String> params = new HashMap<String, String>();

					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					String temp1 = pref.getString("name", "akshay@bombil.com");
					String fid = pref.getString("lastAnnounce", "0");

					params.put("fID", fid);
					params.put("mName", "announcement");
					params.put("companyID",
							getResources().getString(R.string.companyID));

					params.put("emailID", temp1);
					params.put(com.mobcast.util.Constants.user_id, temp1);
					Updatefeeds asyncHttpPost = new Updatefeeds(params);
					asyncHttpPost
							.execute(com.mobcast.util.Constants.REFRESH_FEEDS);

					// sample_refresh.clearAnimation();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	protected void onResume() {

		dbHelper = new AnnounceDBAdapter(this);
		dbHelper.open();
		// IMPORTANT CONCEPT
		// Clean all data
		// dbHelper.deleteAllCountries();
		// Add some data
		// dbHelper.insertSomeCountries();

		// Generate ListView from SQLite Database
		displayListView();

		dbHelper.close();

		// listView.getFirstVisiblePosition();

		// getPreferences(MODE_PRIVATE).edit().putString("listScroll",listView.getFirstVisiblePosition()+"").commit();

		int temp = Integer.parseInt((getPreferences(MODE_PRIVATE).getString(
				"listScroll", 0 + "")));
		Log.v("temp on resume", temp + "");
		listView.setSelection(temp);

		if (listView.getCount() < 1) {
			// search, sample_refresh
			// sample_refresh.setVisibility(ImageButton.VISIBLE);
			nomobcast.setVisibility(TextView.VISIBLE);

		} else {
			nomobcast.setVisibility(TextView.GONE);
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction("MyGCMMessageReceived");
		registerReceiver(receiver, filter);
		super.onResume();

	}

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

	// to view an item
	public void itemView(int position) {
		// Get the cursor, positioned to the corresponding row in the result set
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);

		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		startActivity(new OpenContent(AnnounceListView.this,
				AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).itemView());
	}

	// to delete an item
	public void itemDelete(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete fot announceid = " +
		// _id, Toast.LENGTH_SHORT).show();
		new DeleteFile(getApplicationContext(),
				AnnounceDBAdapter.SQLITE_ANNOUNCE, _id).Delete();
		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				AnnounceListView.this);
		announce.open();
		// delete code
		announce.deleterow(_id, "Announce");
		announce.close();
		DeleteFile d = new DeleteFile(AnnounceListView.this,
				AnnounceDBAdapter.SQLITE_AWARD, _id + "");
		d.Delete();

		onPause();
		onResume();

	}

	// to set an item as read in the announcement list view
	public void itemRead(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				AnnounceListView.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, "Announce");

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
				AnnounceListView.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, "Announce");

		announce.close();
		onPause();
		onResume();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if ((pDialog != null) && (pDialog.isShowing())) {
			pDialog.dismiss();
			pDialog = null;
		}
		super.onDestroy();
	}

	private void displayListView() {
		dbHelper.expireAnnouncements();// VIKALP CONTENT EXPIRY
		Cursor cursor = dbHelper.fetchAllAnnouncements();

		// The desired columns to be bound
		String[] columns = new String[] { AnnounceDBAdapter.KEY_TITLE,
				AnnounceDBAdapter.KEY_DETAIL, AnnounceDBAdapter.KEY_FROM,
				AnnounceDBAdapter.KEY_SUMMARY };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.tvAListTitle, R.id.tvAListDetail,
				R.id.tvAListFrom, R.id.tvAListSummary };

		// create the adapter using the cursor pointing to the desired data
		// as well as the layout information
		dataAdapter = new SuperAdapter(this, R.layout.announcelisting, cursor,
				columns, to, 0);

		listView = (ListView) findViewById(R.id.listView2);
		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		listView.setSelector(R.drawable.listselector);
		// listView.getCount();=listView.getCount();

		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				itemView(position);
			}
		});

		/*
		 * dataAdapter.setFilterQueryProvider(new FilterQueryProvider() { public
		 * Cursor runQuery(CharSequence constraint) { return
		 * dbHelper.fetchCountriesByName(constraint.toString()); } });
		 */
	}

	private class RecruitTask extends AsyncTask<String, Void, String> {

		String id, title, fro, summary, type, link, detail, fileSize,
				name = "name", ss, contentExpiry, fileLink;

		@Override
		protected String doInBackground(String... params1) {

			try {
				if (isCancelled()) {
				} else {

					JSONArray jsonArray = new JSONArray(response);
					// Log.d("Number of entries" , jsonArray.length());
					Log.d("Number of entries", jsonArray.length() + "");
					JSONObject jObject1 = jsonArray.getJSONObject(jsonArray
							.length()-1);

					id = jObject1.getString("ID");

					SharedPreferences pref;
					pref = getSharedPreferences("MobCastPref", 0);
					pref.edit().putString("lastAnnounce", id).commit();
					ApplicationLoader.getPreferences().setLastAnnouncementId(id);//ADDED VIKALP PULL SERVICE

					for (int i = 0; i < jsonArray.length(); i++) {
						if (isCancelled())
							break;
						else {
							// do your work here
							try {
								JSONObject jObject = jsonArray.getJSONObject(i);

								id = jObject.getString("ID");
								title = jObject.getString("title");
								fro = jObject.getString("fro");
								summary = jObject.getString("summary");
								type = jObject.getString("type");
								link = jObject.getString("link");
								fileLink = link;
								name = link.substring(
										link.lastIndexOf('/') + 1,
										link.length());
//								SU VIKALP DATE ORDER ISSUE
//								detail = jObject.getString("detail");
								detail = Utilities.convertdate(jObject.getString("detail"));
//								EU VIKALP DATE ORDER ISSUE
								String readStatus = jObject
										.getString("readStatus");
								fileSize = jObject.getString("fileSize");
								try{
									contentExpiry = Utilities.convertdate(jObject
											.getString("expiry")); // ADDED VIKALP
																	// CONTENT
																	// EXPIRY	
								}catch(Exception e){
									Log.i(TAG, e.toString());
								}
								ss = jObject.getString("ss");
								fro = fro.replaceAll("\\\\", "");
								title = title.replaceAll("\\\\", "");
								summary = summary.replaceAll("\\\\", "");

								announce.open();

								long x = announce.createAnnouncement(title,
										detail, fro, type, name, summary, id,
										ss, contentExpiry, link);
								if (readStatus.contentEquals("true")) {
									announce.readrow(x + "",
											AnnounceDBAdapter.SQLITE_ANNOUNCE);
								}

								announce.close();
							} catch (Exception e) {
								Log.i(TAG, e.toString());
							}
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
			if ((pDialog != null) && (pDialog.isShowing())) {
				pDialog.dismiss();
				pDialog = null;
			}

			onPause();
			onResume();

		}// end of onPostExecute

		@Override
		protected void onPreExecute() {
			/*
			 * Handler handler = new Handler(); handler.postDelayed(new
			 * Runnable() {
			 * 
			 * @Override public void run() { if(pDialog.isShowing()){ //cancel
			 * async, dismiss progress; asyncHttpPost.cancel(true); } } },
			 * 8000);
			 */

			pDialog = new ProgressDialog(AnnounceListView.this);
			pDialog.setMessage(Html
					.fromHtml("Downloading Content, Please Wait..."));
			pDialog.setIndeterminate(false);

			pDialog.show();

			pDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub

					asyncHttpPost.cancel(true);

					onPause();
					onResume();
				}
			});

		}// end of onPreExecute

	}// end of RecruitTask class

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	class Updatefeeds extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		String str = "";
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
			Log.d("1", str);

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
				if ((pDialog != null) && (pDialog.isShowing())) {
					pDialog.dismiss();
					pDialog = null;
				}
				Log.d("2", response);
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

				if (pDialog != null) {
					pDialog.dismiss();
					pDialog = null;
				}

			} catch (Exception e) {
				// nothing
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
