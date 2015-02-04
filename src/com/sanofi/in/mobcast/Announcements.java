package com.sanofi.in.mobcast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Download.OnPostExecuteListener;
import com.mobcast.util.Utilities;

public class Announcements extends Activity implements OnClickListener {

	/*TextView title, detail, from, summary;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name,link;
	Button share;
	File file;
	
	Bitmap bitmap;
	ImageView main;
	Button btnShare;
	
	int _id;
	RelativeLayout base;
	Reports reports;
	Uri path;
	String aid;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;

	enum Direction {
		LEFT, RIGHT;
	}
*/
	TextView title, detail, from, summary, maintitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name,link;
	Button share;
	ImageView main;
	Button btnShare;
	Bitmap bitmap;
	int _id;
	File file;
	Reports reports;
	RelativeLayout base;
	Uri path, fpath;
	String aid;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = Announcements.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.announcement);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		title = (TextView) findViewById(R.id.tvAtitle);
		maintitle = (TextView) findViewById(R.id.maintitle);
		reports = new Reports(getApplicationContext(), "Announcements");
		maintitle.setText("Announcements");
		detail = (TextView) findViewById(R.id.tvDetail);
		from = (TextView) findViewById(R.id.tvFrom);
		summary = (TextView) findViewById(R.id.tvAsummary);
		main = (ImageView) findViewById(R.id.ibType);
		btnShare = (Button) findViewById(R.id.iv6);
		btnShare.setOnClickListener(myhandler1);
		btnShare.setOnTouchListener(myhandler2);
		base = (RelativeLayout) findViewById(R.id.base);
		// share = (Button) findViewById(R.id.bAshare);
		//bitmap = getImageBitmap(from.getText().toString().trim());
		onNewIntent(getIntent());
		// share.setOnClickListener(this);
		//main.setImageBitmap(bitmap);
		main.setOnClickListener(this);
		main.setOnTouchListener(otl);
		detail.setText(DateUtils.formatDate(Ddetail));
		title.setOnTouchListener(otl);
		detail.setOnTouchListener(otl);
		from.setOnTouchListener(otl);
		summary.setOnTouchListener(otl);
		ScrollView sv1 = (ScrollView) findViewById(R.id.scrollView1);
		sv1.setOnTouchListener(otl);

	}

	private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
       } catch (IOException e) {
           Log.e("Bitmap error", "Error getting bitmap", e);
       }
       return bm;
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

	OnTouchListener otl = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

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

	void FunctionDeleteRowWhenSlidingLeft() {

		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(
					Announcements.this);
			announce.open();
			Cursor cursor = announce.getRowNext(_id, "Announce");
			Log.v("Left slide cursor has ", cursor.getCount() + "");
			cursor.moveToFirst();

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));

			Log.v("TITLE", title);
			Log.v("Summary", summary);
			itemView(cursor, 0);

			announce.close();
		} catch (Exception e) {
		}

	}

	void FunctionDeleteRowWhenSlidingRight() {
		try {

			AnnounceDBAdapter announce = new AnnounceDBAdapter(
					Announcements.this);
			announce.open();
			Cursor cursor = announce.getRowPrivious(_id, "Announce");
			Log.v("Right slide cursor has ", cursor.getCount() + "");
			cursor.moveToFirst();

			String title = cursor.getString(cursor
					.getColumnIndexOrThrow("title"));
			String summary = cursor.getString(cursor
					.getColumnIndexOrThrow("summary"));

			Log.v("TITLE", title);
			Log.v("Summary", summary);
			itemView(cursor, 1);

			announce.close();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		/*
		 * case R.id.bAshare:
		 * 
		 * Intent sharingIntent = new
		 * Intent(android.content.Intent.ACTION_SEND);
		 * sharingIntent.setType("text/plain"); String shareBody =
		 * summary.getText().toString();
		 * sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		 * "Sharing"); sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT,
		 * shareBody); startActivity(Intent.createChooser(sharingIntent,
		 * "Share via"));
		 * 
		 * break;
		 */

		case R.id.ibType:

			Intent full = new Intent(this, ImageFullscreen.class);
			full.putExtra("name", name);
			full.putExtra("mname", "announcement");
			startActivity(full);

		}
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			reports.updateShare(aid);

			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");

			share.putExtra(Intent.EXTRA_STREAM, path);
			share.putExtra(Intent.EXTRA_TEXT, " " + summary.getText());
			share.putExtra(Intent.EXTRA_SUBJECT, title.getText());
			startActivity(Intent.createChooser(share, "Share Image"));

		}
	};

	@Override
	public void onNewIntent(Intent intent) {

		/*Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dfrom = intent.getStringExtra("from");
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");
		link = intent.getStringExtra("link");
		aid = intent.getStringExtra("id");
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		// Toast.makeText(getBaseContext(), ""+_id, Toast.LENGTH_SHORT).show();
		if (intent.getStringExtra("share").equals("off"))
			btnShare.setVisibility(Button.INVISIBLE);
		Log.d("Announcement", Ddetail);
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/.mobcast/mobcast_images");
		myDir.mkdirs();
		Log.d("Hallahoo", String.valueOf(myDir.mkdirs()));
		String fname = name;
		file = new File(myDir, fname);
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText("By : " + Dfrom);
		summary.setText(Dsummary);
		Log.d("Hallooo", String.valueOf(fname));
		if (file.exists()) {
			
			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			main.setImageBitmap(bitmap);
			path = Uri.parse("file://" + file.getAbsolutePath());
		} else {
			// TODO download files async with dialog
			Download d = new Download(Announcements.this,
					AnnounceDBAdapter.SQLITE_ANNOUNCE, _id + "");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					main.setImageBitmap(bitmap);
					path = Uri.parse("file://" + file.getAbsolutePath()); 

				}
			});
		}*/
		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
//		SU VIKALP
//		Dfrom = intent.getStringExtra("link");
		Dfrom = intent.getStringExtra("from");
//		EU VIKALP
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");
		aid = intent.getStringExtra("id");
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		if (intent.getStringExtra("share").equals("off"))
			btnShare.setVisibility(Button.INVISIBLE);

		// Toast.makeText(getBaseContext(), ""+_id, Toast.LENGTH_SHORT).show();

		// url = intent.getStringExtra("");

		Log.d("Announcement", Ddetail);

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_IMG);
		myDir.mkdirs();
		String fname = name;
		
		try{
		file = new File(myDir, fname);}
		catch(Exception e){
			e.printStackTrace();
		}
		title.setText(Dtitle);
		detail.setText(Ddetail);
