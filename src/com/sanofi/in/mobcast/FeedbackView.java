package com.sanofi.in.mobcast;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Utilities;

public class FeedbackView extends Activity {

	TextView maintitle, atitle, doctime, docby, summarytv, det;
	Button btn;
	String title, time, summary, feedbackNo, feedtotalquestions;
	int _id;
	Reports reports;
	public static final String NAVIGATED_FROM_NEW_LIST = "navigatedfromnewlist";
	ScrollView sv;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = FeedbackView.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedbackview);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		reports = new Reports(getApplicationContext(), "Feedback");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();

		maintitle = (TextView) findViewById(R.id.maintitle);
		det = (TextView) findViewById(R.id.tvDetails);
		det.setOnTouchListener(otl);
		atitle = (TextView) findViewById(R.id.tvATtitle);
		sv = (ScrollView) findViewById(R.id.scrollView1);
		sv.setOnTouchListener(otl);
		atitle.setOnTouchListener(otl);
		doctime = (TextView) findViewById(R.id.tvDoctime);
		doctime.setOnTouchListener(otl);
		// docby = (TextView) findViewById(R.id.tvDocBy);
		summarytv = (TextView) findViewById(R.id.tvATsummary);
		summarytv.setOnTouchListener(otl);

		btn = (Button) findViewById(R.id.BtnOpenFile);
		btn.setOnTouchListener(myhandler2);
		onNewIntent(getIntent());
		btn.setOnClickListener(myhandler1);
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
					FunctionDeleteRowWhenSlidingLeft();
					return true;
				} else if (event.getX() - historicX > DELTA) {
					FunctionDeleteRowWhenSlidingRight();
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
		// Toast.makeText(getApplicationContext(), "right",
		// Toast.LENGTH_SHORT).show();

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
		// Toast.makeText(getApplicationContext(), "left",
		// Toast.LENGTH_SHORT).show();
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			// Toast.makeText(getBaseContext(), "clicked",
			// Toast.LENGTH_SHORT).show();
			try {

				AnnounceDBAdapter adb = new AnnounceDBAdapter(FeedbackView.this);
				adb.open();
				Cursor c = adb.fetchAllFeedbackForAnswer(feedbackNo);
				adb.updateFeedbackCount(Integer.parseInt(feedbackNo));
				String x = c.getString(c.getColumnIndexOrThrow("questionType"));
				int _id = Integer.parseInt(c.getString(c
						.getColumnIndexOrThrow("_id")));
				Log.v(" feedBack type ", x);
				viewNextfeedbackQuestion(_id, x);
				adb.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	};

	void viewNextfeedbackQuestion(int _id, String type) {

		try {
			Intent show;
			if (type.contentEquals("Subjective")) {
				show = new Intent(FeedbackView.this,
						FeedbackNewSubjective.class);
				show.putExtra("_id", _id + "");
				show.putExtra("NAVIGATED_FROM_NEW_LIST", "true");
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Rating")) {
				show = new Intent(FeedbackView.this, FeedbackNewRating.class);
				show.putExtra("_id", _id + "");
				show.putExtra("NAVIGATED_FROM_NEW_LIST", "true");
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Multiple")) {
				show = new Intent(FeedbackView.this, FeedbackNewRadio.class);
				show.putExtra("NAVIGATED_FROM_NEW_LIST", "true");
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Checkbox")) {
				show = new Intent(FeedbackView.this, FeedbackNewCheck.class);
				show.putExtra("_id", _id + "");
				show.putExtra("NAVIGATED_FROM_NEW_LIST", "true");
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			finish();

		} catch (Exception e) {
			Log.e("error fetching ", "feedback");
		}

	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

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

		title = intent.getStringExtra("title");
		time = intent.getStringExtra("detail");
		summary = intent.getStringExtra("summary");
		feedbackNo = intent.getStringExtra("feedbackNo");
		System.out.print("feedbackNo  >  "+feedbackNo+"\n");
		feedtotalquestions = intent.getStringExtra("feedtotalquestions");
		// System.out.println(" questions  "+feedtotalquestions);
		Date date;
		String final_date = null;
		SimpleDateFormat dateformat = new SimpleDateFormat("dd-mm-yyyy");
		try {

			date = dateformat.parse(intent.getStringExtra("detail"));
			final_date = date.toString();
			Log.v("final_date", final_date);
			// Log.v("final_questions", feedtotalquestions);
			// System.out.println(""+final_date);
		} catch (Exception e) {
			System.out.println("exception :::" + e);
		}
		atitle.setText(title);
		int start = 0, last = 10;
		btn.setText("Begin " + feedtotalquestions + " Question/s");
		String final_date1 = final_date.substring(start, last);

		doctime.setText(DateUtils.formatDate(time));
		summarytv.setText(summary);

		/*
		 * ImageView im; TextView maintitle, atitle, doctime, docby, summarytv;
		 */
		// Toast.makeText(getBaseContext(), "open", Toast.LENGTH_SHORT).show();
		reports.updateRead(feedbackNo);
//		SC VIKALP FEEDBACK READ ONLY ON SUBMIT
//		AnnounceDBAdapter announce = new AnnounceDBAdapter(getApplicationContext());
//		announce.open();
//		announce.readrow(feedbackNo+"", AnnounceDBAdapter.SQLITE_FEEDBACK);
//		announce.close();
//		EC VIKALP FEEDBACK READ ONLY ON SUBMIT
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
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
