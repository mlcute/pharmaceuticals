package com.sanofi.in.mobcast;

import java.io.File;
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
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;

public class AwardList extends Activity {

	private AnnounceDBAdapter dbHelper;
	private AwardAdapter dataAdapter;

	ListView listView;
	String aid;
	ImageButton search, sample_refresh;
	RecruitTask asyncHttpPost;
	String response;
	TextView nomobcast;
	AnnounceDBAdapter announce;
	static ProgressDialog pDialog;
	
	private static final String TAG = AwardList.class.getSimpleName();

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if((pDialog!=null)&&(pDialog.isShowing()))
		{pDialog.dismiss();
			pDialog=null;
		}
		super.onDestroy();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.awardlist);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		Log.v("award", "in award list");
		aid = "";
		listView = (ListView) findViewById(R.id.awardList);
		nomobcast = (TextView) findViewById(R.id.noNew);
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		search = (ImageButton) findViewById(R.id.sample_button);
		search.setOnTouchListener(myhandler2);
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(AwardList.this, Search.class);
				i.putExtra("tablename", "Award");
				startActivity(i);

			}
		});

		sample_refresh.setOnTouchListener(myhandler2);
		announce = new AnnounceDBAdapter(this);
		sample_refresh.setOnClickListener(refreshhandler);
		announce.open();
		if (announce.fetchAllAwards().getCount() < 1) {
			ConnectionDetector cd = new ConnectionDetector(
					getApplicationContext());

			Boolean isInternetPresent = cd.isConnectingToInternet(); //
			if (isInternetPresent) {
				refreshhandler.onClick(sample_refresh);
			}
		}
		announce.close();
	}// end of onCreate

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

	protected void onResume() {
		dbHelper = new AnnounceDBAdapter(this);
		dbHelper.open();
		populate();

		dbHelper.close();

		if (listView.getCount() < 1) {

			nomobcast.setVisibility(TextView.VISIBLE);
		} else {
			nomobcast.setVisibility(TextView.GONE);
		}

		int temp = Integer.parseInt((getPreferences(MODE_PRIVATE).getString(
				"listScroll", 0 + "")));
		Log.v("temp on resume", temp + "");
		listView.setSelection(temp);

		IntentFilter filter = new IntentFilter();
		filter.addAction("MyGCMMessageReceived");
		registerReceiver(receiver, filter);
		super.onResume();

	}

	View.OnClickListener refreshhandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
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
				populate();
				dbHelper.close();
			}
		}
	};
	
	private void refreshData(){

		HashMap<String, String> params = new HashMap<String, String>();

		params.put("mName", "awards");
		params.put("companyID",
				getResources().getString(R.string.companyID));

		SharedPreferences pref;
		pref = getSharedPreferences("MobCastPref", 0);
		String temp1 = pref.getString("name", "akshay@bombil.com");

		params.put("emailID", temp1);
		params.put(com.mobcast.util.Constants.user_id, temp1);
		params.put("fID", pref.getString("lastAward", "0"));
		UpdateFeeds asyncHttpPost = new UpdateFeeds(params);
		asyncHttpPost.execute(com.mobcast.util.Constants.REFRESH_FEEDS);

		// response = response.replace("[", "");
		// response = response.replace("]", "");
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
		
		String fname = cursor.getString((cursor
				.getColumnIndexOrThrow(AnnounceDBAdapter.KEY_IMAGEPATH)));
		File file = new File(fname);
		Log.d("deleting file", file.getAbsolutePath());
		file.delete();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(AwardList.this);
		announce.open();
		// delete code
		announce.deleterow(_id, "Award");
		announce.close();

		onPause();
		onResume();

	}

	public void itemRead(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(AwardList.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, "Award");

		announce.close();
		onPause();
		onResume();

	}

	public void itemUnread(int position) {
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(AwardList.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, "Award");

		announce.close();
		onPause();
		onResume();

	}

	// context menu code ends here

	public void itemView(int position) {
		// Get the cursor, positioned to the corresponding row in the result set
		Cursor cursor = (Cursor) listView.getItemAtPosition(position);
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

		Intent i = new Intent(new OpenContent(AwardList.this,
				AnnounceDBAdapter.SQLITE_AWARD, _id).itemView());

		startActivity(i);

	}

	private void populate() {
		
		dbHelper.expireAwards();//ADDED VIKALP CONTENT EXPIRY
		Cursor cursor = dbHelper.fetchAllAwards();
		Log.v("Inpopulate", "cursor is " + cursor.toString());
		String[] columns = new String[] { AnnounceDBAdapter.KEY_TITLE,
				AnnounceDBAdapter.KEY_NAME, AnnounceDBAdapter.KEY_DETAIL, AnnounceDBAdapter.KEY_DATE, //ADDED VIKALP AWARD RDATE
				AnnounceDBAdapter.KEY_SUMMARY

		};

		int[] to = new int[] { R.id.awardTitle, R.id.awardName, R.id.awardTime, R.id.awardTime, //ADDED VIKALP AWARD RDATE
				R.id.awardSum };

		dataAdapter = new AwardAdapter(this, R.layout.awardlist_item, cursor,
				columns, to, 0);

		// Assign adapter to ListView
		listView.setAdapter(dataAdapter);
		listView.setSelector(R.drawable.listselector);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

				itemView(position);

			}
		});
	}// end of populate

	private class RecruitTask extends AsyncTask<String, Void, String> {
		/*
		 * [{"ID":"12","title":"Award for Excellence in Marketing Communication",
		 * "summary":"Award for Excellence in Marketing Communication", "link":
		 * "http:\/\/www.mobcast.in\/mobcast\/upload\/image\/3a4df44.jpg","imgName":"3a4df44.jpg",
		 * "name"
		 * :"Rahul Srinivasan","detail":"08-04-2014","ss":"yes","fileSize":
		 * 25215}]
		 */
		String id, title, summary, link, imgName, name, detail, ss, filesize, rdate, // ADDED VIKALP AWARD RDATE
				contentExpiry = "9999-99-99";

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

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
					pref.edit().putString("lastAward", id).commit();
					ApplicationLoader.getPreferences().setLastAwardsId(id);//ADDED VIKALP PULL SERVICE

					for (int i = 0; i < jsonArray.length(); i++) {
						if (this.isCancelled()) {
							Log.e("backgroud task", "candeled");
							break;
						} else {
							// do your work here

							JSONObject jObject = jsonArray.getJSONObject(i);

							id = jObject.getString("ID");
							title = jObject.getString("title");
//							SU VIKALP DATE ORDER ISSUE
//							detail = jObject.getString("detail");
							detail = Utilities.convertdate(jObject.getString("detail"));
							rdate = Utilities.convertdate(jObject.getString("timeStamp").substring(0, 10)); //ADDED VIKALP AWARD RDATE
//							EU VIKALP DATE ORDER ISSUE
							imgName = jObject.getString("imgName");
							name = jObject.getString("name");
							ss = jObject.getString("ss");
							summary = jObject.getString("summary");
							link = jObject.getString("link");
							link = link.replace(" ", "%20");
							filesize = jObject.getString("fileSize");
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
							
							String root = Environment
									.getExternalStorageDirectory().toString();
							String fname = imgName;
							String imagePath = root
									+ Constants.APP_FOLDER_IMG + fname;
							Log.v("image name", fname);
							// Storing in Database

							announce.open();
							// announce.createEvent(title, date, day, time,
							// venue,
							// desc, id, "yes");
							long x = announce.createAward(title, name, detail,rdate,
									id, summary, imagePath, ss, contentExpiry,
									link);
							if (readStatus.contentEquals("true")) {
								announce.readrow(x + "",
										AnnounceDBAdapter.SQLITE_AWARD);
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
			pDialog = null;
			asyncHttpPost.cancel(true);

			onPause();
			onResume();

		}// end of onPostExecute

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(AwardList.this);
			pDialog.setMessage(Html
					.fromHtml("Downloading Content, Please Wait..."));
			pDialog.setIndeterminate(false);

			// pDialog.setCancelable(false);
			pDialog.show();
			pDialog.setOnCancelListener(new OnCancelListener() {

				public void onCancel(DialogInterface dialog) {
					// TODO Auto-generated method stub
					if (listView.getCount() > 0)
						asyncHttpPost.cancel(true);
					onPause();
					onResume();

				}
			});
			wl.acquire();
		}// end of onPreExecute

	}// end of RecruitTask class

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


}// end of Activity
