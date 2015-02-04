package com.sanofi.in.mobcast;

import java.io.File;
import java.io.IOException;
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

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import android.widget.Toast;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.sanofi.in.mobcast.TrainingVideo.SendRating;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.DateUtils;
import com.mobcast.util.Download;
import com.mobcast.util.Utilities;
import com.mobcast.util.Download.OnPostExecuteListener;

public class TrainingAudio extends Activity implements OnClickListener,
		OnPreparedListener, MediaPlayerControl {

	TextView title, detail, from, summary, mtitle;
	String Dtitle, Ddetail, Dfrom, Dsummary, url, name, aid, _id;
	Button share;
	ImageView btnplay, btnpause, icon;
	Long time = (long) 0, start = (long) 0, stop = (long) 0;
	// MediaPlayer mMediaPlayer;

	File file,final_file;
	private MediaController mMediaController;
	private MediaPlayer mMediaPlayer;
	Reports reports;
	private Handler mHandler = new Handler();
//	private boolean isToRate = false;
	HashMap<String, String> params = new HashMap<String, String>();
	private static final String TAG = TrainingAudio.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.audio);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		title = (TextView) findViewById(R.id.tvVtitle);
		mtitle = (TextView) findViewById(R.id.mtitle);
		reports = new Reports(getApplicationContext(), "Training");
		detail = (TextView) findViewById(R.id.tvVDetail);
		from = (TextView) findViewById(R.id.tvVFrom);
		summary = (TextView) findViewById(R.id.tvVsummary);
		icon = (ImageView) findViewById(R.id.icon);
		mtitle.setText("Training");

		icon.setImageResource(R.drawable.training);

		share = (Button) findViewById(R.id.iv6);

		onNewIntent(getIntent());

		/*
		 * try{File file = new
		 * File(Environment.getExternalStorageDirectory().getAbsolutePath() +
		 * "/.mobcast/mobcast_audio/"+name); Uri external = Uri.fromFile(file);
		 * mMediaPlayer = MediaPlayer.create(audio.this,external);
		 * }catch(Exception e){e.printStackTrace();}
		 */

		// ============================================================================

		mMediaPlayer = new MediaPlayer();
		mMediaController = new MediaController(this);
		detail.setText(DateUtils.formatDate(Ddetail));
		mMediaController.setMediaPlayer(TrainingAudio.this);
		mMediaController.setAnchorView(findViewById(R.id.audioView));
		file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+ Constants.APP_FOLDER_AUDIO);
		final_file = new File(file,name);
		final String audioFile = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + Constants.APP_FOLDER_AUDIO + name;
		try {
			mMediaPlayer.setDataSource(audioFile);
			mMediaPlayer.prepare();
		} catch (IOException e) {
			
			Log.e("PlayAudioDemo", "Could not open file " + audioFile
					+ " for playback.", e);
			Download d = new Download(TrainingAudio.this, AnnounceDBAdapter.SQLITE_TRAINING, _id+"");
			d.execute("");
			d.setOnPostExecuteListener(new OnPostExecuteListener() {

				public void onPostExecute(String result) {
					
					try{mMediaPlayer.setDataSource(audioFile);
					mMediaPlayer.prepare();} catch (Exception e){Toast.makeText(getApplicationContext(), "Failed To download file", Toast.LENGTH_SHORT).show();}
				}
			});
			
			
		}
		mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				mHandler.post(new Runnable() {
					public void run() {
						// mMediaController.show(3000);
						// mMediaPlayer.start();
					}
				});
			}
		});

		share.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				reports.updateShare(aid);
				final Intent shareIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				shareIntent.setType("audio/mp3");
				String shareBody = title.getText() + "\n" + from.getText()
						+ "\n ON: " + detail.getText() + "\n"
						+ summary.getText();
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						title.getText());
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						shareBody);
				shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(final_file));
				startActivity(Intent.createChooser(shareIntent, "Share audio"));

			}
		});

		share.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

					// if(android.os.Build.VERSION.SDK_INT>=11){
					// v.setAlpha(0.5f);}
					v.getBackground().setAlpha(45);

				} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

					// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
					v.getBackground().setAlpha(255);
				}

				return false;
			}
		});

		// ============================================================================

		// share = (Button) findViewById(R.id.bVshare);
		btnplay = (ImageView) findViewById(R.id.play1);
		btnplay.setOnClickListener(playhandler);
		btnpause = (ImageView) findViewById(R.id.pause1);
		btnpause.setOnClickListener(pausehandler);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Toast.makeText(getApplicationContext(),"16. onDestroy()",
		// Toast.LENGTH_SHORT).show();
		time = (long) mMediaPlayer.getCurrentPosition();
		Log.e("time in mills", mMediaPlayer.getCurrentPosition() + "");
		long seconds = time / 1000;
		long minutes = seconds / 60;
		seconds = seconds % 60;
		long hours = minutes / 60;
		minutes = minutes % 60;
		String timestr = hours + ":" + minutes + ":" + seconds;
		Log.e("time in hh:mm:ss", timestr);
		mMediaPlayer.stop();
		mMediaPlayer.release();
		reports.updateDuration(aid, timestr);
	}

	View.OnClickListener playhandler = new View.OnClickListener() {
		public void onClick(View v) {
			{
//				isToRate = true;
				mMediaPlayer.start();
				btnpause.setVisibility(ImageView.VISIBLE);
				btnplay.setVisibility(ImageView.GONE);
				mMediaController.show(3000);
				mMediaPlayer.start();
			}
		}
	};

	View.OnClickListener pausehandler = new View.OnClickListener() {
		public void onClick(View v) {
			{
//				isToRate = true;
				mMediaPlayer.pause();
				btnplay.setVisibility(ImageView.VISIBLE);
				btnpause.setVisibility(ImageView.GONE);
				mMediaController.show(3000);
				mMediaPlayer.pause();
			}
		}
	};

	 
	
