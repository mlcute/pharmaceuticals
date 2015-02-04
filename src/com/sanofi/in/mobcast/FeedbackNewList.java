package com.sanofi.in.mobcast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;
import com.sanofi.in.mobcast.AsyncHttpPost.OnPostExecuteListener;

public class FeedbackNewList extends Activity {

	ListView list;
	AnnounceDBAdapter dbHelper;
	Cursor cursor;
	FeedbackNewAdapter dataadapter;
	ImageButton search, sample_refresh;
	String response;
	AnnounceDBAdapter announce;
	int total_questions;
	static ProgressDialog pDialog;
	public HashMap<Integer, FeedbackForm> formMap;
	String xmlString = "";
	FeedbackForm ff;
	TextView NoNew;
	FeedbackData fd;
	FeedbackQuestion fq;
	ArrayList<FeedbackQuestion> al;
	String question;
	
	private static final String TAG = FeedbackNewList.class.getSimpleName();
	
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
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}

		getPreferences(MODE_PRIVATE).edit()
				.putString("listScroll", list.getFirstVisiblePosition() + "")
				.commit();
		Log.v("temp on pause", list.getFirstVisiblePosition() + "");
		super.onPause();
		if ((pDialog != null) && (pDialog.isShowing())) {
			pDialog.dismiss();
			pDialog = null;
		}
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

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedbacklist);
		dbHelper = new AnnounceDBAdapter(this);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		list = (ListView) findViewById(R.id.feedbackList);
		NoNew = (TextView) findViewById(R.id.noNew);
		search = (ImageButton) findViewById(R.id.sample_button);
		search.setOnTouchListener(myhandler2);
		getPreferences(MODE_PRIVATE).edit().putString("listScroll", "0")
				.commit();
		sample_refresh = (ImageButton) findViewById(R.id.sample_refresh);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(FeedbackNewList.this, Search.class);
				i.putExtra("tablename", "Feedback");
				startActivity(i);

			}
		});

		sample_refresh.setOnTouchListener(myhandler2);
		sample_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RotateAnimation ranim = (RotateAnimation) AnimationUtils
						.loadAnimation(getApplicationContext(),
								R.anim.rotateanim);
				sample_refresh.startAnimation(ranim);
			}
		});
		// end of
		// refreshbutton
		// onclick

		announce = new AnnounceDBAdapter(this);

		announce.open();
		if (announce.fetchAllFeedbackForListView().getCount() < 1) {
			onRefresh();
		}
		announce.close();

	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

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

	@Override
	protected void onResume() {

		dbHelper.open();
		populate();
		dbHelper.close();
		if (list.getCount() < 1)
			NoNew.setVisibility(TextView.VISIBLE);
		else
			NoNew.setVisibility(TextView.GONE);
		int temp = Integer.parseInt((getPreferences(MODE_PRIVATE).getString(
				"listScroll", 0 + "")));
		Log.v("temp on resume", temp + "");
		list.setSelection(temp);

		IntentFilter filter = new IntentFilter();
		filter.addAction("MyGCMMessageReceived");
		registerReceiver(receiver, filter);
		super.onResume();

	}

	void populate() {
		
		dbHelper.expireFeedback();//ADDED VIKALP CONTENT EXPIRY
		cursor = dbHelper.fetchAllFeedbackForListView();
		Log.e("feedback new", "dbhelper");
		// The desired columns to be bound
		// feedbackNo, feedbackTitle, feedbackDescription,
		// feedbackTotalQuestions, feedbackDate FROM feedback
		String[] columns = new String[] { "feedbackTitle",
				"feedbackDescription", "feedbackTotalQuestions", "feedbackDate" };

		// the XML defined views which the data will be bound to
		int[] to = new int[] { R.id.feedbackTitle, R.id.feedbackDescription,
				R.id.feedbackQuestionNo, R.id.feedbackDate, };

		dataadapter = new FeedbackNewAdapter(FeedbackNewList.this,
				R.layout.feedbacklist_item, cursor, columns, to, 0);

		// SimpleCursorAdapter feedbackadapter = new
		// SimpleCursorAdapter(FeedbackNewList.this,R.layout.feedbacklist_item,cursor,columns,to);
		Log.e("feedback new list", "list adapter called");
		list.setAdapter(dataadapter);
		registerForContextMenu(list);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {
//				SU VIKALP
				if(isItemRead(position))
					Toast.makeText(getApplicationContext(), "This feedback has already been submitted! ", Toast.LENGTH_SHORT).show();
				else{
//				itemRead(position); //ADDED VIKALP FEEDBACK READ ON SUBMIT ONLY
				itemView(position);
				}
//				EU VIKALP
			}
		});

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
			onResume();
			return true;
		case R.id.unread:

			itemUnread(info.position);
			onResume();
			return true;
		case R.id.delete:

			itemDelete(info.position);
			onResume();
			return true;
		case R.id.view:

			itemView(info.position);

			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	// to delete an item
	public void itemDelete(int position) {
		Cursor cursor = (Cursor) list.getItemAtPosition(position);
		String _id = cursor.getString(cursor
				.getColumnIndexOrThrow("feedbackNo"));
		// Toast.makeText(this, "You have chosen the delete fot announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(FeedbackNewList.this);
		announce.open();
		// delete code
		announce.deleterow(_id, AnnounceDBAdapter.SQLITE_FEEDBACK);
		announce.close();

		onPause();
		onResume();

	}

	// to set an item as read in the announcement list view
	public void itemRead(int position) {
		Cursor cursor = (Cursor) list.getItemAtPosition(position);
		String _id = cursor.getString(cursor
				.getColumnIndexOrThrow("feedbackNo"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(FeedbackNewList.this);
		announce.open();
		// set row read code for announcement row
		announce.readrow(_id, AnnounceDBAdapter.SQLITE_FEEDBACK);

		announce.close();
		onPause();
		onResume();

	}

	public void itemUnread(int position) {
		Cursor cursor = (Cursor) list.getItemAtPosition(position);
		String _id = cursor.getString(cursor
				.getColumnIndexOrThrow("feedbackNo"));
		// Toast.makeText(this, "You have chosen the delete for announceid = " +
		// _id, Toast.LENGTH_SHORT).show();

		AnnounceDBAdapter announce = new AnnounceDBAdapter(FeedbackNewList.this);
		announce.open();
		// set row read code for announcement row
		announce.unreadrow(_id, AnnounceDBAdapter.SQLITE_FEEDBACK);

		announce.close();
		onPause();
		onResume();

	}

	public boolean isItemRead(int position) {
		Cursor cursor = (Cursor) list.getItemAtPosition(position);
		String _id = cursor.getString(cursor
				.getColumnIndexOrThrow("feedbackNo"));
		AnnounceDBAdapter announce = new AnnounceDBAdapter(FeedbackNewList.this);
		announce.open();
		if(announce.isReadFeedback(_id)==1)
			return true;
		announce.close();
		return false;
	}

	public void itemView(int position) {
		Cursor cursor = (Cursor) list.getItemAtPosition(position);
		String feedbackNo = cursor.getString(cursor
				.getColumnIndexOrThrow("feedbackNo"));
		startActivity(new OpenContent(FeedbackNewList.this,
				AnnounceDBAdapter.SQLITE_FEEDBACK, feedbackNo).itemView());
	}

	void onRefresh() {

		// Toast.makeText(getApplicationContext(), "refreshList",
		// Toast.LENGTH_SHORT).show();
		// Log.e("myhandler 11","clicked");
		// Toast.LENGTH_SHORT).show();
		RotateAnimation ranim = (RotateAnimation)AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotateanim);
		sample_refresh.startAnimation(ranim);
//		SA VIKALP
		try {
			refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		EA VIKALP
		
		if(list!=null && list.getCount()<1){
			dbHelper = new AnnounceDBAdapter(getApplicationContext());
			dbHelper.open();
			populate();
			dbHelper.close();
		}
		// RecruitTask asyncHttpPost=new RecruitTask();
		// asyncHttpPost.execute();

	}
	
	private void refreshData(){

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("fID", "0");
		params.put("mName", "feedback");

		SharedPreferences prefs = FeedbackNewList.this
				.getSharedPreferences("MobCastPref", 0);
		String mobileNumber = prefs.getString("name", "akshay@bombil.com");

		// pref = getSharedPreferences("MobCastPref", 0);
		// String temp1 = pref.getString("name", "akshay@bombil.com");

		// params.put("emailID", temp1);
		params.put(com.mobcast.util.Constants.user_id, mobileNumber);
		AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
		asyncHttpPost.execute(com.mobcast.util.Constants.REFRESH_FEEDS);

		asyncHttpPost.setOnPostExecuteListener(new OnPostExecuteListener() {

			@Override
			public void onPostExecute(String result) {

				response = result;

				read1(result);
			}
		});

		// response = response.replace("[", "");
		// response = response.replace("]", "");
	}

	void read1(String response1) {
		// Log.d("inside read1",response1);
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(response1);

			// Log.d("Number of entries" , jsonArray.length());

//			int i = 0, j = 0;
//
//			if (jsonArray.length() > 5) {
//				j = jsonArray.length() - 5;
//			}
//			for (i = jsonArray.length() - 1; i > j; i--) {

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jObject = jsonArray.getJSONObject(i);

				String feedbackID = jObject.getString("ID");
				Log.d("feedbackID", feedbackID);

				HashMap<String, String> params = new HashMap<String, String>();
				params.put("feedbackID", feedbackID);
				params.put("companyID", getString(R.string.companyID));
				AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
				asyncHttpPost
						.execute(com.mobcast.util.Constants.CHECK_FEEDBACK);

				asyncHttpPost
						.setOnPostExecuteListener(new OnPostExecuteListener() {

							@Override
							public void onPostExecute(String result) {
								try {

									// Log.e("response in read1",result);
									writeData(result);

								} catch (Exception e) {
								}
							}
						});

				// Log.v("readfromxml", "xml is:" + xmlString);

				onPause();
				onResume();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void writeData(String response) {
		try {

			Log.d("response", response);
			JSONArray jsonArray = new JSONArray(response);
			int i, j;
			i = jsonArray.length();
			Log.d("Number of entries", i + "");

			Log.v("GCMIntent", "in feedback writeData");

			AnnounceDBAdapter db = new AnnounceDBAdapter(this);

			for (j = 0; j < i; j++) {
				JSONObject jObject1 = jsonArray.getJSONObject(j);
				String Fedbackrow = jObject1.getString("feedbackQuestion");
				String totalQuestions = jObject1.getString("totalQuestions");
				JSONObject jObject = new JSONObject(Fedbackrow);

				String feedbackID = jObject.getString("feedbackID");
				String feedbackTitle = jObject.getString("feedbackTitle");
				String feedbackDescription = jObject
						.getString("feedbackDescription");
//				SU ADDED VIKALP FEEDBACK DATE SORT ORDER
//				String feedbackDate = jObject.getString("feedbackDate");
				String feedbackDate = Utilities.convertdate(jObject.getString("feedbackDate"));
//				EU ADDED VIKALP FEEDBACK DATE SORT ORDER
				String feedbackAttempts = jObject.getString("feedbackAttempts");
				String questionNo = jObject.getString("questionNo");

				Log.d("question no",
						questionNo.substring(questionNo.lastIndexOf("/") + 1));
				questionNo = questionNo
						.substring(questionNo.lastIndexOf("/") + 1);

				String questionType = jObject.getString("questionType");
				String question = jObject.getString("question");
				String answerA = jObject.getString("answerA");
				String answerB = jObject.getString("answerB");
				String answerC = jObject.getString("answerC");
				String answerD = jObject.getString("answerD");
				String answerLimit = jObject.getString("answerLimit");
				// String contentExpiry = jObject.getString("contentExpiry");
				String contentExpiry = convertdate(jObject
						.getString("contentExpiry"));

				FeedbackDBHandler feedback = new FeedbackDBHandler();
				feedback.setFeedbackNo(feedbackID);
				feedback.setFeedbacktitle(feedbackTitle);
				feedback.setFeedbackDescription(feedbackDescription);
				feedback.setFeedbackDate(feedbackDate);
				feedback.setFeedbackAttempt(feedbackAttempts);
				feedback.setFeedbackTotalQuestions(totalQuestions);
				feedback.setQuestionNo(questionNo);
				feedback.setQuestionType(questionType);
				feedback.setQuestionTitle(question);
				feedback.setOptionA(answerA);
				feedback.setOptionB(answerB);
				feedback.setOptionC(answerC);
				feedback.setOptionD(answerD);
				feedback.setAnswerLimit(answerLimit);
				db.open();
				db.addFeedback(feedback, contentExpiry);
				db.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		onResume();
	}// end of writeData

	String convertdate(String dateString1) {
		java.util.Date date;
		try {
			date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			Log.e("error in converting date", "returning 9999-99-99");
			return "9999-99-99";
		}
		String dateString2 = new SimpleDateFormat("yyyy-MM-dd").format(date);
		Log.d("datestring2", dateString2);
		return dateString2;
	}// end of convert date
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
