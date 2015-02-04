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
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class FeedbackNewCheck extends Activity {
	TextView questionTV, titleTV, quesNoTV, totalnumberofquestions;
	// TextView dateTV;
	String answer = "";
	ImageView next, prev;
	Button submit;
	Button nextBtn;
	Button prevBtn;

	int nextpos, quesNo, feedbackId;// Denotes the pos of the next question in
									// the array list
	CheckBox check1, check2, check3, check4;
	String question, checkString1, checkString2, checkString3, checkString4,
			title, feedtotalquestions;
	String No;
	SharedPreferences pref;
	String NAVIGATED_FROM_NEW_LIST, key;
	String date, attempt, limit;
	static ProgressDialog pDialog;
	AnnounceDBAdapter adb;
	Cursor c;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.feedback_check);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		pref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		submit = (Button) findViewById(R.id.feedbackCheckSubmit);
		nextBtn = (Button)findViewById(R.id.feedbackSubjNextBtn);
		prevBtn = (Button)findViewById(R.id.feedbackSubjPrevBtn);
		next = (ImageView) findViewById(R.id.feedbackCheckNext);
		prev = (ImageView) findViewById(R.id.feedbackCheckPrev);
		titleTV = (TextView) findViewById(R.id.feedbackCheckTitle);
		questionTV = (TextView) findViewById(R.id.feedbackCheckQuestion);
		totalnumberofquestions = (TextView) findViewById(R.id.totalNumberofQuestion);
		quesNoTV = (TextView) findViewById(R.id.feedbackCheckNumber);
		// dateTV = (TextView) findViewById(R.id.feedbackCheckDetail);
		check1 = (CheckBox) findViewById(R.id.feedbackCheck1);
		check2 = (CheckBox) findViewById(R.id.feedbackCheck2);
		check3 = (CheckBox) findViewById(R.id.feedbackCheck3);
		check4 = (CheckBox) findViewById(R.id.feedbackCheck4);

		onNewIntent(getIntent());

		questionTV.setText(question);
		totalnumberofquestions.setText(feedtotalquestions);
		// dateTV.setText(date);
		titleTV.setText(title);
		Integer temp = quesNo;
		quesNoTV.setText("" + No);

		if (attempt.equals("Single")) {
			prev.setVisibility(ImageView.INVISIBLE);
		}

		// For checkbox1
		if (!checkString1.equals("null")) {
			check1.setText(checkString1);
			check1.setVisibility(CheckBox.VISIBLE);
		} else
			check1.setVisibility(CheckBox.GONE);

		// For checkbox2
		if (!checkString2.equals("null")) {
			check2.setText(checkString2);
			check2.setVisibility(CheckBox.VISIBLE);
		} else
			check2.setVisibility(CheckBox.GONE);

		// For checkbox1
		if (!checkString3.equals("null")) {
			check3.setText(checkString3);
			check3.setVisibility(CheckBox.VISIBLE);
		} else
			check3.setVisibility(CheckBox.GONE);

		// For checkbox1
		if (!checkString4.equals("null")) {
			check4.setText(checkString4);
			check4.setVisibility(CheckBox.VISIBLE);
		} else
			check4.setVisibility(CheckBox.GONE);

		// if(attemp==single) hide prevButton

		if (adb.isLastFeedbackQuestion(quesNo, feedbackId)) {
			submit.setVisibility(Button.VISIBLE);
			next.setVisibility(ImageView.GONE);
			submit.setText("Submit");
			nextBtn.setVisibility(View.GONE);
			prevBtn.setVisibility(View.GONE);
		}
		if (adb.isFirstFeedbackQuestion(quesNo, feedbackId)) {
			prev.setVisibility(ImageView.GONE);
			prevBtn.setVisibility(View.GONE);
		}

		check1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check1.isChecked()) {
					if (!answer.contains("A"))
						answer = answer + "A";

				}
			}

		});

		check2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check2.isChecked()) {
					if (!answer.contains("B"))
						answer = answer + "B";

				}
			}

		});

		check3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check3.isChecked()) {
					if (!answer.contains("C"))
						answer = answer + "C";

				}
			}

		});

		check4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check4.isChecked()) {
					if (!answer.contains("D"))
						answer = answer + "D";

				}
			}

		});

		nextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check1.isChecked()) {
					if (!answer.contains("A"))
						answer = answer + "A";

				}
				if (check2.isChecked()) {
					if (!answer.contains("B"))
						answer = answer + "B";

				}

				if (check3.isChecked()) {
					if (!answer.contains("C"))
						answer = answer + "C";

				}
				if (check4.isChecked()) {
					if (!answer.contains("D"))
						answer = answer + "D";
				}
				System.out.println("Answer(Next) ===" + answer);
				storeChanges();

				viewfeedbackQuestion(quesNo, "next");

			}
		});
		
		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check1.isChecked()) {
					if (!answer.contains("A"))
						answer = answer + "A";

				}
				if (check2.isChecked()) {
					if (!answer.contains("B"))
						answer = answer + "B";

				}

				if (check3.isChecked()) {
					if (!answer.contains("C"))
						answer = answer + "C";

				}
				if (check4.isChecked()) {
					if (!answer.contains("D"))
						answer = answer + "D";
				}
				System.out.println("Answer(Next) ===" + answer);
				storeChanges();

				viewfeedbackQuestion(quesNo, "next");

			}
		});

		prevBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (check1.isChecked()) {
					if (!answer.contains("A"))
						answer = answer + "A";

				}
				if (check2.isChecked()) {
					if (!answer.contains("B"))
						answer = answer + "B";

				}

				if (check3.isChecked()) {
					if (!answer.contains("C"))
						answer = answer + "C";

				}
				if (check4.isChecked()) {
					if (!answer.contains("D"))
						answer = answer + "D";
				}
				System.out.println("Answer(Prev) ===" + answer);

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
				if (check1.isChecked()) {
					if (!answer.contains("A"))
						answer = answer + "A";

				}
				if (check2.isChecked()) {
					if (!answer.contains("B"))
						answer = answer + "B";

				}

				if (check3.isChecked()) {
					if (!answer.contains("C"))
						answer = answer + "C";

				}
				if (check4.isChecked()) {
					if (!answer.contains("D"))
						answer = answer + "D";
				}
				System.out.println("Answer(Prev) ===" + answer);

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
				// Toast.makeText(getApplicationContext(),"Submit clicked",
				// Toast.LENGTH_LONG).show();
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		adb.close();
	}

	@Override
	public void onNewIntent(Intent intent) {

		try {

			adb = new AnnounceDBAdapter(FeedbackNewCheck.this);
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

			nextpos = quesNo + 1;

			date = c.getString(c.getColumnIndexOrThrow("feedbackDate"));
			attempt = c.getString(c.getColumnIndexOrThrow("feedbackAttempt"));
			limit = c.getString(c.getColumnIndexOrThrow("answerLimit"));

			No = c.getString(c.getColumnIndexOrThrow("questionNo"));
			// title=c.getString(c.getColumnIndexOrThrow("feedbackTitle"));
			question = c.getString(c.getColumnIndexOrThrow("questionTitle"));
			title = intent.getStringExtra("title");
			feedtotalquestions = intent.getStringExtra("feedtotalquestions");
			System.out.println("feedbacknewcheck title " + title + " total "
					+ feedtotalquestions);
			checkString1 = c.getString(c.getColumnIndexOrThrow("optionA"));
			checkString2 = c.getString(c.getColumnIndexOrThrow("optionB"));
			checkString3 = c.getString(c.getColumnIndexOrThrow("optionC"));
			checkString4 = c.getString(c.getColumnIndexOrThrow("optionD"));
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
				String ans = pref.getString(key, "");
				if (ans.contains("A"))
					check1.setChecked(true);
				if (ans.contains("B"))
					check2.setChecked(true);
				if (ans.contains("C"))
					check3.setChecked(true);
				if (ans.contains("D"))
					check4.setChecked(true);
				// answer.setText(pref.getString(key, ""));
			} else
				System.out.println(key + " preference-->"
						+ pref.getString(key, "") + " final");
			System.out.println(key + " preference-->" + pref.getString(key, "")
					+ " final");
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
				show = new Intent(FeedbackNewCheck.this,
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
				show = new Intent(FeedbackNewCheck.this,
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
				show = new Intent(FeedbackNewCheck.this, FeedbackNewRadio.class);
				show.putExtra("_id", _id + "");
				show.putExtra("title", title);
				show.putExtra("NAVIGATED_FROM_NEW_LIST",
						NAVIGATED_FROM_NEW_LIST);
				show.putExtra("feedtotalquestions", feedtotalquestions);
				Log.e("putting in show", "_id=" + _id);
				startActivity(show);
			}

			if (type.contentEquals("Checkbox")) {
				show = new Intent(FeedbackNewCheck.this, FeedbackNewCheck.class);
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
		System.out.println("Answer (Store) --->" + answer);
		Editor editor = pref.edit();
		editor.putString(key, answer);
		editor.commit();
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

}// end of class
