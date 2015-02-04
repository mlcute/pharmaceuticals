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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class FeedbackNewRadio extends Activity {
	TextView questionTV, titleTV, dateTV, quesNoTV, totalnumberofQuestion;
	String date;

	ImageView next, prev;
	Button submit;
	Button nextBtn;
	Button prevBtn;

	String NAVIGATED_FROM_NEW_LIST, key;
	String answer = "";

	int nextpos, quesNo, feedbackId;

	RadioButton radio1, radio2, radio3, radio4;
	String question, title, radioString1, radioString2, radioString3,
			radioString4, feedtotalquestions;
	String No;
	SharedPreferences pref;
	String attempt, limit;
	static ProgressDialog pDialog;
	AnnounceDBAdapter adb;
	Cursor c;

	public void onCreate(Bundle savedInstanceState) {
		Log.v("radio", "in oncreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_radio);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		radio1 = (RadioButton) findViewById(R.id.feedbackRadio1);
		radio2 = (RadioButton) findViewById(R.id.feedbackRadio2);
		radio3 = (RadioButton) findViewById(R.id.feedbackRadio3);
		radio4 = (RadioButton) findViewById(R.id.feedbackRadio4);

		submit = (Button) findViewById(R.id.feedbackRadioSubmit);
		nextBtn = (Button)findViewById(R.id.feedbackSubjNextBtn);
		prevBtn = (Button)findViewById(R.id.feedbackSubjPrevBtn);
		next = (ImageView) findViewById(R.id.feedbackRadioNext);
		prev = (ImageView) findViewById(R.id.feedbackRadioPrev);

		titleTV = (TextView) findViewById(R.id.feedbackRadioTitle);
		questionTV = (TextView) findViewById(R.id.feedbackRadioQuestion);
		dateTV = (TextView) findViewById(R.id.feedbackRadioDetail);
		quesNoTV = (TextView) findViewById(R.id.feedbackRadioNumber);
		totalnumberofQuestion = (TextView) findViewById(R.id.totalNumberofQuestion);
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		onNewIntent(getIntent());
		titleTV.setText(title);
		questionTV.setText(question);
		dateTV.setText(date);
		totalnumberofQuestion.setText(feedtotalquestions);
		quesNoTV.setText("" + No + "");

		if (attempt.equals("Single")) {
			prev.setVisibility(ImageView.INVISIBLE);
		}

		// For checkbox1
		if (!radioString1.equals("null")) {
			radio1.setText(radioString1);
			radio1.setVisibility(CheckBox.VISIBLE);
		} else
			radio1.setVisibility(CheckBox.GONE);

		// For checkbox1
		if (!radioString2.equals("null")) {
			radio2.setText(radioString2);
			radio2.setVisibility(CheckBox.VISIBLE);
		} else
			radio2.setVisibility(CheckBox.GONE);

		// For checkbox1
		if (!radioString3.equals("null")) {
			radio3.setText(radioString3);
			radio3.setVisibility(CheckBox.VISIBLE);
		} else
			radio3.setVisibility(CheckBox.GONE);

		// For checkbox1
		if (!radioString4.equals("null")) {
			radio4.setText(radioString4);
			radio4.setVisibility(CheckBox.VISIBLE);
		} else
			radio4.setVisibility(CheckBox.GONE);

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

		radio1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio1.isChecked()) {
					answer = "A";
				}
			}

		});
		radio2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (radio2.isChecked()) {
					answer = "B";
				}
			}

		});

		radio3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio3.isChecked()) {
					answer = "C";
				}
			}

		});

		radio4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio4.isChecked()) {
					answer = "D";
				}
			}

		});

		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio1.isChecked()) {
					answer = "A";
				}
				if (radio2.isChecked()) {
					answer = "B";
				}
				if (radio3.isChecked()) {
					answer = "C";
				}
				if (radio4.isChecked()) {
					answer = "D";
				}
				System.out.println("Answer ===" + answer);
				storeChanges();
				viewfeedbackQuestion(quesNo, "next");

			}
		});
		
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio1.isChecked()) {
					answer = "A";
				}
				if (radio2.isChecked()) {
					answer = "B";
				}
				if (radio3.isChecked()) {
					answer = "C";
				}
				if (radio4.isChecked()) {
					answer = "D";
				}
				System.out.println("Answer ===" + answer);
				storeChanges();
				viewfeedbackQuestion(quesNo, "next");

			}
		});

		
		prevBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (radio1.isChecked()) {
					answer = "A";
				}
				if (radio2.isChecked()) {
					answer = "B";
				}
				if (radio3.isChecked()) {
					answer = "C";
				}
				if (radio4.isChecked()) {
					answer = "D";
				}
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
				if (radio1.isChecked()) {
					answer = "A";
				}
				if (radio2.isChecked()) {
					answer = "B";
				}
				if (radio3.isChecked()) {
					answer = "C";
				}
				if (radio4.isChecked()) {
					answer = "D";
				}
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
					adb.close();
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
								ansa = "null";
								ansb = "null";
								ansc = "null";
								ansd = "null";
								anssub = "null";
								ansrat = "null";
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
						adb.open();
						adb.deleteFeedback(feedbackId);
						adb.close();
					}*/
					
					adb.open();
					adb.readrow(feedbackId+"", AnnounceDBAdapter.SQLITE_FEEDBACK); //ADDED VIKALP FEEDBACK READ ON SUBMIT ONLY
					adb.close();


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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adb.close();
	}

	@Override
	public void onNewIntent(Intent intent) {

		try {

			adb = new AnnounceDBAdapter(FeedbackNewRadio.this);
			adb.open();
			try {
				quesNo = Integer.parseInt(intent.getStringExtra("_id"));
				Log.d("Question No.", String.valueOf(quesNo));
			} catch (Exception e) {
				Log.e("error in  ", " _id");
			}
			
			
			Cursor c = adb.fetchFeedback(quesNo);
			
			Log.d("Cursor", String.valueOf(c));
			try {
				feedbackId = Integer.parseInt(c.getString(c
						.getColumnIndexOrThrow("feedbackNo")));
				Log.d("Feedback ID", String.valueOf(feedbackId));
			} catch (Exception e) {
				Log.e("error in  ", " feedback_id");
			}

			nextpos = quesNo + 1;

			date = c.getString(c.getColumnIndexOrThrow("feedbackDate"));
			attempt = c.getString(c.getColumnIndexOrThrow("feedbackAttempt"));
			limit = c.getString(c.getColumnIndexOrThrow("answerLimit"));

			No = c.getString(c.getColumnIndexOrThrow("questionNo"));
			// title=c.getString(c.getColumnIndexOrThrow("feedbackTitle"));
			question = c.getString(c.getColumnIndexOrThrow("questionTitle"));
			title = intent.getStringExtra("title");
			feedtotalquestions = intent.getStringExtra("feedtotalquestions");
			Log.d("Total questions", feedtotalquestions);
			Log.d("date", date);
			Log.d("attempt", attempt);
			Log.d("limit", limit);
			Log.d("no.", No);
			
			System.out.println("Radionew--->" + feedtotalquestions + " title "
					+ title);
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
			key = "Q" + No;
			System.out.println("Number " + No + "  Question " + quesNo
					+ " Key " + key);
			if (!pref.getString(key, "").equals("")) {
				if (pref.getString(key, "").equals("A"))
					radio1.toggle();
				else if (pref.getString(key, "").equals("B"))
					radio2.toggle();
				else if (pref.getString(key, "").equals("C"))
					radio3.toggle();
				else if (pref.getString(key, "").equals("D"))
					radio4.toggle();

			} else
				System.out.println(key + " preference-->"
						+ pref.getString(key, "") + " final");
			System.out.println(key + " preference--> "
					+ pref.getString(key, "") + " final");
			radioString1 = c.getString(c.getColumnIndexOrThrow("optionA"));
			radioString2 = c.getString(c.getColumnIndexOrThrow("optionB"));
			radioString3 = c.getString(c.getColumnIndexOrThrow("optionC"));
			radioString4 = c.getString(c.getColumnIndexOrThrow("optionD"));
			Log.v("radio", "end of newIntent");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}// end of onNewIntent

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
			int _id = 0;

			try {
				_id = Integer.parseInt(c.getString(c
						.getColumnIndexOrThrow("_id")));
			} catch (Exception e) {
				Log.e("error in  ", " feedback_id");
			}
			String type = c.getString(c.getColumnIndexOrThrow("questionType"));

			Intent show;
			if (type.contentEquals("Subjective")) {
				show = new Intent(FeedbackNewRadio.this,
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
				show = new Intent(FeedbackNewRadio.this,
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
				show = new Intent(FeedbackNewRadio.this, FeedbackNewRadio.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Checkbox")) {
				show = new Intent(FeedbackNewRadio.this, FeedbackNewCheck.class);
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

		adb.open();
		adb.answerFeedbackSubjective(answer, quesNo);
		adb.close();
		Editor editor = pref.edit();
		editor.putString(key, answer);
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
}// end of FeedbackRadio class
