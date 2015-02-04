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
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.Document.SendRating;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

public class TrainingVideo extends Activity implements OnClickListener,
		OnPreparedListener {

	TextView title, detail, from, summary, mtitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name, aid, _id;
	Button share;
	SharedPreferences prefs;
	ImageView vfullscreenplay;
	com.sanofi.in.mobcast.CustomVideoView vid;
	ImageView btn, icon, play;
	long time = (long) 0, start = (long) 0, stop = (long) 0;
	MediaController mc;
	Reports reports;
	File file;
	private MediaPlayer mMediaPlayer;

	// private boolean isToRate = false;
	HashMap<String, String> params = new HashMap<String, String>();
	private static final String TAG = TrainingVideo.class.getSimpleName();

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.avideo);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		title = (TextView) findViewById(R.id.tvVtitle);
		reports = new Reports(getApplicationContext(), "Training");
		mtitle = (TextView) findViewById(R.id.mtitle);
		icon = (ImageView) findViewById(R.id.miconimage);
		detail = (TextView) findViewById(R.id.tvVDetail);
		from = (TextView) findViewById(R.id.tvVFrom);
		summary = (TextView) findViewById(R.id.tvVsummary);
		vfullscreenplay = (ImageView) findViewById(R.id.vfullscreenplay);

		vid = (com.sanofi.in.mobcast.CustomVideoView) findViewById(R.id.vvType);

		mtitle.setText("Training");

		icon.setImageResource(R.drawable.training);

		// share = (Button) findViewById(R.id.bVshare);
		vid.setOnPreparedListener(this);
		btn = (ImageView) findViewById(R.id.vbackground);
		play = (ImageView) findViewById(R.id.vbackgroundplay);
		play.setOnClickListener(BackgroundclkListener);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			play.setAlpha(0.7f);
		}

		btn.setOnClickListener(BackgroundclkListener);

		share = (Button) findViewById(R.id.iv6);

		onNewIntent(getIntent());
		share.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						v.setAlpha(0.5f);
					}
					// v.getBackground().setAlpha(45);
				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
					if (android.os.Build.VERSION.SDK_INT >= 11) {
						v.setAlpha(1);
					}
					// v.getBackground().setAlpha(255);
				}
				return false;
			}
		});
		vfullscreenplay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// isToRate = true;
				vid.pause();
				time = vid.getCurrentPosition();
				Log.d("Paused at ", "" + time);
				Intent i = new Intent(TrainingVideo.this, VideoFullscreen.class);
				i.putExtra("name", name);
				Log.d("name", name);
				i.putExtra("StartAt", (int) time);
				onDestroy();
				startActivity(i);
			}
		});

		detail.setText(DateUtils.formatDate(Ddetail));
		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reports.updateShare(aid);
				final Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("video/mp4");
				String shareBody = title.getText() + "\n" + from.getText()
						+ "\n ON: " + detail.getText() + "\n"
						+ summary.getText();
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title.getText());
				prefs.edit().putInt("StartAt", vid.getCurrentPosition())
						.commit();
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				shareIntent.putExtra(android.content.Intent.EXTRA_STREAM,
						Uri.fromFile(file));
				startActivity(Intent.createChooser(shareIntent, "Share video"));

			}
		});

		String roo1t = Environment.getExternalStorageDirectory().toString()
				+ Constants.APP_FOLDER_VIDEO + name;
		Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(roo1t,
				MediaStore.Images.Thumbnails.MINI_KIND);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(thumbnail);
		vid.setBackgroundDrawable(bitmapDrawable);
		// btn.setBackgroundDrawable(bitmapDrawable);

		btn.setImageDrawable(bitmapDrawable);
		vid.setVideoPath(roo1t);

		mc = new MediaController(this);
		mc.show(500000);
		mc.setAnchorView(vid);
		vid.setMediaController(mc);
		vid.requestFocus();
		// vid.setPlayPauseListener(ppl);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Toast.makeText(getApplicationContext(),"16. onDestroy()",
		// Toast.LENGTH_SHORT).show();
		Log.e("time in mills", time + "");
		long seconds = time / 1000;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		long hours = minutes / 60;
		minutes = minutes % 60;
		String timestr = hours + ":" + minutes + ":" + seconds;
		Log.e("time in hh:mm:ss", timestr);
		reports.updateDuration(aid, timestr);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d("Orientation ", "" + newConfig.orientation);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			vid.pause();
			Intent i = new Intent(TrainingVideo.this, VideoFullscreen.class);
			i.putExtra("name", name);
			i.putExtra("StartAt", (int) time);
			Log.d("StartAt", "" + time);
			Log.d("name", name);
			onDestroy();
			startActivity(i);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}
	}

	CustomVideoView.PlayPauseListener ppl = new CustomVideoView.PlayPauseListener() {

		@Override
		public void onPlay() {
			System.out.println("Play!");
			Log.d("Start", "" + start);
			start = System.currentTimeMillis();
			play.setVisibility(ImageView.GONE);

		}

		@Override
		public void onPause() {
			System.out.println("Pause!");
			Log.d("Stop", "" + stop);
			play.setVisibility(ImageView.VISIBLE);
			stop = System.currentTimeMillis();
			time += stop - start;
		}
	};

	View.OnClickListener BackgroundclkListener = new View.OnClickListener() {
		public void onClick(View v) {
			// isToRate = true;
			btn.setVisibility(ImageView.GONE);
			vid.setPlayPauseListener(ppl);
			vid.setVisibility(com.sanofi.in.mobcast.CustomVideoView.VISIBLE);
			vid.start();
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		vid.setBackgroundColor(0x00000000);
		vid.setOnClickListener(handler1);
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	View.OnClickListener handler1 = new View.OnClickListener() {
		public void onClick(View v) {
			btn.setVisibility(ImageView.GONE);
			play.setVisibility(ImageView.GONE);
			vid.setVisibility(com.sanofi.in.mobcast.CustomVideoView.VISIBLE);
			vid.start();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	protected void onResume() {
		super.onResume();
		int st = 0;
		try {
			st = prefs.getInt("StartAt", 0);
			System.out.println("St------->" + st);
			prefs.edit().remove("StartAt").commit();
		} catch (Exception e) {
			System.out.println("" + e);
		}
		vid.seekTo(st);
		System.out.println("" + st);
		vid.start();

		// if(isToRate){
		// showRateDialog();
		// isToRate = false;
		// }
	};

	// public void isToShowDialog(){
	// showRateDialog();
	// isToRate = false;
	// }
	//
	// @Override
	// public void onBackPressed() {
	// // TODO Auto-generated method stub
	// if (isToRate)
	// isToShowDialog();
	// else
	// finish();
	//
	// }

	@Override
	public void onNewIntent(Intent intent) {

		Dtitle = intent.getStringExtra("title");
		Ddetail = intent.getStringExtra("detail");
		Dfrom = intent.getStringExtra("from");
		_id = intent.getStringExtra("_id");
		Dsummary = intent.getStringExtra("summary");
		String sharekey = intent.getStringExtra("shareKey");
		if (sharekey.contains("off"))
			share.setVisibility(Button.GONE);
		name = intent.getStringExtra("name");
		aid = intent.getStringExtra("id");
		// String shareflag = intent.getStringExtra("shareKey");
		// if(shareflag.trim().contentEquals("off"))
		// share.setVisibility(Button.INVISIBLE);

		// url = intent.getStringExtra("");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText(Dfrom);
		summary.setText(Dsummary);

		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + Constants.APP_FOLDER_VIDEO);
		// myDir.mkdirs();
		String fname = name;
		file = new File(myDir, fname);
		if (file.exists()) {

			String roo1t = Environment.getExternalStorageDirectory().toString()
					+ Constants.APP_FOLDER_VIDEO + name;
			vid.setVideoPath(roo1t);
			vid.setZOrderOnTop(false);
			vid.setMediaController(new MediaController(this) {
			});
			vid.requestFocus();
		} else {
			if (Utilities.isInternetConnected()) {
				Download d = new Download(TrainingVideo.this,
						AnnounceDBAdapter.SQLITE_TRAINING, _id + "");
				d.execute("");
				d.setOnPostExecuteListener(new OnPostExecuteListener() {

					public void onPostExecute(String result) {
						String roo1t = Environment
								.getExternalStorageDirectory().toString()
								+ Constants.APP_FOLDER_VIDEO + name;
						detail.setText(DateUtils.formatDate(Ddetail));
						Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(
								roo1t, MediaStore.Images.Thumbnails.MINI_KIND);
						BitmapDrawable bitmapDrawable = new BitmapDrawable(
								thumbnail);
						// vid.setBackgroundDrawable(bitmapDrawable);
						btn.setImageDrawable(bitmapDrawable);
						vid.setVideoPath(roo1t);

						vid.setVideoPath(roo1t);
						vid.setZOrderOnTop(false);
						vid.setMediaController(new MediaController(
								TrainingVideo.this) {
						});
						vid.requestFocus();
					}
				});
			} else {
				Toast.makeText(TrainingVideo.this,
						"Please check your internet connection",
						Toast.LENGTH_SHORT).show();
			}
		}

		// vid.start();
		AnnounceDBAdapter announce = new AnnounceDBAdapter(
				getApplicationContext());
		announce.open();
		announce.readrow(_id + "", "Training");
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

	@Override
	public void onPrepared(MediaPlayer arg0) {
		// TODO Auto-generated method stub

		// vid.start();
		Log.v("onPrepared", "called");

	}

	public void showRateDialog() {
		final Dialog dialog = new Dialog(TrainingVideo.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.training_pop_up_rate);

		final RatingBar mRating1 = (RatingBar) dialog
				.findViewById(R.id.feedbackStarRating1);
		final RatingBar mRating2 = (RatingBar) dialog
				.findViewById(R.id.feedbackStarRating2);
		final RatingBar mRating3 = (RatingBar) dialog
				.findViewById(R.id.feedbackStarRating3);
		final RatingBar mRating4 = (RatingBar) dialog
				.findViewById(R.id.feedbackStarRating4);
		TextView mCancel = (TextView) dialog
				.findViewById(R.id.training_pop_up_rate_cancel);
		TextView mSubmit = (TextView) dialog
				.findViewById(R.id.training_pop_up_rate_submit);

		mCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				sendRateToAPI(mRating1, mRating2, mRating3, mRating4);
			}
		});

		dialog.show();
	}

	private void sendRateToAPI(RatingBar r1, RatingBar r2, RatingBar r3,
			RatingBar r4) {
		params.put("rating1", String.valueOf(r1.getRating()));
		params.put("rating2", String.valueOf(r2.getRating()));
		params.put("rating3", String.valueOf(r3.getRating()));
		params.put("rating4", String.valueOf(r4.getRating()));
		params.put("trainingID", aid);
		params.put("device", "android");
		params.put("mobileNumber", ApplicationLoader.getPreferences()
				.getMobileNumber());

		// post request to server

		Log.d("device", params.get("device").toString());
		ConnectionDetector cd = new ConnectionDetector(TrainingVideo.this);
		Boolean isInternetPresent = cd.isConnectingToInternet(); //
		if (isInternetPresent
				&& params.get("device").equalsIgnoreCase("android")) {
			SendRating checkLoginTask = new SendRating(params,
					TrainingVideo.this);
			checkLoginTask.execute(com.mobcast.util.Constants.TRAIN_RATE);
		}
	}

	class SendRating extends AsyncTask<String, String, String> {

		ProgressDialog pDialog;
		private Activity activity;
		private HashMap<String, String> mData = null;// post data
		Context context = null;
		String str = "";

		/**
		 * constructor
		 */

		public SendRating(HashMap<String, String> data) {
			mData = data;
		}

		public SendRating(HashMap<String, String> data, Activity activity) {

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
			return str;
		}

		/**
		 * on getting result
		 */
		@Override
		protected void onPostExecute(String result) {
			// something...
			try {
				pDialog.dismiss();
				pDialog = null;
			} catch (Exception e) {
				// nothing
			}

			Log.d("SendRating result", str);

			JSONObject jObject;
			try {
				jObject = new JSONObject(str);

				String exists = jObject.getString("updated");
				// if(exists.contains("true")){verifywithoutsms();}
				if (exists.contains("true")) {
					Log.i(TAG, "updated");
				} else {
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

}
