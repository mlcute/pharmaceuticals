package com.sanofi.in.mobcast;


import java.util.HashMap;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class FeedbackNewRating extends Activity {

	TextView questionTV, titleTV, quesNoTV, totalnumberofquestions;
	// ArrayList<FeedbackQuestion> al1;
	int nextpos, prevpos, quesNo, feedbackId;

	ImageView next, prev;
	Button submit;
	Button nextBtn;
	Button prevBtn;
	String answer, date, attempt, limit;
	SharedPreferences pref;
	String question, title, No, feedtotalquestions, NAVIGATED_FROM_NEW_LIST,
			key;
	RatingBar rb;
	String answerA, answerB, answerC, answerD, subjective, rating;
	HashMap<String, String> postParam;
	static ProgressDialog pDialog;
	AnnounceDBAdapter adb;
	Cursor c;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_star);
		Log.v("Rating", "in onCreate");
		answerA = "null";
		answerB = "null";
		answerC = "null";
		answerD = "null";
		rating = "1";
		subjective = "null";
		postParam = new HashMap<String, String>();
		rb = (RatingBar) findViewById(R.id.feedbackStarRating);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		rb.setStepSize(1);

		// rb.setRating(1);

		submit = (Button) findViewById(R.id.feedbackStarSubmit);
		nextBtn = (Button)findViewById(R.id.feedbackSubjNextBtn);
		prevBtn= (Button)findViewById(R.id.feedbackSubjPrevBtn);
		next = (ImageView) findViewById(R.id.feedbackStarNext);
		prev = (ImageView) findViewById(R.id.feedbackStarPrev);
		titleTV = (TextView) findViewById(R.id.feedbackStarTitle);
		questionTV = (TextView) findViewById(R.id.feedbackStarQuestion);
		quesNoTV = (TextView) findViewById(R.id.feedbackStarNumber);
		// dateTV=(TextView)findViewById(R.id.feedbackSDetail);
		totalnumberofquestions = (TextView) findViewById(R.id.totalNumberofQuestion);
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		onNewIntent(getIntent());

		titleTV.setText(title);
		totalnumberofquestions.setText(feedtotalquestions);
		System.out.println("oncreate ---> " + title);
		questionTV.setText(question);
		// dateTV.setText(date);

		quesNoTV.setText("" + No.toString() + "");

		rb.setNumStars(Integer.parseInt(limit));

		if (attempt.equals("Single")) {
			prev.setVisibility(ImageView.INVISIBLE);
		}
		if (adb.isLastFeedbackQuestion(quesNo, feedbackId)) {
			submit.setVisibility(Button.VISIBLE);
			next.setVisibility(ImageView.GONE);
			nextBtn.setVisibility(View.GONE);
			prevBtn.setVisibility(View.GONE);

		}
		if (adb.isFirstFeedbackQuestion(quesNo, feedbackId)) {
			prev.setVisibility(ImageView.GONE);
			prevBtn.setVisibility(View.GONE);
		}

		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				storeChanges();
				viewfeedbackQuestion(quesNo, "next");

			}
		});
		
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				storeChanges();
				viewfeedbackQuestion(quesNo, "next");

			}
		});

		
		prevBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeChanges();
				adb.open();
				adb.updateFeedbackCount((feedbackId));
				adb.close();
				viewfeedbackQuestion(quesNo, "privious");

			}
		});
		
		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeChanges();
				adb.open();
				adb.updateFeedbackCount((feedbackId));
				adb.close();
				viewfeedbackQuestion(quesNo, "privious");

			}
		});

		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				storeChanges();
				new Reports(getApplicationContext(),"Feedback").updateAttempts(feedbackId+"");
				Boolean b = true;
				for (int i = 1; i <= Integer.parseInt(feedtotalquestions); i++) {
					String check_id = "Q" + i;
					b = b & (!pref.getString(check_id, "").equals(""));
				}
				if (b) {
					adb.open();
					Cursor c = adb.fetchAllFeedbackForAnswer(feedbackId + "");
					if (c != null)
						c.moveToFirst();
					{
						// String type =
						// cur.getString(cur.getColumnIndexOrThrow("type"));
						// _id, feedbackNo, feedbackTitle, feedbackDescription,
						// feedbackAttempt, feedbackTotalQuestions,
						// feedbackDate,
						// questionNo, questionType, questionTitle, optionA,
						// optionB, optionC, optionD, answerLimit, answer,
						// attempts

						HashMap<String, String> params = new HashMap<String, String>();

						params.put("feedbackID", feedbackId + "");

						SessionManagement sm = new SessionManagement(
								getApplicationContext());
						String email = sm.getUserDetails().get("name");
						params.put("emailID", email);
						params.put(com.mobcast.util.Constants.user_id, email);
						params.put("companyID", getString(R.string.companyID));
						params.put("feedbackID", c.getString(c
								.getColumnIndexOrThrow("feedbackNo")));
						params.put(
								"count",
								c.getString(c
										.getColumnIndexOrThrow("feedbackTotalQuestions")));

						String question = "null", ansa = "null", ansb = "null", ansc = "null", ansd = "null", anssub = "null", ansrat = "null", questionNo = "null", answer;

						do {
							answer = c.getString(c
									.getColumnIndexOrThrow("answer"));
							question = "null";
							ansa = "null";
							ansb = "null";
							ansc = "null";
							ansd = "null";
							anssub = "null";
							ansrat = "null";
							questionNo = c.getString(c
									.getColumnIndexOrThrow("questionNo"));

							question = c.getString(c
									.getColumnIndexOrThrow("feedbackNo"))
									+ "/"
									+ c.getString(c
											.getColumnIndexOrThrow("questionNo"));
							params.put("question" + questionNo, question);

							if (c.getString(
									c.getColumnIndexOrThrow("questionType"))
									.equals("Multiple")
									|| c.getString(
											c.getColumnIndexOrThrow("questionType"))
											.contentEquals("Checkbox")) {
								ansa = "0";
								ansb = "0";
								ansc = "0";
								ansd = "0";
								anssub = "0";
								ansrat = "0";
								if (answer.contains("a")
										|| answer.contains("A"))
									ansa = "1";
								if (answer.contains("b")
										|| answer.contains("B"))
									ansb = "1";
								if (answer.contains("c")
										|| answer.contains("C"))
									ansc = "1";
								if (answer.contains("d")
										|| answer.contains("D"))
									ansd = "1";

								params.put("answerA" + questionNo, ansa);
								params.put("answerB" + questionNo, ansb);
								params.put("answerC" + questionNo, ansc);
								params.put("answerD" + questionNo, ansd);
								params.put("subjective" + questionNo, anssub);
								params.put("rating" + questionNo, ansrat);

							}

							else if (c.getString(
									c.getColumnIndexOrThrow("questionType"))
									.equals("Subjective")) {
								anssub = answer;
								params.put("answerA" + questionNo, ansa);
								params.put("answerB" + questionNo, ansb);
								params.put("answerC" + questionNo, ansc);
								params.put("answerD" + questionNo, ansd);
								params.put("subjective" + questionNo, anssub);
								params.put("rating" + questionNo, ansrat);
							}

							else if (c.getString(
									c.getColumnIndexOrThrow("questionType"))
									.equals("Rating")) {
								ansrat = answer;
								params.put("answerA" + questionNo, ansa);
								params.put("answerB" + questionNo, ansb);
								params.put("answerC" + questionNo, ansc);
								params.put("answerD" + questionNo, ansd);
								params.put("subjective" + questionNo, anssub);
								params.put("rating" + questionNo, ansrat);
							}

						}while (c.moveToNext());
						AsyncHttpPost asyncHttpPost = new AsyncHttpPost(params);
						asyncHttpPost
								.execute(com.mobcast.util.Constants.UPDATE_FEEDBACK);

					}
					/*if (attempt.equals("Single")) {
						adb.deleteFeedback(feedbackId);*/
						adb.readrow(feedbackId+"", AnnounceDBAdapter.SQLITE_FEEDBACK); //ADDED VIKALP FEEDBACK READ ON SUBMIT ONLY
						adb.close();
					//}

					Intent i = new Intent(getApplicationContext(),
							Home1.class);
					Toast.makeText(getBaseContext(),
							"Thank You for your feedback", Toast.LENGTH_SHORT)
							.show();
					startActivity(i);
					finish();
				} else {
					Toast.makeText(getBaseContext(),
							"Please complete all the question, Thank You!!",
							Toast.LENGTH_SHORT).show();
				}

			}// end of onClick
		});// submit button

	}// end of onCreate

	@Override
	public void onNewIntent(Intent intent) {
		rb.setVisibility(RatingBar.VISIBLE);

		try {

			adb = new AnnounceDBAdapter(FeedbackNewRating.this);
			adb.open();
			try {
				quesNo = Integer.parseInt(intent.getStringExtra("_id"));
			} catch (Exception e) {
				Log.e("error in  ", " _id");
			}
			Cursor c = adb.fetchFeedback(quesNo);
			try {
				feedbackId = Integer.parseInt(c.getString(c
						.getColumnIndexOrThrow("feedbackNo")));
			} catch (Exception e) {
				Log.e("error in  ", " feedback_id");
			}

			/*
			 * attempt=intent.getStringExtra("attempt");
			 * date=intent.getStringExtra("date");
			 * limit=intent.getStringExtra("limit");
			 * nextpos=intent.getIntExtra("nextpos",0);
			 * title=intent.getStringExtra("title");
			 * question=intent.getStringExtra("question");
			 */
			if (intent.getStringExtra("NAVIGATED_FROM_NEW_LIST").equals("true")) {
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
				System.out.println("Prefs deleting everything");
			} else
				System.out.println("Nope not deleting");
			NAVIGATED_FROM_NEW_LIST = "false";
			date = c.getString(c.getColumnIndexOrThrow("feedbackDate"));
			attempt = c.getString(c.getColumnIndexOrThrow("feedbackAttempt"));
			limit = c.getString(c.getColumnIndexOrThrow("answerLimit"));
			nextpos = quesNo + 1;
			No = c.getString(c.getColumnIndexOrThrow("questionNo"));
			key = "Q" + No;
			System.out.println("Number " + No + "  Question " + quesNo
					+ " Key " + key);
			if (!pref.getString(key, "").equals("")) {

				// answer.setText(pref.getString(key, ""));
				rb.setRating((float) Integer.parseInt(pref.getString(key, "")));
			} else
				System.out.println(key + " preference-->"
						+ pref.getString(key, "") + " final");

			System.out.println(key + " preference-->" + pref.getString(key, "")
					+ " final");

			// title=c.getString(c.getColumnIndexOrThrow("feedbackTitle"));
			question = c.getString(c.getColumnIndexOrThrow("questionTitle"));
			title = intent.getStringExtra("title");
			feedtotalquestions = intent.getStringExtra("feedtotalquestions");
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}// end of onNewIntent

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adb.close();
	}

	void viewfeedbackQuestion(int current_id, String direction) {

		try {
			adb.open();
			Log.v(direction, "" + current_id);

			if (direction.equals("next"))
				try {
					c = adb.fetchFeedback(current_id + 1);
				} catch (Exception e) {
					Log.v("next feedback", "nahi aaya");
				}

			else
				c = adb.fetchFeedback(current_id - 1);

			int _id = Integer.parseInt(c.getString(c
					.getColumnIndexOrThrow("_id")));
			String type = c.getString(c.getColumnIndexOrThrow("questionType"));

			Intent show;
			if (type.contentEquals("Subjective")) {
				show = new Intent(FeedbackNewRating.this,
						FeedbackNewSubjective.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Rating")) {
				show = new Intent(FeedbackNewRating.this,
						FeedbackNewRating.class);
				show.putExtra("_id", _id + "");
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Multiple")) {
				show = new Intent(FeedbackNewRating.this,
						FeedbackNewRadio.class);
				show.putExtra("_id", _id + "");
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("title", title);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Checkbox")) {
				show = new Intent(FeedbackNewRating.this,
						FeedbackNewCheck.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

		} catch (Exception e) {
			Log.e("error fetching ", "feedback");
		}

		finally {
			c.close();
			adb.close();
			finish();
		}

	}

	// Writing back the answers into the serialized object
	public void storeChanges() {
		Integer rf = (int) rb.getRating();
		rating = rf.toString();
		adb.open();
		adb.answerFeedbackSubjective(rating, quesNo);
		adb.close();
		Editor editor = pref.edit();
		System.out.println("key  " + key + "Rating " + rating);
		editor.putString(key, rating.toString());
		editor.commit();

	}// end of storeChanges
	
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
	
}// end of FeedbackRating
