package com.mobcast.calc;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;
import com.mobcast.view.Slider;
import com.mobcast.view.Slider.OnValueChangedListener;
import com.sanofi.in.mobcast.R;

public class MonthlyActivity extends FragmentActivity {

	private static final String TAG = MonthlyActivity.class.getSimpleName();

	private EditText mMidPer;
	private EditText mMonPer;
	 private Slider mMidSlider;
//	private SeekBar mMidSlider;
	private Slider mMonSlider;

	private TextView mMidIncePer;
	private TextView mMonIncePer;
	private TextView mMidInceRs;
	private TextView mMonInceRs;
	private TextView mInceRs;

	private TextView mMidInceRsSy;
	private TextView mMonInceRsSy;
	private TextView mInceRsSy;

	private ScrollView mScrollPer;
	private ScrollView mScrollInce;

	private Button mSubmit;

	private Toast mToast;

	private Typeface mTypeFace;

	private boolean isSliderView = true;

	private Animation mAnimLeftIn;
	private Animation mAnimLeftOut;

	@Override
	protected void onCreate(Bundle bundleSavedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(bundleSavedInstance);
		setContentView(R.layout.activity_monthly);
		initUi();
		setUiListener();
		setRupeeFont();
	}

	private void initUi() {
		mMidPer = (EditText) findViewById(R.id.activity_month_midmonth_per);
		mMonPer = (EditText) findViewById(R.id.activity_month_month_per);
		 mMidSlider = (Slider) findViewById(R.id.activity_month_mid_slider);
//		mMidSlider = (SeekBar) findViewById(R.id.activity_month_mid_slider);
		mMonSlider = (Slider) findViewById(R.id.activity_month_month_slider);

		mMidIncePer = (TextView) findViewById(R.id.activity_month_mid_incen_per);
		mMidInceRs = (TextView) findViewById(R.id.activity_month_mid_incen_rs);
		mMidInceRsSy = (TextView) findViewById(R.id.activity_month_mid_incen_rs_sy);

		mMonIncePer = (TextView) findViewById(R.id.activity_month_month_incen_per);
		mMonInceRs = (TextView) findViewById(R.id.activity_month_month_incen_rs);
		mMonInceRsSy = (TextView) findViewById(R.id.activity_month_month_incen_rs_sy);

		mInceRs = (TextView) findViewById(R.id.activity_month_incen_rs);
		mInceRsSy = (TextView) findViewById(R.id.activity_month_incen_rs_sy);

		mScrollInce = (ScrollView) findViewById(R.id.activity_month_sl2);
		mScrollPer = (ScrollView) findViewById(R.id.activity_month_sl1);

		mSubmit = (Button) findViewById(R.id.activity_month_showIncentive);

	}

	private void setUiListener() {
		 mMidSlider.setOnValueChangedListener(new OnValueChangedListener() {
		 @Override
		 public void onValueChanged(int value) {
		 // TODO Auto-generated method stub
		 try {
		 mMidPer.setText(String.valueOf(value));
		 } catch (Exception e) {
		 Log.i(TAG, e.toString());
		 }
		 }
		 });
//
//		mMidSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			int progressChanged = 0;
//			@Override
//			public void onStopTrackingTouch(SeekBar mSeekBar) {
//				// TODO Auto-generated method stub
//				try {
//					mMidPer.setText(String.valueOf(progressChanged));
//				} catch (Exception e) {
//					Log.i(TAG, e.toString());
//				}
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar mSeekBar) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onProgressChanged(SeekBar mSeekBar, int value, boolean fromUser) {
//				// TODO Auto-generated method stub
//				
//			}
//		});

		mMonSlider.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					mMonPer.setText(String.valueOf(value));
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (isSliderView) { // show incentive
					mAnimLeftIn = AnimationUtils.loadAnimation(
							MonthlyActivity.this, R.anim.slide_left);
					mAnimLeftIn.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub
							mScrollPer.setVisibility(View.GONE);
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							mScrollInce.setVisibility(View.VISIBLE);
							mSubmit.setText("Close");
							isSliderView = !isSliderView;
							calculateMonthIncentive();
						}
					});
					mScrollInce.startAnimation(mAnimLeftIn);
				} else { // show slider view
					mAnimLeftOut = AnimationUtils.loadAnimation(
							MonthlyActivity.this, R.anim.slide_right);
					mAnimLeftOut.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation arg0) {
							// TODO Auto-generated method stub
							mScrollInce.setVisibility(View.GONE);
						}

						@Override
						public void onAnimationRepeat(Animation arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onAnimationEnd(Animation arg0) {
							// TODO Auto-generated method stub
							mScrollPer.setVisibility(View.VISIBLE);
							mSubmit.setText("Show Incentive");
							isSliderView = !isSliderView;
						}
					});
					mScrollPer.startAnimation(mAnimLeftOut);
				}
			}
		});

		mMidPer.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence mString, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence mString, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable mString) {
				// TODO Auto-generated method stub
				validateEditBox(mString, 0);
			}
		});

		mMonPer.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable mString) {
				// TODO Auto-generated method stub
				validateEditBox(mString, 1);
			}
		});
		
		mSubmit.setOnTouchListener(mOnTouchEffect);
	}

	private void validateEditBox(Editable mString, int mSlider) {
		if (!TextUtils.isEmpty(mString.toString())) {
			if (mString.toString().length() >= 2) {
				if (Integer.parseInt(mString.toString()) >= 90
						&& Integer.parseInt(mString.toString()) <= 110) {
					switch (mSlider) {
					case 0:
						mMidSlider
								.setValue(Integer.parseInt(mString.toString()));
						break;
					case 1:
						mMonSlider
								.setValue(Integer.parseInt(mString.toString()),true);
						break;
					}
				} else{
					if (mToast == null
							|| mToast.getView().getWindowVisibility() != View.VISIBLE) {
						mToast.makeText(MonthlyActivity.this,
								"Please enter value between 90 & 110!",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else {
			if (mToast == null
					|| mToast.getView().getWindowVisibility() != View.VISIBLE) {
				mToast.makeText(MonthlyActivity.this,
						"Please enter digits only!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mMidInceRsSy.setTypeface(mTypeFace);
		mMonInceRsSy.setTypeface(mTypeFace);
		mInceRsSy.setTypeface(mTypeFace);

		mMidInceRsSy.setText("`");
		mMonInceRsSy.setText("`");
		mInceRsSy.setText("`");

	}
	
	View.OnTouchListener mOnTouchEffect = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			// btn.setBackgroundResource(R.drawable.gradient2);

			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(0.5f);}
				v.getBackground().setAlpha(70);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// if(android.os.Build.VERSION.SDK_INT>=11){ v.setAlpha(1);}
				v.getBackground().setAlpha(255);
			}

			return false;
		}

	};
	
	public void calculateMonthIncentive(){
		try{
			mMonIncePer.setText(mMonPer.getText().toString());
			mMonInceRs.setText(String.valueOf(Constants.mMapMonth[Integer
					.parseInt(mMonPer.getText().toString()) - 90]));
		}catch(Exception e){
			Log.i(TAG, e.toString());
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
