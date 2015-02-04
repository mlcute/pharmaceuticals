package com.sanofi.in.mobcast;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class Search extends Activity {
	TextView none;
	ImageButton search;
	EditText et;
	String Tablename;
	Cursor c;
	ListView lv2;
	private SimpleCursorAdapter dataAdapter;

	AnnounceDBAdapter adb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		none = (TextView) findViewById(R.id.None);
		search = (ImageButton) findViewById(R.id.sample_button);
		et = (EditText) findViewById(R.id.editText1);
		lv2 = (ListView) findViewById(R.id.listView2);
		adb = new AnnounceDBAdapter(Search.this);

		search.setOnClickListener(myhandler1);
		search.setOnTouchListener(myhandler2);
		onNewIntent(getIntent());

		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					myhandler1.onClick(search);

					return true;
				}
				return false;
			}

		});

		et.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				myhandler1.onClick(search);
			}
		});

	}

	OnClickListener myhandler1 = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// none.setVisibility(TextView.VISIBLE);

			adb.open();
			c = adb.Search(Tablename, et.getText().toString());

			if (c.getCount() > 0) {
				none.setVisibility(TextView.GONE);
				// Toast.makeText(getBaseContext(), "count = "+c.getCount(),
				// Toast.LENGTH_SHORT).show();
				DisplayListView();
				lv2.setVisibility(ListView.VISIBLE);
			}

			else {
				none.setVisibility(TextView.VISIBLE);
				lv2.setVisibility(ListView.INVISIBLE);
			}
			adb.close();
			onResume();

		}
	};

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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		Tablename = intent.getStringExtra("tablename");
		Log.v("TableName", Tablename);

	}

	
	@SuppressLint("NewApi")
	public void DisplayListView() {
		
		if(android.os.Build.VERSION.SDK_INT>=11) { 
		String[] columns = null;

		if (Tablename.equals("Feedback")) {
			columns = new String[] { "feedbackTitle", "feedbackDescription" };
		} else {
			columns = new String[] { AnnounceDBAdapter.KEY_TITLE,
					AnnounceDBAdapter.KEY_SUMMARY };
		}

		int[] to = new int[] { R.id.tvAListTitle, R.id.tvAListSummary };

		dataAdapter = new SimpleCursorAdapter(this, R.layout.searchlisting, c,
				columns, to, 0);
		lv2.setAdapter(dataAdapter);

		lv2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,
					int position, long id) {

			//	itemView(position);
				Cursor cursor = (Cursor) lv2.getItemAtPosition(position);
				String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
				startActivity(new OpenContent(getApplicationContext(), Tablename, _id).itemView());
			}
		});
		}
	}

	public void itemView(int position) {

		String share = "off";

		if (Tablename.equals("Announce")) {
			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);
			// Toast.makeText(getBaseContext(), ""+position,
			// Toast.LENGTH_LONG).show();
			Log.v("position in listview", "" + position);

			// Get the state's capital from this row in the database.
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("detail"));
			String from = cursor.getString(cursor.getColumnIndexOrThrow("fro"));
			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			share = cursor.getString(cursor.getColumnIndexOrThrow("shareKey"));

			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));
			String aid = cursor.getString(cursor.getColumnIndexOrThrow("aid"));
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

			if (Integer.parseInt(cursor.getString(cursor
					.getColumnIndexOrThrow("read_id"))) == 0) {
				// to set this announcement as read (setting read_id in database
				// row as 1)
				AnnounceDBAdapter announce = new AnnounceDBAdapter(Search.this);
				announce.open();
				// announce.setRead(title, summary, "Announce");
				announce.readrow(_id, "Announce");

				announce.close();

				// to send report to server
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("fID", aid);
				params.put("mName", "announcement");
				params.put("companyID", getString(R.string.companyID));
				AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
				asyncHttpPost
						.execute("http://www.mobcast.in/ncp/updateRead.php");

			}

			Intent show;
			if (type.contentEquals("image")) {
				show = new Intent(Search.this, Announcements.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("from", from);
				show.putExtra("type", type);
				show.putExtra("name", name);
				show.putExtra("summary", summary);
				show.putExtra("share", share);
				show.putExtra("id", aid);
				show.putExtra("_id", _id);
				startActivity(show);
			} else if (type.contentEquals("video")) {
				show = new Intent(Search.this, AnnounceVideo.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("from", from);
				show.putExtra("type", type);
				show.putExtra("name", name);
				show.putExtra("share", share);
				show.putExtra("summary", summary);
				show.putExtra("id", aid);
				show.putExtra("_id", _id);
				// File file = new
				// File(Environment.getExternalStorageDirectory().getAbsolutePath()
				// + "/.mobcast/mobcast_videos/"+name);
				// show = new Intent(Intent.ACTION_VIEW);
				// show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				// show.setAction(Intent.ACTION_VIEW);
				// show.setDataAndType(Uri.fromFile(file), "video/*");

				startActivity(show);
			} else if (type.equals("audio"))

			{

				/*
				 * File file = new
				 * File(Environment.getExternalStorageDirectory()
				 * .getAbsolutePath() + "/.mobcast/mobcast_audio/"+name); show =
				 * new Intent(Intent.ACTION_VIEW);
				 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
				 * show.setAction(Intent.ACTION_VIEW);
				 * show.setDataAndType(Uri.fromFile(file), "audio/mp3");
				 * NotificationManager mNotificationManager =
				 * (NotificationManager)
				 * getSystemService(Context.NOTIFICATION_SERVICE);
				 * mNotificationManager.cancelAll(); startActivity(show);
				 */

				show = new Intent(Search.this, audio.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("from", from);
				show.putExtra("share", share);
				show.putExtra("type", type);
				show.putExtra("name", name);
				show.putExtra("summary", summary);
				show.putExtra("id", aid);
				show.putExtra("_id", _id);

				startActivity(show);

			}// end of audio
			else {
				show = new Intent(Search.this, AnnounceText.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("from", from);
				show.putExtra("type", type);
				show.putExtra("share", share);
				show.putExtra("name", name);
				show.putExtra("summary", summary);
				show.putExtra("id", aid);
				show.putExtra("_id", _id);
				startActivity(show);
			}

		}
		// end of table announce

		else if (Tablename.equals("Event")) {

			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);

			// Get the state's capital from this row in the database.
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String date = cursor
					.getString(cursor.getColumnIndexOrThrow("date"));
			String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
			String time = cursor
					.getString(cursor.getColumnIndexOrThrow("time"));
			String venue = cursor.getString(cursor
					.getColumnIndexOrThrow("venue"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));
			String rsvp = cursor
					.getString(cursor.getColumnIndexOrThrow("rsvp"));
			String eid = cursor.getString(cursor.getColumnIndexOrThrow("eid"));
			share = cursor.getString(cursor.getColumnIndexOrThrow("shareKey"));
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			String calenderEnabled = cursor.getString(cursor.getColumnIndexOrThrow("calenderEnabled"));
			String rsvpNeeded = cursor.getString(cursor.getColumnIndexOrThrow("rsvpNeeded"));

			Log.d("title", title);
			Log.d("date", date);
			Log.d("day", day);
			Log.d("time", time);
			Log.d("venue", venue);
			Log.d("summary", summary);
			Log.d("rsvp", rsvp);
			Log.d("eid", eid);
			Log.d("share", share);
			Log.d("_id", _id);
			Log.d("calenderEnabled",calenderEnabled);
			Log.d("rsvpNeeded",rsvpNeeded);
			Intent show = new Intent(Search.this, Event.class);
			show.putExtra("title", title);
			show.putExtra("date", date);
			show.putExtra("day", day);
			show.putExtra("time", time);
			show.putExtra("rsvp", rsvp);
			show.putExtra("venue", venue);
			show.putExtra("summary", summary);
			show.putExtra("shareKey", share);
			show.putExtra("id", eid);
			show.putExtra("_id", _id);
			show.putExtra("calenderEnabled", calenderEnabled);
			show.putExtra("rsvpNeeded", rsvpNeeded);

			show.putExtra("pos", position + "");
			startActivity(show);

			// Toast.makeText(getApplicationContext(),
			// title, Toast.LENGTH_SHORT).show();

		}

		// end of events

		else if (Tablename.equals("News")) {

			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);

			// Get the state's capital from this row in the database.
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("detail"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));
			String tid = cursor.getString(cursor.getColumnIndexOrThrow("nid"));
			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));
			String link = cursor
					.getString(cursor.getColumnIndexOrThrow("link"));
			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));
			share = cursor.getString(cursor.getColumnIndexOrThrow("shareKey"));
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

			Intent show;
			if (type.contentEquals("video")) {
				show = new Intent(Search.this, NewsVideo.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("name", name);
				show.putExtra("link", link);
				show.putExtra("summary", summary);
				show.putExtra("shareKey", share);
				show.putExtra("id", tid);
				show.putExtra("_id", _id);
				startActivity(show);
			}

			else if (type.contentEquals("image")) {
				show = new Intent(Search.this, NewsImage.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("name", name);
				show.putExtra("link", link);
				show.putExtra("shareKey", share);
				show.putExtra("summary", summary);
				show.putExtra("id", tid);
				show.putExtra("_id", _id);
				startActivity(show);
			} else if (type.contentEquals("audio")) {
				show = new Intent(Search.this, NewsAudio.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("name", name);
				show.putExtra("link", link);
				show.putExtra("shareKey", share);
				show.putExtra("summary", summary);
				show.putExtra("id", tid);
				show.putExtra("_id", _id);
				startActivity(show);
			}

			else {
				show = new Intent(Search.this, NewsText.class);
				show.putExtra("title", title);
				show.putExtra("detail", detail);
				show.putExtra("name", name);
				show.putExtra("link", link);
				show.putExtra("summary", summary);
				show.putExtra("shareKey", share);
				show.putExtra("id", tid);
				show.putExtra("_id", _id);
				startActivity(show);
			}

			// Toast.makeText(getApplicationContext(),
			// title, Toast.LENGTH_SHORT).show();

		}

		// end of news

		else if (Tablename.equals("Award")) {
			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);

			// Get the state's capital from this row in the database.
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("detail"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));

			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));
			String imgPath = cursor.getString(cursor
					.getColumnIndexOrThrow("imagePath"));

			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
			share = cursor.getString(cursor.getColumnIndexOrThrow("shareKey"));

			String aid = cursor.getString(cursor
					.getColumnIndexOrThrow("awardId"));

			if (Integer.parseInt(cursor.getString(cursor
					.getColumnIndexOrThrow("read_id"))) == 0) {
				// to set this announcement as read (setting read_id in database
				// row as 1)
				AnnounceDBAdapter announce = new AnnounceDBAdapter(Search.this);
				announce.open();
				announce.readrow(cursor.getString(cursor.getColumnIndexOrThrow("_id")),"Award");
				announce.close();

			}

			Intent i = new Intent(Search.this, Award.class);

			i.putExtra("title", title);
			i.putExtra("detail", detail);
			i.putExtra("summary", summary);
			i.putExtra("imagePath", imgPath);
			i.putExtra("name", name);
			i.putExtra("shareKey", share);
			i.putExtra("aid", aid);
			i.putExtra("_id", _id);
			startActivity(i);

		}

		else if (Tablename.equals("Feedback")) {
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("feedbackTitle"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("feedbackDate"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("feedbackDescription"));
			String feedbackNo = cursor.getString(cursor
					.getColumnIndexOrThrow("feedbackNo"));

			Intent show;
			show = new Intent(Search.this, FeedbackView.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			// show.putExtra("from", from);

			show.putExtra("feedbackNo", feedbackNo);

			show.putExtra("summary", summary);

			// show.putExtra("_id", _id);
			startActivity(show);

		}


		
		// end of documents

		else if (Tablename.equals("Training")) {

			// Get the cursor, positioned to the corresponding row in the result
			// set
			Cursor cursor = (Cursor) lv2.getItemAtPosition(position);

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String detail = cursor.getString(cursor
					.getColumnIndexOrThrow("detail"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));
			String tid = cursor.getString(cursor.getColumnIndexOrThrow("tid"));
			String name = cursor
					.getString(cursor.getColumnIndexOrThrow("name"));
			String ename = cursor
					.getString(cursor.getColumnIndexOrThrow("ename"));
			share = cursor.getString(cursor.getColumnIndexOrThrow("shareKey"));
			String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

			String type = cursor
					.getString(cursor.getColumnIndexOrThrow("type"));

			Log.v("Training type", type);
			try {

				Intent show;

				if (type.equals("video")) {
					show = new Intent(Search.this, TrainingVideo.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("share", share);
					show.putExtra("_id", _id);
					// File file = new
					// File(Environment.getExternalStorageDirectory().getAbsolutePath()
					// + "/.mobcast/mobcast_videos/"+name);
					// show = new Intent(Intent.ACTION_VIEW);
					// show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					// show.setAction(Intent.ACTION_VIEW);
					// show.setDataAndType(Uri.fromFile(file), "video/*");

					startActivity(show);

				}// end of video
				
				if (type.equals("doc"))

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

					show = new Intent(Search.this, Document.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("share", share);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("mtitle", "Training");
					show.putExtra("ename", ename);
					show.putExtra("_id", _id);
					startActivity(show);

				}// end of pdf

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

					show = new Intent(Search.this, Document.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("share", share);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("mtitle", "Training");
					show.putExtra("ename", ename);
					show.putExtra("_id", _id);
					startActivity(show);

				}// end of pdf
				
				
				if (type.equals("xls"))

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

					show = new Intent(Search.this, Document.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("share", share);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("mtitle", "Training");
					show.putExtra("ename", ename);
					show.putExtra("_id", _id);
					startActivity(show);

				}// end of pdf
				
				
				
				

				if (type.equals("ppt"))

				{
					/*
					 * Intent show; File file = new
					 * File(Environment.getExternalStorageDirectory
					 * ().getAbsolutePath() + "/.mobcast/"+getString(R.string.ppt)+"/"+name);
					 * show = new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file),
					 * "application/vnd.ms-powerpoint"); startActivity(show);
					 */

					// Intent show;
					show = new Intent(Search.this, Document.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("id", tid);
					show.putExtra("ename", ename);
					show.putExtra("share", share);
					show.putExtra("mtitle", "Training");
					show.putExtra("_id", _id);
					startActivity(show);

				}// end of ppt

				if (type.equals("audio"))

				{

					// Intent show;
					/*
					 * File file = new
					 * File(Environment.getExternalStorageDirectory
					 * ().getAbsolutePath() + "/.mobcast/mobcast_audio/"+name);
					 * show = new Intent(Intent.ACTION_VIEW);
					 * show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					 * show.setAction(Intent.ACTION_VIEW);
					 * show.setDataAndType(Uri.fromFile(file), "audio/mp3");
					 * startActivity(show);
					 */

					show = new Intent(Search.this, TrainingAudio.class);
					show.putExtra("title", title);
					show.putExtra("detail", detail);
					// show.putExtra("from", from);
					show.putExtra("type", type);
					show.putExtra("name", name);
					show.putExtra("summary", summary);
					show.putExtra("share", share);
					show.putExtra("ename", ename);
					show.putExtra("id", tid);
					show.putExtra("_id", _id);

					startActivity(show);

				}// end of audio
				
				
				

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getBaseContext(), "Suitable App not Found",
						Toast.LENGTH_SHORT).show();
			}

		}
		// end of training

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