//		SU VIKALP
		from.setVisibility(View.VISIBLE);
		from.setText("By : "+Dfrom);
//		from.setTextColor(Color.BLUE);
//		EU VIKALP
		summary.setText(Dsummary);
		Log.d("Hallahoo", String.valueOf(myDir.mkdirs()));
		Log.d("Hallooo", fname);
		if (file.exists()) {

			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			main.setImageBitmap(bitmap);
			
			path = Uri.fromFile(file);
			//path = Uri.parse("file://" + file.getAbsolutePath());
		} else {
			Download d = new Download(Announcements.this,
					AnnounceDBAdapter.SQLITE_ANNOUNCE, _id + "");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					main.setImageBitmap(bitmap);
					
					path = Uri.fromFile(file);
					//path = Uri.parse("file://" + file.getAbsolutePath());
				}
			});
		}

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "Announce");
		announce.close();
		reports.updateRead(aid);
		
//		SA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
		try{
			Utilities.cancelLolliPopNotification(ApplicationLoader.getApplication());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		EA VIKALP ADDED CANCEL LOLLIPOP NOTIFICATION
	}

	/*@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent i = new Intent(Announcements.this,Home1.class);
		startActivity(i);
		finish();
		
		
	}
*/
	public void itemView(Cursor cursor, int direction) {

		// Get the state's capital from this row in the database.
		String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
		String detail = cursor
				.getString(cursor.getColumnIndexOrThrow("detail"));
		String from = cursor.getString(cursor.getColumnIndexOrThrow("fro"));
		String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
		String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
		String summary = cursor.getString(cursor
				.getColumnIndexOrThrow("summary"));
		String aid = cursor.getString(cursor.getColumnIndexOrThrow("aid"));
		String _id = cursor.getString(cursor.getColumnIndexOrThrow("_id"));

		Intent show;
		if (type.contentEquals("image")) {
			show = new Intent(getBaseContext(), Announcements.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			finish();
		} else if (type.contentEquals("video")) {
			// show = new Intent(AnnounceListView.this , AnnounceVideo.class);
			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_VIDEO + name);
			show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file), "video/*");
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
		} else if (type.equals("audio"))

		{

			File file = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + Constants.APP_FOLDER_AUDIO + name);
			show = new Intent(Intent.ACTION_VIEW);
			show.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
			show.setAction(Intent.ACTION_VIEW);
			show.setDataAndType(Uri.fromFile(file), "audio/mp3");
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
		}// end of audio
		else {
			show = new Intent(getBaseContext(), AnnounceText.class);
			show.putExtra("title", title);
			show.putExtra("detail", detail);
			show.putExtra("from", from);
			show.putExtra("type", type);
			show.putExtra("name", name);
			show.putExtra("summary", summary);
			show.putExtra("id", aid);
			show.putExtra("_id", _id);
			startActivity(show);
			if (direction == 0)
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			else
				overridePendingTransition(R.anim.push_down_in,
						R.anim.push_down_out);
			finish();
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