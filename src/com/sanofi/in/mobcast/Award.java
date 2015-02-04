package com.sanofi.in.mobcast;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

public class Award extends Activity {

	String titleString, timeString, summaryString, imagePath, aid;
	TextView title, time, summary, name, type;
	ImageButton ccount;
	ImageView image, share;
	String filename;
	Reports reports;
	File f;
	ScrollView sv;
	int _id;
	Bitmap bitmap; // SA VIKALP IMAGE AWARD FIXED
	Uri path;
	File file; // EA VIKALP IMAGE AWARD FIXED

	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = Award.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(0.5f);}
				v.getBackground().setAlpha(45);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
				v.getBackground().setAlpha(255);
			}

			return false;
		}

	};

	View.OnClickListener myhandler3 = new View.OnClickListener() {
		public void onClick(View v) {

			// Toast.makeText(AnnounceText.this, "Test",
			// Toast.LENGTH_SHORT).show();

			reports.updateShare(aid);

			// works for everyapp except facebook
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.setType("text/plain");
			sharingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
			// sharingIntent.setType("message/rfc822");
			String shareBody = type.getText() + "\n" + title.getText() + "\n"
					+ summary.getText();
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					title.getText());
			sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM,
					Uri.fromFile(f));

			sharingIntent
					.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
			startActivity(Intent.createChooser(sharingIntent, "Share via"));

			/*
			 * Intent sharingIntent = new
			 * Intent(android.content.Intent.ACTION_SEND);
			 * sharingIntent.setType("text/plain"); List activities =
			 * getPackageManager().queryIntentActivities(sharingIntent,0); //
			 * sharingIntent.setClassName(null,null);
			 * sharingIntent.setType("text/plain");
			 * sharingIntent.putExtra(android
			 * .content.Intent.EXTRA_SUBJECT,"hello");
			 * sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
			 * "facebook");
			 */

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.award);
		share = (ImageButton) findViewById(R.id.cshare);
		share.setOnClickListener(myhandler3);
		share.setOnTouchListener(myhandler2);
		reports = new Reports(getApplicationContext(), "Award");
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}

		title = (TextView) findViewById(R.id.awardTitle);
		time = (TextView) findViewById(R.id.awardTime);
		summary = (TextView) findViewById(R.id.awardSummary);
		type = (TextView) findViewById(R.id.awardType);
		image = (ImageView) findViewById(R.id.awardImage);
		sv = (ScrollView) findViewById(R.id.sv);
		ccount = (ImageButton) findViewById(R.id.ccount);
		ccount.setOnClickListener(myhandler1);

		title.setOnTouchListener(otl);
		time.setOnTouchListener(otl);

		summary.setOnTouchListener(otl);
		sv.setOnTouchListener(otl);

		onNewIntent(getIntent());

		image.setOnClickListener(zoomhandler);
		time.setText(DateUtils.formatDate(timeString));

	}// end of onCreate

	OnClickListener zoomhandler = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent full = new Intent(Award.this, ImageFullscreen.class);
			full.putExtra("name", filename);
			full.putExtra("mname", "award");
			startActivity(full);

		}
	};

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
					Log.v("OnTouchListener", "left swipe");

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

	public void itemView(Cursor cursor, int direction) {
		// Get the cursor, positioned to the corresponding row in the result set

		// Get the state's capital from this row in the database.
		String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		String detail = cursor
				.getString(cursor.getColumnIndexOrThrow("detail"));
		String summary = cursor.getString(cursor
				.getColumnIndexOrThrow("summary"));

		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

		String imgPath = cursor.getString(cursor
				.getColumnIndexOrThrow("imagePath"));
		filename = imgPath;

		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
		aid = cursor.getString(cursor.getColumnIndexOrThrow("awardId"));

		Log.e("awardid:", aid + "");

		AnnounceDBAdapter announce = new AnnounceDBAdapter(Award.this);
		announce.open();
		announce.readrow(cursor.getString(cursor.getColumnIndexOrThrow("_id")),
				"Award");
		announce.close();

		Intent i = new Intent(Award.this, Award.class);

		i.putExtra("title", title);
		i.putExtra("aid", aid);
		i.putExtra("detail", detail);
		i.putExtra("summary", summary);
		i.putExtra("name", name);
		i.putExtra("imagePath", imgPath);
		i.putExtra("_id", _id);
		startActivity(i);
		if (direction == 0)
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		else
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);

	}

	@Override
	public void onNewIntent(Intent intent) {

		titleString = intent.getStringExtra("title");
		String name = intent.getStringExtra("name");
		// Toast.makeText(getBaseContext(), name, Toast.LENGTH_SHORT).show();
		timeString = intent.getStringExtra("detail");
		summaryString = intent.getStringExtra("summary");
		imagePath = intent.getStringExtra("imagePath");

		// SA VIKALP AWARD IMAGE FIXED
		int index = imagePath.lastIndexOf("/");
		filename = imagePath.substring(index + 1);
		// EA VIKALP AWARD IMAGE FIXED
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		aid = intent.getStringExtra("aid");
		title.setText(name);
		type.setText(titleString);
		Log.e("awardid:", aid + "");
		// title.setText(titleString);
		time.setText(timeString);
		summary.setText(summaryString);

		// SA VIKALP IMAGE AWARD FIXED
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_IMG);
		myDir.mkdirs();
		String fname = filename;
		try {
			file = new File(myDir, fname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("Hallahoo", String.valueOf(myDir.mkdirs()));
		Log.d("Hallooo", fname);
		// EA VIKALP IMAGE AWARD FIXED
		f = new File(imagePath);
		// SU VIKALP IMAGE AWARD FIXED
		// if (f.exists()) {
		if (file.exists()) {
			// EU VIKALP IMAGE AWARD FIXED
			// SU VIKALP IMAGE AWARD FIXED
            // Bitmap bmp = BitmapFactory.decodeFile(imagePath);
            // image.setImageBitmap(bmp);
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            image.setImageBitmap(bitmap);
            path = Uri.fromFile(file);
            // EU VIKALP IMAGE AWARD FIXED
		} else {
			Download d = new Download(Award.this,
					AnnounceDBAdapter.SQLITE_AWARD, _id + "");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {
				public void onPostExecute(String result) {
					// SU VIKALP IMAGE AWARD FIXED
					// Bitmap bmp = BitmapFactory.decodeFile(imagePath);
					// image.setImageBitmap(bmp);
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    image.setImageBitmap(bitmap);
                    path = Uri.fromFile(file);
                    // EU VIKALP IMAGE AWARD FIXED
				}
			});
		}

		String shareflag = intent.getStringExtra("shareKey");
		if (shareflag.trim().contentEquals("off"))
			share.setVisibility(ImageButton.INVISIBLE);

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "Award");
		announce.close();
		// SU VIKALP SENDING CONGRATULATE INSTEAD OF READ
		// reports.updateCongratulate(aid);
		reports.updateRead(aid); // SENDING CONGRATULATE ON READ
		// EU VIKALP SENDING CONGRATULATE INSTEAD OF READ
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
	}// end of onNewIntent

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {
			Toast.makeText(getBaseContext(),
					"You just congratulated " + title.getText(),
					Toast.LENGTH_SHORT).show();
			// use asynchttppost class methods to send increase congratulation
			// count on the server
			reports.updateCongratulate(aid);
		}
	};

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

}// end of Award class
