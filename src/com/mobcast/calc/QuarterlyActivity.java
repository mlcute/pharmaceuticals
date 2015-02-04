package com.mobcast.calc;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.Constants;
import com.mobcast.util.Utilities;
import com.mobcast.view.ExpandedListView;
import com.mobcast.view.MyScrollView;
import com.mobcast.view.MyScrollView.OnScrollStoppedListener;
import com.mobcast.view.Slider;
import com.mobcast.view.Slider.OnValueChangedListener;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class QuarterlyActivity extends FragmentActivity {

	private static final String TAG = QuarterlyActivity.class.getSimpleName();

	private EditText mMonPer1;
	private EditText mMonPer2;
	private EditText mMonPer3;
	private Slider mMonSlider1;
	private Slider mMonSlider2;
	private Slider mMonSlider3;

	private TextView mMonPer1Tv;
	private TextView mMonPer2Tv;
	private TextView mMonPer3Tv;

	private TextView mQuarterCoverage;
	private TextView mQuarterCoverageWt;

	private TextView mCPA;
	private TextView mCPAWt;
	private TextView mKPI;

	private TextView mKPIRsSy;

	private RadioGroup mRadioGroup;
	private int mPreviousQuarter = 0;

	private LinearLayout mProLayout1;
	private LinearLayout mProLayout2;
	private LinearLayout mProLayout3;
	private LinearLayout mProLayout4;
	private LinearLayout mProLayout5;
	private ExpandedListView mListView;

	private LinearLayout mScrollPerLayout;

	private MyScrollView mScrollPer;
	private ScrollView mScrollInce;

	private Button mSubmit;
	private Button mClose;

	private Toast mToast;

	private Typeface mTypeFace;

	private ListProductAdapter mAdapter;

	private Animation mAnimLeftIn;
	private Animation mAnimLeftOut;

	private static final int PROCOUNT = 5;

	private ArrayList<Product> mProduct;

	@Override
	protected void onCreate(Bundle bundleSavedInstance) {
		// TODO Auto-generated method stub
		super.onCreate(bundleSavedInstance);
		setContentView(R.layout.activity_quarterly);
		initUi();
		setUiListener();
		setRupeeFont();
		getProductJSON();
		setListView();
		// setProLayout();
	}

	private void initUi() {
		mMonPer1 = (EditText) findViewById(R.id.activity_quarter_month_1_per);
		mMonPer2 = (EditText) findViewById(R.id.activity_quarter_month_2_per);
		mMonPer3 = (EditText) findViewById(R.id.activity_quarter_month_3_per);

		mMonSlider1 = (Slider) findViewById(R.id.activity_quarter_month_1_slider);
		mMonSlider2 = (Slider) findViewById(R.id.activity_quarter_month_2_slider);
		mMonSlider3 = (Slider) findViewById(R.id.activity_quarter_month_3_slider);

		mMonPer1Tv = (TextView) findViewById(R.id.activity_quarter_month_1);
		mMonPer2Tv = (TextView) findViewById(R.id.activity_quarter_month_2);
		mMonPer3Tv = (TextView) findViewById(R.id.activity_quarter_month_3);

		mListView = (ExpandedListView) findViewById(R.id.activity_quarter_lv);
		mListView.setExpanded(true);

		mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupQuarter);

		mScrollPerLayout = (LinearLayout) findViewById(R.id.quarterLayout);

		mQuarterCoverage = (TextView) findViewById(R.id.activity_quarter_coverage);
		mQuarterCoverageWt = (TextView) findViewById(R.id.activity_quarter_coverage_wt);

		mCPA = (TextView) findViewById(R.id.activity_quarter_cpa);
		mCPAWt = (TextView) findViewById(R.id.activity_quarter_cpa_avg);

		mKPI = (TextView) findViewById(R.id.activity_quarter_kpi);
		mKPIRsSy = (TextView) findViewById(R.id.activity_quarter_kpi_rs_sy);

		mScrollInce = (ScrollView) findViewById(R.id.activity_quarter_sl2);
		mScrollPer = (MyScrollView) findViewById(R.id.activity_quarter_sl1);

		mSubmit = (Button) findViewById(R.id.activity_quarter_showIncentive);
		mClose = (Button) findViewById(R.id.activity_quarter_close);

	}

	private void setUiListener() {

		mScrollPer.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mScrollPer.startScrollerTask();
					mScrollPerLayout.setEnabled(false);
					Log.i(TAG, "setOnTouchListener");
				}
				return false;
			}
		});
		mScrollPer.setOnScrollStoppedListener(new OnScrollStoppedListener() {
			public void onScrollStopped() {
				mScrollPerLayout.setEnabled(true);
			}
		});

		mMonSlider1.setOnValueChangedListener(new OnValueChangedListener() {
			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					mMonPer1.setText(String.valueOf(value));
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mMonSlider2.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					mMonPer2.setText(String.valueOf(value));
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mMonSlider3.setOnValueChangedListener(new OnValueChangedListener() {

			@Override
			public void onValueChanged(int value) {
				// TODO Auto-generated method stub
				try {
					mMonPer3.setText(String.valueOf(value));
				} catch (Exception e) {
					Log.i(TAG, e.toString());
				}
			}
		});

		mSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mAnimLeftIn = AnimationUtils.loadAnimation(
						QuarterlyActivity.this, R.anim.slide_left);
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
						mClose.setVisibility(View.VISIBLE);
					}
				});
				mScrollInce.startAnimation(mAnimLeftIn);
			}
		});

		mClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mAnimLeftOut = AnimationUtils.loadAnimation(
						QuarterlyActivity.this, R.anim.slide_right);
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
						mClose.setVisibility(View.GONE);
					}
				});
				mScrollPer.startAnimation(mAnimLeftOut);

			}
		});

		mMonPer1.addTextChangedListener(new TextWatcher() {
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

		mMonPer2.addTextChangedListener(new TextWatcher() {

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

		mMonPer3.addTextChangedListener(new TextWatcher() {

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
				validateEditBox(mString, 2);
			}
		});

		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup mGroup, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radioQuarter1:
					saveQuarter(mPreviousQuarter);
					mPreviousQuarter = 0;
					restoreQuarter(0);
					break;
				case R.id.radioQuarter2:
					saveQuarter(mPreviousQuarter);
					mPreviousQuarter = 1;
					restoreQuarter(1);
					break;
				case R.id.radioQuarter3:
					saveQuarter(mPreviousQuarter);
					mPreviousQuarter = 2;
					restoreQuarter(2);
					break;
				case R.id.radioQuarter4:
					saveQuarter(mPreviousQuarter);
					mPreviousQuarter = 3;
					restoreQuarter(3);
					break;
				}
			}
		});

		mClose.setOnTouchListener(mTouchEffect);
		mSubmit.setOnTouchListener(mTouchEffect);
	}

	private void validateEditBox(Editable mString, int mSlider) {
		if (!TextUtils.isEmpty(mString.toString())) {
			if (mString.toString().length() >= 2) {
				if (Integer.parseInt(mString.toString()) >= 90
						&& Integer.parseInt(mString.toString()) <= 110) {
					switch (mSlider) {
					case 0:
						mMonSlider1.setValue(Integer.parseInt(mString
								.toString()));
						break;
					case 1:
						mMonSlider2.setValue(Integer.parseInt(mString
								.toString()));
						break;
					case 2:
						mMonSlider3.setValue(Integer.parseInt(mString
								.toString()));
						break;
					}
				} else {
					if (mToast == null
							|| mToast.getView().getWindowVisibility() != View.VISIBLE) {
						mToast.makeText(QuarterlyActivity.this,
								"Please enter value between 90 & 110!",
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		} else {
			if (mToast == null
					|| mToast.getView().getWindowVisibility() != View.VISIBLE) {
				mToast.makeText(QuarterlyActivity.this,
						"Please enter digits only!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mKPIRsSy.setTypeface(mTypeFace);

		mKPIRsSy.setText("`");
	}

	private void setListView() {
		if (mProduct.size() > 0) {
			mAdapter = new ListProductAdapter(QuarterlyActivity.this, mProduct,
					mPreviousQuarter);
			mListView.setAdapter(mAdapter);
			mListView.setExpanded(true);
		}
	}

	private void saveQuarter(int quarterType) {
		switch (quarterType) {
		case 0:
			ApplicationLoader.getPreferences().setQuarter1(
					new String[] { mMonPer1.getText().toString(),
							mMonPer2.getText().toString(),
							mMonPer3.getText().toString() });
			break;
		case 1:
			ApplicationLoader.getPreferences().setQuarter2(
					new String[] { mMonPer1.getText().toString(),
							mMonPer2.getText().toString(),
							mMonPer3.getText().toString() });
			break;
		case 2:

			ApplicationLoader.getPreferences().setQuarter3(
					new String[] { mMonPer1.getText().toString(),
							mMonPer2.getText().toString(),
							mMonPer3.getText().toString() });
			break;
		case 3:
			ApplicationLoader.getPreferences().setQuarter4(
					new String[] { mMonPer1.getText().toString(),
							mMonPer2.getText().toString(),
							mMonPer3.getText().toString() });
			break;
		}
	}

	private void restoreQuarter(int quarterType) {
		String mStr[] = new String[3];
		switch (quarterType) {
		case 0:
			mStr = ApplicationLoader.getPreferences().getQuarter1();
			break;
		case 1:
			mStr = ApplicationLoader.getPreferences().getQuarter2();
			break;
		case 2:
			mStr = ApplicationLoader.getPreferences().getQuarter3();
			break;
		case 3:
			mStr = ApplicationLoader.getPreferences().getQuarter4();
			break;
		}

		if (!TextUtils.isEmpty(mStr[0])) {
			mMonPer1.setText(mStr[0]);
		} else {
			mMonPer1.setText("90");
		}

		if (!TextUtils.isEmpty(mStr[1])) {
			mMonPer2.setText(mStr[1]);
		} else {
			mMonPer2.setText("90");
		}

		if (!TextUtils.isEmpty(mStr[2])) {
			mMonPer3.setText(mStr[2]);
		} else {
			mMonPer3.setText("90");
		}
	}

	View.OnTouchListener mTouchEffect = new View.OnTouchListener() {

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

	public class ListProductAdapter extends BaseAdapter {

		public Context mContext;
		public ArrayList<Product> mListProduct;
		public String[] mSliderValue;

		public ListProductAdapter(Context mContext,
				ArrayList<Product> mListProduct, int QuarterType) {
			this.mContext = mContext;
			this.mListProduct = mListProduct;
			mSliderValue = new String[mListProduct.size()];
		}

		@Override
		public int getCount() {
			return mListProduct.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			ViewHolder holder = null;

			if (convertView == null) {
				LayoutInflater li = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = li.inflate(R.layout.item_incen_quarter_product, null);
				holder = new ViewHolder();
				holder.mListName = (TextView) v
						.findViewById(R.id.item_incen_quarter_product_name);
				holder.mListPer = (EditText) v
						.findViewById(R.id.item_incen_quarter_per);
				holder.mListSlider = (Slider) v
						.findViewById(R.id.item_incen_quarter_product_slider);
				holder.mListSliderMin = (TextView) v
						.findViewById(R.id.item_incen_quarter_product_min_per);
				holder.mListSliderMax = (TextView) v
						.findViewById(R.id.item_incen_quarter_product_max_per);
				v.setTag(holder);
			} else {
				v = convertView;
				holder = (ViewHolder) v.getTag();
			}

			holder.mListName.setText(mProduct.get(position).getmName());
			holder.mListSliderMin.setText(mProduct.get(position).getmMin()
					+ "%");
			holder.mListSliderMax.setText(mProduct.get(position).getmMax()
					+ "%");
			holder.mListSlider.setMin(Integer.parseInt(mProduct.get(position)
					.getmMin()));
			holder.mListSlider.setMax(Integer.parseInt(mProduct.get(position)
					.getmMax()));

			setListSlider(holder.mListSlider, holder.mListPer, position);
			setListEditPer(holder.mListPer, holder.mListSlider,
					mProduct.get(position).getmMin(), mProduct.get(position)
							.getmMax());
			setListSliderValue(holder.mListSlider, mSliderValue[position]);
			return v;
		}

		public void restoreQuarterType(int quarterType) {
			switch (quarterType) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				break;
			case 3:
				break;
			}
		}

		public void setListSliderValue(Slider mSlider, String value) {
			try {
				mSlider.setValue(Integer.parseInt(value));
			} catch (Exception e) {

			}
		}

		public void setListSlider(Slider mSlider, final EditText mEditText,
				final int position) {
			mSlider.setOnValueChangedListener(new OnValueChangedListener() {
				@Override
				public void onValueChanged(int value) {
					// TODO Auto-generated method stub
					try {
						mEditText.setText(String.valueOf(value));
						mSliderValue[position] = String.valueOf(value);
					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
				}
			});
		}

		public void setListEditPer(EditText mEditPer, final Slider mSlider,
				final String min, final String max) {
			mEditPer.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence mString, int arg1,
						int arg2, int arg3) {
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
					validateListEditBox(mSlider, mString.toString(), min, max);
				}
			});
		}

		public void saveProductQuarter(Slider mSlider, int quarterType) {
			switch (quarterType) {
			case 0:
				String s[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s[i] = mSliderValue[i];
				}
				ApplicationLoader.getPreferences().setProduct1(s);
				break;
			case 1:
				String s1[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s1[i] = mSliderValue[i];
				}
				ApplicationLoader.getPreferences().setProduct1(s1);
				break;
			case 2:
				String s2[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s2[i] = mSliderValue[i];
				}
				ApplicationLoader.getPreferences().setProduct1(s2);
				break;
			case 3:
				String s3[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s3[i] = mSliderValue[i];
				}
				ApplicationLoader.getPreferences().setProduct1(s3);
				break;
			}

		}

		public void validateListEditBox(Slider mSlider, String mString,
				String min, String max) {
			if (!TextUtils.isEmpty(mString.toString())) {
				if (mString.toString().length() >= 2) {
					if (Integer.parseInt(mString.toString()) >= Integer
							.parseInt(min)
							&& Integer.parseInt(mString.toString()) <= Integer
									.parseInt(max)) {
						mSlider.setValue(Integer.parseInt(mString.toString()));

					} else {
						// if (mToast == null
						// || mToast.getView().getWindowVisibility() !=
						// View.VISIBLE) {
						// mToast.makeText(QuarterlyActivity.this,
						// "Please enter value between"+min+" & "+max,
						// Toast.LENGTH_SHORT).show();
						// }
					}
				}
			} else {
				if (mToast == null
						|| mToast.getView().getWindowVisibility() != View.VISIBLE) {
					mToast.makeText(QuarterlyActivity.this,
							"Please enter digits only!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}

	static class ViewHolder {
		TextView mListName;
		EditText mListPer;
		Slider mListSlider;
		TextView mListSliderMin;
		TextView mListSliderMax;
	}

	public void getProductJSON() {
		String str = Utilities.readFile("data.json");
		parseJSON(str);
	}

	public void parseJSON(String str) {
		getProductList(str);
	}

	public void getProductList(String str) {
		mProduct = new ArrayList<Product>();

		try {
			JSONObject mJSONObj = new JSONObject(str);
			JSONArray mJSONArray = mJSONObj.getJSONArray("products");

			for (int i = 0; i < mJSONArray.length(); i++) {
				Product mObj = new Product();
				JSONObject mInnerJSONObj = mJSONArray.getJSONObject(i);

				mObj.setmMax(mInnerJSONObj.getString("max"));
				mObj.setmMin(mInnerJSONObj.getString("min"));
				mObj.setmName(mInnerJSONObj.getString("name"));

				String[] mSlab = new String[Integer.parseInt(mObj.getmMax())
						- Integer.parseInt(mObj.getmMin())];
				for (int j = 0; j < Integer.parseInt(mObj.getmMax())
						- Integer.parseInt(mObj.getmMin()); j++) {
					mSlab[j] = mInnerJSONObj.getString("slab" + (j + 1));
				}
				mObj.setmSlab(mSlab);
				mProduct.add(mObj);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
