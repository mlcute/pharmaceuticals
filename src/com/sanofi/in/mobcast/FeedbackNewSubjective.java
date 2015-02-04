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
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class FeedbackNewSubjective extends Activity {

	TextView questionTV, titleTV, dateTV, quesNoTV, totalquestions;
	// ArrayList<FeedbackQuestion> al1;
	// int hashMapKey;
	int nextpos, prevpos, quesNo, feedbackId;
	EditText answer;
	ImageView next, prev;
	Button submit;
	Button nextBtn;
	Button prevBtn;
	// HashMap<Integer,FeedbackForm> formMap1,formMap11;
	String question, title, date;
	String answerA, answerB, answerC, answerD, subjective, rating, No;
	String feedtotalquestions;
	HashMap<String, String> postParam;
	String attempt, limit;
	AnnounceDBAdapter adb;
	String NAVIGATED_FROM_NEW_LIST;
	static ProgressDialog pDialog;
	SharedPreferences pref;
	Cursor c;
	String key;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedbacksubjective);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		answerA = "null";
		answerB = "null";
		answerC = "null";
		answerD = "null";
		rating = "null";
		subjective = "";
		submit = (Button) findViewById(R.id.feedbackSubjSubmit);
		nextBtn = (Button)findViewById(R.id.feedbackSubjNextBtn);
		prevBtn = (Button)findViewById(R.id.feedbackSubjPrevBtn);
		next = (ImageView) findViewById(R.id.feedbackSubjNext);
		prev = (ImageView) findViewById(R.id.feedbackSubjPrev);
		titleTV = (TextView) findViewById(R.id.feedbackSubjTitle);
		questionTV = (TextView) findViewById(R.id.feedbackSubjQuestion);
		dateTV = (TextView) findViewById(R.id.feedbackSubjDetail);
		quesNoTV = (TextView) findViewById(R.id.feedbackSubjNumber);
		answer = (EditText) findViewById(R.id.feedbacksubjAnswer);
		totalquestions = (TextView) findViewById(R.id.Total_question);
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		onNewIntent(getIntent());
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		
		mNotificationManager.cancelAll();
		titleTV.setText(title);
		questionTV.setText(question);
		dateTV.setText(date);
		totalquestions.setText(feedtotalquestions);
		quesNoTV.setText("" + No + "");
		InputFilter[] FilterArray = new InputFilter[1];
		try {
			FilterArray[0] = new InputFilter.LengthFilter(
					Integer.parseInt(limit));
		} catch (Exception e) {
			Log.e("error in  ", " limit");
		}
		answer.setFilters(FilterArray);
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
				Log.v("Subjective", "in nextclick");
				storeChanges();
				viewfeedbackQuestion(quesNo, "next");
			}
		});
		
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.v("Subjective", "in nextclick");
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
							} else if (c.getString(
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
						} while (c.moveToNext());
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

		adb = new AnnounceDBAdapter(FeedbackNewSubjective.this);
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
		date = c.getString(c.getColumnIndexOrThrow("feedbackDate"));
		attempt = c.getString(c.getColumnIndexOrThrow("feedbackAttempt"));
		limit = c.getString(c.getColumnIndexOrThrow("answerLimit"));
		nextpos = quesNo + 1;
		System.out.println("Navigated -->"
				+ intent.getStringExtra("NAVIGATED_FROM_NEW_LIST"));
		if (intent.getStringExtra("NAVIGATED_FROM_NEW_LIST").equals("true")) {
			Editor editor = pref.edit();
			editor.clear();
			editor.commit();
			System.out.println("Prefs deleting everything");
		} else
			System.out.println("Nope not deleting");
		NAVIGATED_FROM_NEW_LIST = "false";
		No = c.getString(c.getColumnIndexOrThrow("questionNo"));
		// title=c.getString(c.getColumnIndexOrThrow("feedbackTitle"));
		question = c.getString(c.getColumnIndexOrThrow("questionTitle"));
		;
		title = intent.getStringExtra("title");
		feedtotalquestions = intent.getStringExtra("feedtotalquestions");
		key = "Q" + No;
		System.out.println("Number " + No + "  Question " + quesNo + " Key "
				+ key);
		if (!pref.getString(key, "").equals("")) {
			answer.setText(pref.getString(key, ""));
		} else
			System.out.println(key + " preference-->" + pref.getString(key, "")
					+ " final");

		c.close();

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
				show = new Intent(FeedbackNewSubjective.this,
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
				show = new Intent(FeedbackNewSubjective.this,
						FeedbackNewRating.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Multiple")) {
				show = new Intent(FeedbackNewSubjective.this,
						FeedbackNewRadio.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Checkbox")) {
				show = new Intent(FeedbackNewSubjective.this,
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

		try {

			adb.open();
			System.out.println("answer " + answer.getText().toString());
			adb.answerFeedbackSubjective(answer.getText().toString(), quesNo);
			adb.close();
			System.out.println("Store changes key " + key + " "
					+ answer.getText().toString());
			Editor editor = pref.edit();
			editor.putString(key, answer.getText().toString());
			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// end of storeChanges

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
	
}// end of class FeedbackSubjective
