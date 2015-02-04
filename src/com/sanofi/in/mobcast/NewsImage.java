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
import android.graphics.Color;
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
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class NewsImage extends Activity implements OnClickListener {

	TextView title, detail, from, summary, maintitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name;
	Button share;
	ImageView main;
	Button btnShare;
	Bitmap bitmap;
	int _id;
	Reports reports;
	RelativeLayout base;
	Uri path;
	String aid;
	float historicX = Float.NaN, historicY = Float.NaN;
	static final int DELTA = 50;
	private static final String TAG = NewsImage.class.getSimpleName();

	enum Direction {
		LEFT, RIGHT;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		reports = new Reports(getApplicationContext(), "News");
		maintitle.setText("News");
		detail = (TextView) findViewById(R.id.tvDetail);
		from = (TextView) findViewById(R.id.tvFrom);
		from.setText("Go to Source");
		summary = (TextView) findViewById(R.id.tvAsummary);
		main = (ImageView) findViewById(R.id.ibType);
		btnShare = (Button) findViewById(R.id.iv6);
		btnShare.setOnClickListener(myhandler1);
		btnShare.setOnTouchListener(myhandler2);
		base = (RelativeLayout) findViewById(R.id.base);
		// share = (Button) findViewById(R.id.bAshare);
		onNewIntent(getIntent());
		// share.setOnClickListener(this);
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
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				historicX = event.getX();
				historicY = event.getY();
				break;

			case MotionEvent.ACTION_UP:
				if (event.getX() - historicX < -DELTA) {
 
					    
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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsImage.this);
			announce.open();
			Cursor cursor = announce.getRowNextNews(_id);
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

			AnnounceDBAdapter announce = new AnnounceDBAdapter(NewsImage.this);
			announce.open();
			// Cursor cursor = announce.getRowPrivious(_id, "Announce");
			Cursor cursor = announce.getRowPriviousNews(_id);
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
		// TODO Auto-generated method stub
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
			full.putExtra("mname", "news");
			full.putExtra("name", name);
			startActivity(full);

		}
	}

	View.OnClickListener myhandler1 = new View.OnClickListener() {
		public void onClick(View v) {

			reports.updateShare(aid);

			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");

			share.putExtra(Intent.EXTRA_STREAM, path);
			// share.putExtra(Intent.EXTRA_TEXT,
			// "Source "+from.getText()+" - "+summary.getText());

			String shareBody = Dfrom
					+ "\n ON: " + detail.getText() + "\n" + summary.getText();

			share.putExtra(Intent.EXTRA_SUBJECT, title.getText());
			share.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

			startActivity(Intent.createChooser(share, "Share Image"));

		}
	};

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dfrom = intent.getStringExtra("link");
		Dsummary = intent.getStringExtra("summary");
		name = intent.getStringExtra("name");
		aid = intent.getStringExtra("id");
		_id = Integer.parseInt(intent.getStringExtra("_id"));
		String shareflag = intent.getStringExtra("shareKey");
		if (shareflag.trim().contentEquals("off"))
			btnShare.setVisibility(Button.INVISIBLE);

		// Toast.makeText(getBaseContext(), ""+_id, Toast.LENGTH_SHORT).show();

		// url = intent.getStringExtra("");

		Log.d("Announcement", Ddetail);

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_IMG);
		myDir.mkdirs();
		String fname = name;
		final File file = new File(myDir, fname);
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText("Go to source");
		from.setTextColor(Color.BLUE);
		summary.setText(Dsummary);
		Log.d("Hallahoo", String.valueOf(myDir.mkdirs()));
		Log.d("Hallooo", fname);
		if (file.exists()) {

			bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			main.setImageBitmap(bitmap);

			path = Uri.fromFile(file);
		} else {
			Download d = new Download(NewsImage.this,
					AnnounceDBAdapter.SQLITE_NEWS, _id + "");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					main.setImageBitmap(bitmap);

					path = Uri.fromFile(file);
				}
			});
		}

		try {

			from.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					reports.updateLinkCLicked(aid);

					// Toast.makeText(getApplicationContext(), "Text Touch",
					// Toast.LENGTH_SHORT).show();
					url = Dfrom;
					if (url.contains("http")) {

						Intent i = new Intent(Intent.ACTION_VIEW, Uri
								.parse(url));
						startActivity(i);
					} else {
						Intent i = new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://" + url));
						startActivity(i);

					}

				}
			});
		} catch (Exception e) {
		}

		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "News");
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

	public void itemView(Cursor cursor, int direction) {

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