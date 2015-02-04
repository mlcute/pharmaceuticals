package com.sanofi.in.mobcast;

import java.util.HashMap;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.sanofi.in.mobcast.R;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;

public class Recruitment extends Activity {

	Intent intent;
	TextView design, minExp, loc, contact, title, contact2;
	Button skills, jobdesc, qualification, ctc, rShare, cbar, dbar;
	ImageButton fbshare;
	String id, sk, jd, qf, ct, ti;
	String jobID;
	LinearLayout details;
	Reports reports;
	TextView skillText, jdText, ctcText, qfText;
	Drawable imgminus, imgplus;
	public final static int CONTACT_PICK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recruit);
		intent = getIntent();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
		// textviews
		title = (TextView) findViewById(R.id.RTitle);
		reports = new Reports (getApplicationContext(),"Recruitment");
		design = (TextView) findViewById(R.id.Rdesig);
		minExp = (TextView) findViewById(R.id.RExp);
		loc = (TextView) findViewById(R.id.RLoc);
		contact = (TextView) findViewById(R.id.Rcontact);
		contact2 = (TextView) findViewById(R.id.Rcontact2);
		skillText = (TextView) findViewById(R.id.RSkillText);
		jdText = (TextView) findViewById(R.id.RJDText);
		ctcText = (TextView) findViewById(R.id.RctText);
		qfText = (TextView) findViewById(R.id.RqfText);

		details = (LinearLayout) findViewById(R.id.job_details);

		// buttons
		skills = (Button) findViewById(R.id.RSkill);
		dbar = (Button) findViewById(R.id.details_bar);
		cbar = (Button) findViewById(R.id.contact_bar);
		jobdesc = (Button) findViewById(R.id.RJd);
		qualification = (Button) findViewById(R.id.Rqf);
		ctc = (Button) findViewById(R.id.Rct);
		rShare = (Button) findViewById(R.id.RbtnShare);
		rShare.setOnTouchListener(myhandler2);
		fbshare = (ImageButton) findViewById(R.id.RbtnSharefb);
		fbshare.setOnTouchListener(myhandler2);
		fbshare.setOnClickListener(sharehandler);
		// strings values

		skillText.setVisibility(TextView.GONE);
		jdText.setVisibility(TextView.GONE);
		ctcText.setVisibility(TextView.GONE);
		qfText.setVisibility(TextView.GONE);

		if (!intent.equals(null)) {
			id = intent.getStringExtra(RecruitList.KEY_ID);
			jobID = id;
			reports.updateRead(jobID);
			title.setText(intent.getStringExtra(RecruitList.KEY_TITLE));
			design.setText(intent.getStringExtra(RecruitList.KEY_DESIGNATION));
			loc.setText(intent.getStringExtra(RecruitList.KEY_LOC));
			minExp.setText(intent.getStringExtra(RecruitList.KEY_MINEXP));
			Log.d(RecruitList.KEY_SHARECONTACT_ALLOWED, getIntent()
					.getStringExtra(RecruitList.KEY_SHARECONTACT_ALLOWED) + "");

			if (intent.getStringExtra(RecruitList.KEY_SHARECONTACT_ALLOWED).equalsIgnoreCase("off")) {
				rShare.setVisibility(Button.GONE);
			}

			Log.d(RecruitList.KEY_SHARING_ALLOWED,""+intent.getStringExtra(RecruitList.KEY_SHARING_ALLOWED));
			if (intent.getStringExtra(RecruitList.KEY_SHARING_ALLOWED).equalsIgnoreCase("off")) {
				fbshare.setVisibility(ImageButton.GONE);
			}
			contact.setText(intent.getStringExtra(RecruitList.KEY_CONTACTNAME)
					+ "\n"
					+ (intent.getStringExtra(RecruitList.KEY_CONTACTDESIGNATION))					
					+ "\n"
					+ (intent.getStringExtra(RecruitList.KEY_CONTACTNUMBER))
					+ "\n");

			contact2.setText(intent
					.getStringExtra(RecruitList.KEY_CONTACTEMAIL) + "\n");

			contact2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

									
					reports.updateEmailClicked(jobID);

					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_EMAIL, new String[]{contact2.getText()
							.toString().trim()});

					i.putExtra(Intent.EXTRA_SUBJECT, "Reply to recruitment No "
							+ jobID);
					// i.putExtra(Intent.EXTRA_TEXT , "body of email");
					try {
						startActivity(Intent.createChooser(i, "Send mail..."));
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(Recruitment.this,
								"There are no email clients installed.",
								Toast.LENGTH_SHORT).show();
					}

				}
			});

			contact.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				reports.updateCallClicked(jobID);

					String numberString = intent
							.getStringExtra(RecruitList.KEY_CONTACTNUMBER);
					if (!numberString.equals("")) {
						Uri number = Uri.parse("tel:" + numberString);
						Intent dial = new Intent(Intent.ACTION_CALL, number);
						startActivity(dial);
					}

				}
			});

			// dialog interface being made ready
			
			sk = intent.getStringExtra(RecruitList.KEY_SKILL);
			jd = intent.getStringExtra(RecruitList.KEY_JOBDESC);
			qf = intent.getStringExtra(RecruitList.KEY_QUAL);
			ct = intent.getStringExtra(RecruitList.KEY_CTC);

		}
		// button on click listener
		skills.setOnClickListener(new MyOnclickListener());
		jobdesc.setOnClickListener(new MyOnclickListener());
		qualification.setOnClickListener(new MyOnclickListener());
		ctc.setOnClickListener(new MyOnclickListener());

		dbar.setOnClickListener(new MyOnclickListener());
		cbar.setOnClickListener(new MyOnclickListener());

		rShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				reports.updateRefferenceShared(jobID);

				startActivityForResult(new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI), CONTACT_PICK);

			}
		});

	}// onCreate method ends here

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// log.v("OnactivityResult", "reached");
		if (requestCode == CONTACT_PICK) {
			if (resultCode == RESULT_OK) {
				String phoneNumber = "", email = "";
				String name = "", hasph = "", id = "";
				Uri uri = Uri.parse(data.getDataString());
				// Log.v("uri", uri.toString());
				// Toast.makeText(getApplicationContext(), uri.toString(),
				// Toast.LENGTH_LONG).show();
				Cursor cursor = managedQuery(uri, null, null, null, null);
				if (cursor.moveToFirst()) {

					name = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
					id = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Data._ID));
					// Log.v("id", id);
					// Toast.makeText(getApplicationContext(), "id= "+id,
					// Toast.LENGTH_LONG).show();
					hasph = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Data.HAS_PHONE_NUMBER));
					if (Integer.parseInt(hasph) == 1) {
						Cursor cur = managedQuery(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
								null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ "=" + id, null, null);
						if (cur.moveToFirst()) {
							phoneNumber = cur
									.getString(cur
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						}
					}
					Cursor cur = managedQuery(
							ContactsContract.CommonDataKinds.Email.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ "=" + id, null, null);
					if (cur.moveToFirst()) {
						email = cur
								.getString(cur
										.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
					}
				}// first cursor if ends here

				hasph = "Name: " + name;

				if (phoneNumber.equals("") || phoneNumber.equals(null)) {
					Toast.makeText(getApplicationContext(),
							"contact without phone number", Toast.LENGTH_LONG)
							.show();
				} else {
					hasph += "\nNumber: " + phoneNumber;
				}
				if (email.equals("") || email.equals(null)) {
					Toast.makeText(getApplicationContext(),
							"contact without email", Toast.LENGTH_LONG).show();
				} else {
					hasph += "\nEmail: " + email;
				}
				// hasph+="\nJobID: "+jobID;
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");

				// log.v("data", hasph);
				i.putExtra(Intent.EXTRA_SUBJECT,
						"Reference to the recruitment Job ID " + jobID);
				i.putExtra(Intent.EXTRA_TEXT, hasph);
				String email1 = contact2.getText().toString().trim();
				
				i.putExtra(Intent.EXTRA_EMAIL,  new String[]{email1});
				try {

					startActivity(Intent.createChooser(i, "Send mail"));

				} catch (android.content.ActivityNotFoundException ex) {
					Toast.makeText(getApplicationContext(),
							"There are no email clients installed.",
							Toast.LENGTH_SHORT).show();
				}

			}// inner resultCode if ends here
		}// requestCode if

	}// onActivity result

	class MyOnclickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			// Log.v("in OnClick", "in on skill"+v.toString());
			if (v.equals(skills)) {
				// skills code goes here
				if (skillText.getVisibility() == 8) {
					skills.setBackgroundResource(R.drawable.gradient3);
					skills.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);

					skillText.setText(sk);
					skillText.setVisibility(TextView.VISIBLE);
				} else {
					skillText.setVisibility(TextView.GONE);
					skills.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
					skills.setBackgroundResource(R.drawable.gradient);
				}
				// Log.v("in skill", "skill ke under");
			} else if (v.equals(jobdesc)) {
				// jobDescription code goes here
				if (jdText.getVisibility() == 8) {
					jdText.setText(jd);
					jdText.setVisibility(TextView.VISIBLE);
					jobdesc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);
					jobdesc.setBackgroundResource(R.drawable.gradient3);
				} else {
					jdText.setVisibility(TextView.GONE);
					jobdesc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
					jobdesc.setBackgroundResource(R.drawable.gradient);
				}
			} else if (v.equals(qualification)) {
				// qualification code goes here
				if (qfText.getVisibility() == 8) {
					qfText.setText(qf);
					qfText.setVisibility(TextView.VISIBLE);
					qualification.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);
					qualification.setBackgroundResource(R.drawable.gradient3);
				} else {
					qfText.setVisibility(TextView.GONE);
					qualification.setBackgroundResource(R.drawable.gradient);
					qualification.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
				}

			} else if (v.equals(cbar)) {

				if (contact.getVisibility() == 8) {
					contact.setText(intent
							.getStringExtra(RecruitList.KEY_CONTACTNAME)
							+ "\n"
							+ (intent
									.getStringExtra(RecruitList.KEY_CONTACTNUMBER))
							+ "\n");
					contact2.setText((intent
							.getStringExtra(RecruitList.KEY_CONTACTEMAIL))
							+ "\n");
					contact.setVisibility(TextView.VISIBLE);
					contact2.setVisibility(TextView.VISIBLE);
					cbar.setBackgroundResource(R.drawable.gradient);
					cbar.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);
					// Toast.makeText(getBaseContext(), "contact visible",
					// Toast.LENGTH_SHORT).show();

					contact.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							reports.updateCallClicked(jobID);

							String numberString = intent
									.getStringExtra(RecruitList.KEY_CONTACTNUMBER);
							if (!numberString.equals("")) {
								Uri number = Uri.parse("tel:" + numberString);
								Intent dial = new Intent(Intent.ACTION_CALL,
										number);
								startActivity(dial);
							}

						}
					});

					contact2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

						reports.updateEmailClicked(jobID);

							Intent i = new Intent(Intent.ACTION_SEND);
							i.setType("message/rfc822");
							i.putExtra(Intent.EXTRA_EMAIL,
									new String[] { contact2.getText()
											.toString().trim() });
							i.putExtra(Intent.EXTRA_SUBJECT,
									"Reply to recruitment No.  " + jobID);
							// i.putExtra(Intent.EXTRA_TEXT , "body of email");
							try {
								startActivity(Intent.createChooser(i,
										"Send mail"));
							} catch (android.content.ActivityNotFoundException ex) {
								Toast.makeText(
										Recruitment.this,
										"There are no email clients installed.",
										Toast.LENGTH_SHORT).show();
							}

						}
					});

				} else {
					contact.setVisibility(TextView.GONE);
					contact2.setVisibility(TextView.GONE);
					cbar.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
					cbar.setBackgroundResource(R.drawable.gradient3);
				}

			}

			else if (v.equals(dbar)) {

				if (details.getVisibility() == 8) {
					dbar.setBackgroundResource(R.drawable.gradient);
					dbar.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);
					details.setVisibility(LinearLayout.VISIBLE);

				} else {

					details.setVisibility(LinearLayout.GONE);
					dbar.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
					dbar.setBackgroundResource(R.drawable.gradient3);
				}

			}

			else {
				// ctc code goes here
				if (ctcText.getVisibility() == 8) {
					ctcText.setText(ct);
					ctcText.setVisibility(TextView.VISIBLE);
					ctc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.minus_round, 0);
					ctc.setBackgroundResource(R.drawable.gradient3);
				} else {
					ctcText.setVisibility(TextView.GONE);
					ctc.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.plus_round, 0);
					ctc.setBackgroundResource(R.drawable.gradient);
				}
			}

			// Log.v("in Onclick", "after dialog"+dialog.toString());
		}// onClick method ends here

	}// MyOnclickListener class ends here

	View.OnTouchListener myhandler2 = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				v.setBackgroundResource(R.drawable.gradient2);
				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				v.setBackgroundResource(R.drawable.button_gradient);

				int padding_in_dp = 7; // 6 dps
				final float scale = getResources().getDisplayMetrics().density;
				int padding_in_px = (int) (padding_in_dp * scale + 0.5f);

				v.setPadding(padding_in_px, padding_in_px, padding_in_px,
						padding_in_px);
			}

			return false;
		}

	};

	View.OnClickListener sharehandler = new View.OnClickListener() {
		public void onClick(View v) {

			reports.updateShare(jobID);
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");

			// share.setType("message/rfc822");

			String shareBody = "RECRUITMENT \n";

			shareBody = shareBody + "TITLE: ";
			shareBody = shareBody
					+ (intent.getStringExtra(RecruitList.KEY_TITLE)) + "\n";

			shareBody = shareBody + "Designation: ";
			shareBody = shareBody
					+ intent.getStringExtra(RecruitList.KEY_DESIGNATION) + "\n";

			shareBody = shareBody + "Minimum Experience: ";
			shareBody = shareBody
					+ intent.getStringExtra(RecruitList.KEY_MINEXP) + "\n";

			shareBody = shareBody + "Location: ";
			shareBody = shareBody + intent.getStringExtra(RecruitList.KEY_LOC)
					+ "\n";

			shareBody = shareBody + "Job Description: ";
			shareBody = shareBody
					+ intent.getStringExtra(RecruitList.KEY_JOBDESC) + "\n";

			shareBody = shareBody + "Skill: ";
			shareBody = shareBody
					+ intent.getStringExtra(RecruitList.KEY_SKILL) + "\n";

			shareBody = shareBody + "CTC: ";
			shareBody = shareBody + intent.getStringExtra(RecruitList.KEY_CTC)
					+ "\n";

			shareBody = shareBody + "Qualification: ";
			shareBody = shareBody + intent.getStringExtra(RecruitList.KEY_QUAL)
					+ "\n";

			shareBody = shareBody + "CONTACT: \n";
			shareBody = shareBody
					+ intent.getStringExtra(RecruitList.KEY_CONTACTNAME) + "\n"
					+ (intent.getStringExtra(RecruitList.KEY_CONTACTNUMBER))
					+ "\n"
					+ (intent.getStringExtra(RecruitList.KEY_CONTACTEMAIL))
					+ "\n";

			share.putExtra(Intent.EXTRA_TEXT, shareBody);
			share.putExtra(android.content.Intent.EXTRA_SUBJECT, "Recruitment");

			startActivity(Intent.createChooser(share, "Share Text"));

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


}// Recruitment class ends here