//	public void isToShowDialog(){
//		if(isToRate){
//			showRateDialog();
//			isToRate = false;
//		}
//	}
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		if (isToRate)
//			isToShowDialog();
//		else
//			finish();
//
//	}

	@Override
	public void onNewIntent(Intent intent) {

		try {
			Dtitle = intent.getStringExtra("title");
			Ddetail = intent.getStringExtra("detail");
			Dfrom = intent.getStringExtra("from");
			_id = intent.getStringExtra("_id");
			Dsummary = intent.getStringExtra("summary");
			name = intent.getStringExtra("name");
			if (intent.getStringExtra("shareKey").equals("off"))
				share.setVisibility(Button.INVISIBLE);
			aid = intent.getStringExtra("id");
		} catch (Exception e) {
		}
		// url = intent.getStringExtra("");
		title.setText(Dtitle);
		detail.setText(Ddetail);
		from.setText(Dfrom);
		summary.setText(Dsummary);

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

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean canPause() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canSeekForward() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getBufferPercentage() {
		// TODO Auto-generated method stub
		int percentage = (mMediaPlayer.getCurrentPosition() * 100)
				/ mMediaPlayer.getDuration();
		return percentage;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getCurrentPosition();
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		return mMediaPlayer.getDuration();
	}

	@Override
	public boolean isPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}

	@Override
	public void pause() {
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			btnplay.setVisibility(ImageView.VISIBLE);
			btnpause.setVisibility(ImageView.GONE);
			stop = System.currentTimeMillis();
			time += stop - start;
		}
	}

	@Override
	public void seekTo(int pos) {
		// TODO Auto-generated method stub
		mMediaPlayer.seekTo(pos);
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		mMediaPlayer.start();
		btnpause.setVisibility(ImageView.VISIBLE);
		btnplay.setVisibility(ImageView.GONE);
		start = System.currentTimeMillis();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mMediaController.show();

		return false;
	}

	@Override
	public int getAudioSessionId() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void showRateDialog()
	{
		final Dialog dialog = new Dialog(TrainingAudio.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setContentView(R.layout.training_pop_up_rate);
		
		final RatingBar mRating1 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating1);
		final RatingBar mRating2 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating2);
		final RatingBar mRating3 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating3);
		final RatingBar mRating4 = (RatingBar)dialog.findViewById(R.id.feedbackStarRating4);
		TextView mCancel = (TextView)dialog.findViewById(R.id.training_pop_up_rate_cancel);
		TextView mSubmit = (TextView)dialog.findViewById(R.id.training_pop_up_rate_submit);

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
	
	
	private void sendRateToAPI(RatingBar r1, RatingBar r2, RatingBar r3, RatingBar r4) {
		params.put("rating1", String.valueOf(r1.getRating()));
		params.put("rating2", String.valueOf(r2.getRating()));
		params.put("rating3", String.valueOf(r3.getRating()));
		params.put("rating4", String.valueOf(r4.getRating()));
		params.put("trainingID", aid);
		params.put("device", "android");
		params.put("mobileNumber", ApplicationLoader.getPreferences().getMobileNumber());

		// post request to server

		Log.d("device", params.get("device").toString());
		ConnectionDetector cd = new ConnectionDetector(TrainingAudio.this);
		Boolean isInternetPresent = cd.isConnectingToInternet(); //
		if (isInternetPresent
				&& params.get("device").equalsIgnoreCase("android")) {
			SendRating checkLoginTask = new SendRating(params, TrainingAudio.this);
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
