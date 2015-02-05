package com.mobcast.calc;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.mobcast.view.AccordionView;
import com.mobcast.view.Slider;
import com.mobcast.view.Slider.OnValueChangedListener;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class IncenProductActivity extends FragmentActivity {

	private static final String TAG = IncenProductActivity.class
			.getSimpleName();

	private AccordionView mAccordionTotal;

	private Toast mToast;

	private TextView mTotalRsSy;
	public Typeface mTypeFace;

	public TextView mTotalProductTv;

	private ListView mListView;
	private ListProductAdapter mAdapter;
	private ArrayList<Product> mProduct;

	public String[] mSliderValueArr;
	public String[] mTotalValueArr;
	public String[] mPerValueArr;
	public String mTotalArr;

	public String mSliderValueAdd;

	public int whichQuarter;
	public boolean isRestore = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_product);
		setSecurity();
		initUi();
		getIntentData();
		getProductJSON();
		setRupeeFont();
		setListView();
	}

	private void initUi() {
		mListView = (ListView) findViewById(R.id.incen_product_lv);
		// mListView.setExpanded(true);

		mAccordionTotal = (AccordionView) findViewById(R.id.incen_product_total_acc_view);
		mAccordionTotal.toggleSection(0);
		mAccordionTotal.setSectionHeaders("Total Product");

		mTotalProductTv = (TextView) findViewById(R.id.incen_product_total_incen);

		mTotalRsSy = (TextView) findViewById(R.id.incen_product_total_rs_sy);

		mTypeFace = Utilities.getFontStyleRupee();
	}

	private void getIntentData() {
		whichQuarter = Integer.parseInt(getIntent().getStringExtra(
				IncenDashBoardActivity.INTENT_QUARTER));
	}

	private void setRupeeFont() {

		mTotalRsSy.setTypeface(mTypeFace);

		mTotalRsSy.setText("`");
	}

	/*
	 * DYNAMIC PRODUCTS
	 */
	private void setListView() {
		if (mProduct != null && mProduct.size() > 0) {
			mAdapter = new ListProductAdapter(IncenProductActivity.this,
					mProduct);
			mListView.setAdapter(mAdapter);
			// mListView.setExpanded(true);
		}
	}

	public class ListProductAdapter extends BaseAdapter {

		public Context mContext;
		public ArrayList<Product> mListProduct;

		public ListProductAdapter(Context mContext,
				ArrayList<Product> mListProduct) {
			this.mContext = mContext;
			this.mListProduct = mListProduct;
			mSliderValueArr = new String[mListProduct.size()];
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
				v = li.inflate(R.layout.item_incen_product, null);
				holder = new ViewHolder();
				holder.mListAccordionView = (AccordionView) v
						.findViewById(R.id.incen_product_acc_view);
				holder.mListPer = (EditText) v
						.findViewById(R.id.incen_product_slider_per);
				holder.mListSlider = (Slider) v
						.findViewById(R.id.incen_product_slider);
				holder.mListSliderMin = (TextView) v
						.findViewById(R.id.incen_product_slider_min);
				holder.mListSliderMax = (TextView) v
						.findViewById(R.id.incen_product_slider_max);
				holder.mListTotal = (TextView) v
						.findViewById(R.id.incen_item_product_total_incen);
				holder.mListTotalRsSy = (TextView) v
						.findViewById(R.id.incen_item_product_rs_sy);
				if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()
						|| ApplicationLoader.getPreferences()
								.isIncenRenealTeam()) {
					holder.mListNameAdd = (TextView) v
							.findViewById(R.id.incen_biosurgery_name);
					holder.mListPerAdd = (EditText) v
							.findViewById(R.id.incen_biosurgery_slider_per);
					holder.mListSliderAdd = (Slider) v
							.findViewById(R.id.incen_biosurgery_slider);
					holder.mListSliderMinAdd = (TextView) v
							.findViewById(R.id.incen_biosurgery_slider_min);
					holder.mListSliderMaxAdd = (TextView) v
							.findViewById(R.id.incen_biosurgery_slider_max);

					holder.bioSurgeryPerLayout = (LinearLayout) v
							.findViewById(R.id.bioSurgeryPerLayout);
					holder.bioSurgerySliderLayout = (LinearLayout) v
							.findViewById(R.id.bioSurgerySliderLayout);
				}
				v.setTag(holder);
			} else {
				v = convertView;
				holder = (ViewHolder) v.getTag();
			}

			holder.mListAccordionView.setSectionHeaders(mProduct.get(position)
					.getmName());

			// holder.mListName.setText(mProduct.get(position).getmName());
			holder.mListSliderMin.setText(mProduct.get(position).getmMin()
					+ "%");
			holder.mListSliderMax.setText(mProduct.get(position).getmMax()
					+ "%");
			holder.mListSlider.setMin(Integer.parseInt(mProduct.get(position)
					.getmMin()) - 2);
			holder.mListSlider.setMax(Integer.parseInt(mProduct.get(position)
					.getmMax()));

			setListSlider(holder.mListSlider, holder.mListSliderAdd,
					holder.mListPer, holder.mListTotal, position);
			setListEditPer(holder.mListSliderAdd, position, holder.mListPer,
					holder.mListSlider, mProduct.get(position).getmMin(),
					mProduct.get(position).getmMax(), holder.mListTotal);
			setListRupeeFont(holder.mListTotalRsSy);
			restoreQuarterType(whichQuarter, position, holder.mListSlider,
					holder.mListPer, holder.mListTotal);

			if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()
					|| ApplicationLoader.getPreferences().isIncenRenealTeam()) {
				if (position == 0) {
					if (ApplicationLoader.getPreferences()
							.isIncenBioSurgeryTeam()) {

						addAdditionalSlider(holder.bioSurgeryPerLayout,
								holder.bioSurgerySliderLayout,
								holder.mListNameAdd, holder.mListSliderAdd,
								"Vial", 50, holder.mListSliderMaxAdd);
						addSetListSlider(holder.mListSlider, holder.mListTotal,
								holder.mListSliderAdd, holder.mListPerAdd,
								true, position, holder.mListPer,
								mProduct.get(position).getmMin(),
								mProduct.get(position).getmMax());
						addSetListEditPer(holder.mListSlider,
								holder.mListTotal, holder.mListPerAdd,
								holder.mListSliderAdd, "50", true, 360, 0);
						addRestoreQuarterType(whichQuarter,
								holder.mListSliderAdd,holder.mListPerAdd);
					} else {
						addAdditionalSlider(holder.bioSurgeryPerLayout,
								holder.bioSurgerySliderLayout,
								holder.mListNameAdd, holder.mListSliderAdd,
								"Boxes", 30,holder.mListSliderMaxAdd);
						addSetListSlider(holder.mListSlider, holder.mListTotal,
								holder.mListSliderAdd, holder.mListPerAdd,
								false, position, holder.mListPer,
								mProduct.get(position).getmMin(),
								mProduct.get(position).getmMax());
						addSetListEditPer(holder.mListSlider,
								holder.mListTotal, holder.mListPerAdd,
								holder.mListSliderAdd, "30", false, 150, 0);
						addRestoreQuarterType(whichQuarter,
								holder.mListSliderAdd, holder.mListPerAdd);
					}
				}
			}

			return v;
		}

		public void addAdditionalSlider(LinearLayout bioSurgeryPerLayout,
				LinearLayout bioSurgerySliderLayout, TextView mListNameAdd,
				Slider mSlider, String mName, int max, TextView mListSliderMaxAdd) {
			bioSurgeryPerLayout.setVisibility(View.VISIBLE);
			bioSurgerySliderLayout.setVisibility(View.VISIBLE);
			mListNameAdd.setText(mName);
			mSlider.setMax(max);
			mListSliderMaxAdd.setText(String.valueOf(max));
		}

		public void setListRupeeFont(TextView mRsSy) {
			mRsSy.setTypeface(mTypeFace);
			mRsSy.setText("`");
		}

		public void restoreQuarterType(int quarterType, int position,
				Slider mSlider, EditText mSliderPer, TextView mTotalView) {
			mSliderValueArr = new String[mProduct.size()];
			mTotalValueArr = new String[mProduct.size()];
			mPerValueArr = new String[mProduct.size()];
			for (int i = 0; i < mProduct.size(); i++) {
				mSliderValueArr[i] = "89";
				mPerValueArr[i] = "0";
			}
			switch (quarterType) {
			case 1:
				mSliderValueArr = ApplicationLoader.getPreferences()
						.getProduct1(mProduct.size());
				mTotalValueArr = ApplicationLoader.getPreferences()
						.getProductValue1(mProduct.size());
				mTotalArr = ApplicationLoader.getPreferences()
						.getProductTotalValue1();
				break;
			case 2:
				mSliderValueArr = ApplicationLoader.getPreferences()
						.getProduct2(mProduct.size());
				mTotalValueArr = ApplicationLoader.getPreferences()
						.getProductValue2(mProduct.size());
				mTotalArr = ApplicationLoader.getPreferences()
						.getProductTotalValue2();
				break;
			case 3:
				mSliderValueArr = ApplicationLoader.getPreferences()
						.getProduct3(mProduct.size());
				mTotalValueArr = ApplicationLoader.getPreferences()
						.getProductValue3(mProduct.size());
				mTotalArr = ApplicationLoader.getPreferences()
						.getProductTotalValue3();
				break;
			case 4:
				mSliderValueArr = ApplicationLoader.getPreferences()
						.getProduct4(mProduct.size());
				mTotalValueArr = ApplicationLoader.getPreferences()
						.getProductValue4(mProduct.size());
				mTotalArr = ApplicationLoader.getPreferences()
						.getProductTotalValue4();
				break;
			}
			checkValuesFromPreferencesNullOrNot();
			setValuesFromPreferences(position, mSlider, mSliderPer, mTotalView);
		}

		public void addRestoreQuarterType(int whichQuarter, Slider mSlider, EditText mEditTextPer) {
			switch (whichQuarter) {
			case 1:
				mSliderValueAdd = ApplicationLoader.getPreferences()
						.getIncenBioSurgeryVialSlider1();
				break;
			case 2:
				mSliderValueAdd = ApplicationLoader.getPreferences()
						.getIncenBioSurgeryVialSlider2();
				break;
			case 3:
				mSliderValueAdd = ApplicationLoader.getPreferences()
						.getIncenBioSurgeryVialSlider3();
				break;
			case 4:
				mSliderValueAdd = ApplicationLoader.getPreferences()
						.getIncenBioSurgeryVialSlider4();
				break;
			}
			addCheckValuesFromPreferencesNullOrNot();
			addSetValuesFromPreferences(mSlider, mEditTextPer);
		}

		public void setValuesFromPreferences(int mPosition, Slider mSlider,
				EditText mSliderPer, TextView mTotalTextView) {

			mSlider.setValue(Integer.parseInt(mSliderValueArr[mPosition]));

			mSliderPer.setText(mPerValueArr[mPosition]);

			mTotalTextView.setText(mTotalValueArr[mPosition]);

			mTotalProductTv.setText(mTotalArr);
		}

		public void addSetValuesFromPreferences(Slider mSlider, EditText mEditTextPer) {
			mSlider.setValue(Integer.parseInt(mSliderValueAdd));
			mEditTextPer.setText(mSliderValueAdd);
		}

		public void checkValuesFromPreferencesNullOrNot() {
			for (int i = 0; i < mProduct.size(); i++) {
				if (mSliderValueArr[i] == null) {
					mSliderValueArr[i] = String.valueOf(Integer
							.parseInt(mProduct.get(i).getmMin()) - 1);
				}
			}

			for (int i = 0; i < mProduct.size(); i++) {
				if (mTotalValueArr[i] == null) {
					mTotalValueArr[i] = "0";
				}
			}

			if (mTotalArr == null) {
				mTotalArr = "0";
			}
			try {
				for (int i = 0; i < mProduct.size(); i++) {
					try {
						mPerValueArr[i] = mSliderValueArr[i];
						if (mSliderValueArr[i] == null
								|| Integer.parseInt(mSliderValueArr[i]) == 89) {
							mPerValueArr[i] = "0";
						}
					} catch (Exception e) {
						mPerValueArr[i] = "0";
					}
				}
			} catch (Exception e) {

			}
		}

		public void addCheckValuesFromPreferencesNullOrNot() {
			try {
				if (mSliderValueAdd == null) {
					mSliderValueAdd = "0";
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
				mSliderValueAdd = "0";
			}
		}

		public void setListSlider(final Slider mSlider,
				final Slider mSliderAdd, final EditText mEditText,
				final TextView mTextView, final int position) {
			mSlider.setOnValueChangedListener(new OnValueChangedListener() {
				@Override
				public void onValueChanged(int value) {
					// TODO Auto-generated method stub
					try {
						if (value < mSlider.getMin() + 2) {
							mEditText.setText("0");
						} else {
							mEditText.setText(String.valueOf(value));
						}
						mSliderValueArr[position] = String.valueOf(value);
						businessIncenLogic(mSlider, mTextView, mSliderAdd,
								position, value, mEditText, mTextView,
								String.valueOf(mSlider.getMin()), false);
					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
				}
			});
		}

		public void addSetListSlider(final Slider mListSlider,
				final TextView mListTotal, final Slider mSlider,
				final EditText mEditText, final boolean isBioSurgery,
				final int position, final EditText mListPer,
				String mProductMin, String mProductMax) {
			mSlider.setOnValueChangedListener(new OnValueChangedListener() {
				@Override
				public void onValueChanged(int value) {
					// TODO Auto-generated method stub
					try {
						mEditText.setText(String.valueOf(value));
					} catch (Exception e) {
						Log.i(TAG, e.toString());
					}
				}
			});
		}

		public void businessIncenLogic(Slider mListSlider, TextView mListTotal,
				Slider mSlider, int mPosition, int mSliderValue,
				EditText mSliderPer, TextView mProductTotal, String minValue,
				boolean isPointToZero) {
			try {
				String[] slab = mProduct.get(mPosition).getmSlab();
				try {
					mProductTotal.setText(slab[mSliderValue
							- Integer.parseInt(mProduct.get(mPosition)
									.getmMin())]);

				} catch (Exception e) {
					Log.i(TAG, e.toString());
					mProductTotal.setText("0");
				}
				if (isPointToZero) {
					if (Integer.parseInt(mSliderPer.getText().toString()) < Integer
							.parseInt(minValue)) {
						mProductTotal.setText("0");
					}
				}

				if (ApplicationLoader.getPreferences().isIncenBioSurgeryTeam()
						|| ApplicationLoader.getPreferences()
								.isIncenRenealTeam()) {
					if (mPosition == 0) {
						if (ApplicationLoader.getPreferences()
								.isIncenBioSurgeryTeam()) {
								addBusinessIncenLogic(mListSlider, mListTotal,
										mSlider, 50, 360);
						} else {
								addBusinessIncenLogic(mListSlider, mListTotal,
										mSlider, 30, 150);	
						}
					}
				}

				mTotalValueArr[mPosition] = mProductTotal.getText().toString();

				int intTotalArr = 0;
				for (int i = 0; i < mProduct.size(); i++) {
					intTotalArr += Integer.parseInt(mTotalValueArr[i]);
				}
				mTotalArr = String.valueOf(intTotalArr);
				mTotalProductTv.setText(mTotalArr);
				saveProductQuarter(whichQuarter);
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}

		public void addBusinessIncenLogic(Slider mListSlider,
				TextView mListTotal, Slider mSlider, int max, int multiple) {
			try {
				String str[] = mProduct.get(0).getmSlab();
				String mProductSlabValue = str[(str.length - 1) / 2];
				if (mListSlider.getValue() >= 100) {
					mListTotal.setText(String.valueOf(Integer
							.parseInt(mProductSlabValue)
							+ (mSlider.getValue() * multiple)));
				}
				mSliderValueAdd = String.valueOf(mSlider.getValue());
				if(!isRestore){
					addSaveQuarter(whichQuarter);	
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}

		public void setListEditPer(final Slider mSliderAdd, final int position,
				final EditText mEditPer, final Slider mSlider,
				final String min, final String max, final TextView mProductTotal) {
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
					validateListEditBox(mSlider, mSliderAdd,
							mString.toString(), min, max, mEditPer,
							mProductTotal, position);
				}
			});
		}

		public void addSetListEditPer(final Slider mListSlider,
				final TextView mListTotal, final EditText mEditPer,
				final Slider mSlider, final String max,
				final boolean isBioSurgery, final int multiple,
				final int mPosition) {
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
					addValidateListEditBox(mListSlider, mListTotal, mSlider,
							mString.toString(), max, mEditPer, multiple,
							mPosition);
					isRestore = false;
				}
			});
		}

		public void saveProductQuarter(int quarterType) {
			switch (quarterType) {
			case 1:
				String s[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s[i] = mSliderValueArr[i];
				}
				ApplicationLoader.getPreferences().setProduct1(s);
				ApplicationLoader.getPreferences().setProductValue1(
						mTotalValueArr);
				ApplicationLoader.getPreferences().setProductTotalValue1(
						mTotalArr);
				break;
			case 2:
				String s1[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s1[i] = mSliderValueArr[i];
				}
				ApplicationLoader.getPreferences().setProduct2(s1);
				ApplicationLoader.getPreferences().setProductValue2(
						mTotalValueArr);
				ApplicationLoader.getPreferences().setProductTotalValue2(
						mTotalArr);
				break;
			case 3:
				String s2[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s2[i] = mSliderValueArr[i];
				}
				ApplicationLoader.getPreferences().setProduct3(s2);
				ApplicationLoader.getPreferences().setProductValue3(
						mTotalValueArr);
				ApplicationLoader.getPreferences().setProductTotalValue3(
						mTotalArr);
				break;
			case 4:
				String s3[] = new String[mListProduct.size()];
				for (int i = 0; i < mListProduct.size(); i++) {
					s3[i] = mSliderValueArr[i];
				}
				ApplicationLoader.getPreferences().setProduct4(s3);
				ApplicationLoader.getPreferences().setProductValue4(
						mTotalValueArr);
				ApplicationLoader.getPreferences().setProductTotalValue4(
						mTotalArr);
				break;
			}

		}

		public void addSaveQuarter(int quarterType) {
			switch (quarterType) {
			case 1:
				ApplicationLoader.getPreferences()
						.setIncenBioSurgeryVialSlider1(mSliderValueAdd);
				break;
			case 2:
				ApplicationLoader.getPreferences()
						.setIncenBioSurgeryVialSlider2(mSliderValueAdd);
				break;
			case 3:
				ApplicationLoader.getPreferences()
						.setIncenBioSurgeryVialSlider3(mSliderValueAdd);
				break;
			case 4:
				ApplicationLoader.getPreferences()
						.setIncenBioSurgeryVialSlider4(mSliderValueAdd);
				break;
			}
		}

		public void validateListEditBox(Slider mSlider, Slider mSliderAdd,
				String mString, String min, String max, EditText mEditTextPer,
				TextView mProductTotal, int mPosition) {
			try {
				if (!TextUtils.isEmpty(mString.toString())) {
					if (mString.toString().length() >= 2) {
						if (Integer.parseInt(mString.toString()) >= Integer
								.parseInt(min)
								&& Integer.parseInt(mString.toString()) <= Integer
										.parseInt(max)) {
							mSlider.setValue(Integer.parseInt(mString
									.toString()));
							businessIncenLogic(mSlider, mProductTotal,
									mSliderAdd, mPosition, mSlider.getValue(),
									mEditTextPer, mProductTotal, min, false);
						} else {
							// if (mToast == null
							// || mToast.getView().getWindowVisibility() !=
							// View.VISIBLE) {
							// mToast.makeText(QuarterlyActivity.this,
							// "Please enter value between"+min+" & "+max,
							// Toast.LENGTH_SHORT).show();
							// }
							if (Integer.parseInt(mString) < Integer
									.parseInt(min)
							/* && Integer.parseInt(mString) > 20 */) {
								mSlider.setValue(Integer.parseInt(min) - 1);
								// mEditTextPer.setText(min);
								// mEditTextPer.setText("0");
								businessIncenLogic(mSlider, mProductTotal,
										mSliderAdd, mPosition,
										mSlider.getValue(), mEditTextPer,
										mProductTotal, min, true);
							} else if (Integer.parseInt(mString) > Integer
									.parseInt(max)) {
								mSlider.setValue(Integer.parseInt(max));
								// mEditTextPer.setText(max);
								businessIncenLogic(mSlider, mProductTotal,
										mSliderAdd, mPosition,
										mSlider.getValue(), mEditTextPer,
										mProductTotal, min, false);
							}/*
							 * else if (Integer.parseInt(mString) > 9 &&
							 * Integer.parseInt(mString) <= 20) {
							 * mSlider.setValue(Integer.parseInt(min) - 1);
							 * businessIncenLogic(mPosition, mSlider.getValue(),
							 * mEditTextPer, mProductTotal, min, true); }
							 */
						}
					} else if (mString.toString().length() == 1) {
						if (Integer.parseInt(mString) == 0) {
							mSlider.setValue(Integer.parseInt(min) - 1);
							businessIncenLogic(mSlider, mProductTotal,
									mSliderAdd, mPosition, mSlider.getValue(),
									mEditTextPer, mProductTotal, min, true);
						} else if (Integer.parseInt(mString) > 0
								&& Integer.parseInt(mString) <= 10) {
							mSlider.setValue(Integer.parseInt(min) - 1);
							businessIncenLogic(mSlider, mProductTotal,
									mSliderAdd, mPosition, mSlider.getValue(),
									mEditTextPer, mProductTotal, min, true);
						}
					}
				} else {
					// if (mToast == null
					// || mToast.getView().getWindowVisibility() !=
					// View.VISIBLE) {
					// mToast.makeText(IncenProductActivity.this,
					// "Please enter digits only!", Toast.LENGTH_SHORT)
					// .show();
					// }
					if (Integer.parseInt(mString) == 0) {
						mSlider.setValue(Integer.parseInt(min) - 1);
						businessIncenLogic(mSlider, mProductTotal, mSliderAdd,
								mPosition, mSlider.getValue(), mEditTextPer,
								mProductTotal, min, true);
					}
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}

		public void addValidateListEditBox(Slider mListSlider,
				TextView mListTotal, Slider mSlider, String mString,
				String max, EditText mEditTextPer, int multiple, int mPosition) {
			try {
				if (!TextUtils.isEmpty(mString.toString())) {
					if (mString.toString().length() >= 2) {
						if (Integer.parseInt(mString.toString()) >= 0
								&& Integer.parseInt(mString.toString()) <= Integer
										.parseInt(max)) {
							mSlider.setValue(Integer.parseInt(mString
									.toString()));
							businessIncenLogic(mListSlider, mListTotal,
									mSlider, mPosition, mListSlider.getValue(),
									mEditTextPer, mListTotal,
									String.valueOf(mListSlider.getMin()), false);
						} else {
							if (Integer.parseInt(mString) > Integer
									.parseInt(max)) {
								mSlider.setValue(Integer.parseInt(max));
								businessIncenLogic(mListSlider, mListTotal,
										mSlider, mPosition,
										mListSlider.getValue(), mEditTextPer,
										mListTotal,
										String.valueOf(mListSlider.getMin()),
										false);
							}
						}
					} else if (mString.toString().length() == 1) {
						if (Integer.parseInt(mString) >= 0
								&& Integer.parseInt(mString) <= 10) {
							mSlider.setValue(Integer.parseInt(mString));
							businessIncenLogic(mListSlider, mListTotal,
									mSlider, mPosition, mListSlider.getValue(),
									mEditTextPer, mListTotal,
									String.valueOf(mListSlider.getMin()), false);
						}
					}
				} else {
					if (Integer.parseInt(mString) == 0) {
						mSlider.setValue(0);
						businessIncenLogic(mListSlider, mListTotal, mSlider,
								mPosition, mListSlider.getValue(),
								mEditTextPer, mListTotal,
								String.valueOf(mListSlider.getMin()), false);
					}
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}
	}

	static class ViewHolder {
		AccordionView mListAccordionView;
		TextView mListName;
		EditText mListPer;
		Slider mListSlider;
		TextView mListSliderMin;
		TextView mListSliderMax;
		TextView mListTotal;
		TextView mListTotalRsSy;

		TextView mListNameAdd;
		EditText mListPerAdd;
		Slider mListSliderAdd;
		TextView mListSliderMinAdd;
		TextView mListSliderMaxAdd;

		LinearLayout bioSurgerySliderLayout;
		LinearLayout bioSurgeryPerLayout;
	}

	public void getProductJSON() {
		if (!BuildVars.debug) {
			if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
					.getProductJSON())) {
				parseJSON(ApplicationLoader.getPreferences().getProductJSON());
			} else {
				showAlertDialog();
			}
		} else {
			String str = Utilities.readFile("data.json");
			parseJSON(str);
		}
	}

	public void parseJSON(String str) {
		getProductList(str);
	}

	public void getProductList(String str) {
		mProduct = new ArrayList<Product>();

		try {
			JSONObject mJSONObj = new JSONObject(str);
			JSONArray mJSONArray = mJSONObj.getJSONArray("products");

			try {
				ApplicationLoader.getPreferences().setIncenTeamName(mJSONObj.getString("team"));
				if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("Bio Surgery") == 0){
					ApplicationLoader.getPreferences().setIncenBioSurgeryTeam(true);
				}else if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("Renal Team") == 0){
					ApplicationLoader.getPreferences().setIncenRenealTeam(true);
				}else if(ApplicationLoader.getPreferences().getIncenTeamName().compareToIgnoreCase("Heritage-OTC") == 0){
					ApplicationLoader.getPreferences().setIncenHeritageTeam(true);
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
			
			try {
				ApplicationLoader.getPreferences().setProductNumber(
						String.valueOf(mJSONArray.length()));
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}

			for (int i = 0; i < mJSONArray.length(); i++) {
				Product mObj = new Product();
				JSONObject mInnerJSONObj = mJSONArray.getJSONObject(i);

				mObj.setmMax(mInnerJSONObj.getString("max"));
				mObj.setmMin(mInnerJSONObj.getString("min"));
				mObj.setmName(mInnerJSONObj.getString("name"));

				String[] mSlab = new String[(Integer.parseInt(mObj.getmMax()) - Integer
						.parseInt(mObj.getmMin())) + 1];
				for (int j = 0; j < (Integer.parseInt(mObj.getmMax())
						- (Integer.parseInt(mObj.getmMin())) + 1); j++) {
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

	private void showAlertDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				IncenProductActivity.this);

		// set title
		alertDialogBuilder.setTitle("Sanofi Mobcast");

		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Will try again to get your respective product list")
				.setCancelable(false)
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// ApplicationLoader.getPreferences().setIncenFirstTime(true);
								// finish();
								if (Utilities.isInternetConnected()) {
									new AsyncDataFromApi(true).execute();
								} else {
									Toast.makeText(
											IncenProductActivity.this,
											"Please check your internet connection",
											Toast.LENGTH_SHORT).show();
									showAlertDialog();
								}
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public class AsyncDataFromApi extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isProductData = false;
		private boolean forceUser = false;
		private String strJSONData;

		public AsyncDataFromApi(boolean forceUser) {
			this.forceUser = forceUser;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (forceUser) {
				mProgress = new ProgressDialog(IncenProductActivity.this);
				mProgress.setMessage("Please wait");
				mProgress.setCanceledOnTouchOutside(false);
				mProgress.setCancelable(false);
				mProgress.show();
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SharedPreferences pref;
			pref = getSharedPreferences("MobCastPref", 0);
			String temp1 = pref.getString("name", "tushar@mobcast.in");

			HashMap<String, String> formValue = new HashMap<String, String>();
			formValue.put("device", "android");
			if (!BuildVars.debug) {
				formValue.put(com.mobcast.util.Constants.user_id, temp1);
			} else {
				formValue.put(com.mobcast.util.Constants.user_id,
						"tushar@mobcast.in");
			}

			String str = RestClient
					.postData(Constants.INCEN_PRODUCT, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray mJSONArray = mJSONObj.getJSONArray("products");
				if (mJSONArray.length() > 0) {
					isProductData = true;
					strJSONData = str;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (forceUser) {
				if (mProgress != null)
					mProgress.dismiss();
			}

			if (isProductData) {
				ApplicationLoader.getPreferences().setProductJSON(strJSONData);
				ApplicationLoader.getPreferences().setIncenFirstTime(true);
			}
			getProductJSON();
		}
	}

	private void setSecurity() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
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
