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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.mobcast.calc.IncenProductActivity.AsyncDataFromApi;
import com.mobcast.util.BuildVars;
import com.mobcast.util.Constants;
import com.mobcast.util.RestClient;
import com.mobcast.util.Utilities;
import com.mobcast.view.AccordionView;
import com.sanofi.in.mobcast.ApplicationLoader;
import com.sanofi.in.mobcast.R;

public class IncenKPIActivity extends FragmentActivity {

	private AccordionView mAccordionTotal;

	private TextView mTotalRsSy;
	private Typeface mTypeFace;
	private TextView mTotalTv;

	private ListProductAdapter mAdapter;
	private ArrayList<Product> mProduct;
	private ListView mListView;

	private String[] mArrMonthName;
	private String[] mArrTempMonthName;
	private int whichQuarter;

	private int[] mCheckBoxSelected;
	
	private String [] mCoverageArr;
	private String [] mCPAArr;
	private String mCoverageAvgArr;
	private String mCPAAvgArr;
	
	private String mKPITotalArr;
	
	public float mWeightAgeCoverageTotal = 0.4f;
	public float mWeightAgeCPATotal = 0.6f;
	public float mWeightAgeCPA;

	private static final String TAG = IncenKPIActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incen_kpi);
		setSecurity();
		initUi();
		setAccordionHeader();
		getProductJSON();
		setRupeeFont();
		getIntentData();
		setListView();
	}

	private void initUi() {
		mListView = (ListView) findViewById(R.id.incen_kpi_listView);

		mAccordionTotal = (AccordionView) findViewById(R.id.incen_kpi_total_acc_view);

		mTotalTv = (TextView)findViewById(R.id.incen_kpi_total_incen);
		
		mTotalRsSy = (TextView) findViewById(R.id.incen_kpi_total_rs_sy);
	}

	private void setAccordionHeader() {
		mAccordionTotal.toggleSection(0);
		mAccordionTotal.setSectionHeaders("Total KPI Incentive");

	}

	private void getIntentData() {
		whichQuarter = (Integer.parseInt(getIntent().getStringExtra(
				IncenDashBoardActivity.INTENT_QUARTER)));
		mArrMonthName = new String[13];
		mArrTempMonthName = new String[12];
		mArrTempMonthName = getResources().getStringArray(R.array.month);
		for (int i = 0; i < mArrTempMonthName.length; i++) {
			mArrMonthName[i + 1] = mArrTempMonthName[i];
		}
	}

	private void setRupeeFont() {
		mTypeFace = Utilities.getFontStyleRupee();

		mTotalRsSy.setTypeface(mTypeFace);

		mTotalRsSy.setText("`");
	}

	/*
	 * DYNAMIC LISTVIEW
	 */

	/*
	 * DYNAMIC PRODUCTS
	 */
	private void setListView() {
		if (mProduct!=null && mProduct.size() > 0) {
			mAdapter = new ListProductAdapter(IncenKPIActivity.this, mProduct);
			mListView.setAdapter(mAdapter);
			// Utilities.setListViewHeightBasedOnChildren(mListView);
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
			mWeightAgeCPA = mWeightAgeCPATotal / mListProduct.size();
			mCheckBoxSelected = new int[mListProduct.size() + 3];
			for (int i = 0; i < mListProduct.size() + 3; i++) {
				mCheckBoxSelected[i] = 0;
			}
		}

		@Override
		public int getCount() {
			return mListProduct.size() + 3;
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
				v = li.inflate(R.layout.item_incen_kpi_coverage, null);
				holder = new ViewHolder();
				holder.mListLabel = (TextView) v
						.findViewById(R.id.item_incen_kpi_coverage_cpa_tv);
				holder.mListLabelLayout = (LinearLayout) v
						.findViewById(R.id.item_incen_kpi_coverage_cpa_layout);
				holder.mListAccordionView = (AccordionView) v
						.findViewById(R.id.incen_kpi_acc_view_cpa);
				holder.mListCheckBox1 = (CheckBox) v
						.findViewById(R.id.incen_kpi_cpa_switchView1);
				holder.mListCheckBox2 = (CheckBox) v
						.findViewById(R.id.incen_kpi_cpa_switchView2);
				holder.mListCheckBox3 = (CheckBox) v
						.findViewById(R.id.incen_kpi_cpa_switchView3);
				holder.mListCheckBox4 = (CheckBox) v
						.findViewById(R.id.incen_kpi_cpa_switchView4);
				holder.mListCheckBox5 = (CheckBox) v
						.findViewById(R.id.incen_kpi_cpa_switchView5);
				v.setTag(holder);
			} else {
				v = convertView;
				holder = (ViewHolder) v.getTag();
			}

			switch (position) {
			case 0:
				holder.mListLabelLayout.setVisibility(View.VISIBLE);
				holder.mListLabel.setText("Coverage");
				holder.mListAccordionView.toggleSection(0);
				holder.mListAccordionView
						.setSectionHeaders(mArrMonthName[Utilities
								.getCurrentMonth(whichQuarter)]);

				break;
			case 1:
				holder.mListLabelLayout.setVisibility(View.GONE);
				holder.mListAccordionView
						.setSectionHeaders(mArrMonthName[Utilities
								.getCurrentMonth(whichQuarter) + 1]);
				break;
			case 2:
				holder.mListLabelLayout.setVisibility(View.GONE);
				holder.mListAccordionView
						.setSectionHeaders(mArrMonthName[Utilities
								.getCurrentMonth(whichQuarter) + 2]);
				break;
			case 3:
				holder.mListLabelLayout.setVisibility(View.VISIBLE);
				holder.mListLabel.setText("CPA "
						+ Utilities.getCPATitle(mProduct.size()));
				holder.mListAccordionView.setSectionHeaders(mProduct.get(0)
						.getmName());
				break;
			default:
				holder.mListLabelLayout.setVisibility(View.GONE);
				holder.mListAccordionView.setSectionHeaders(mProduct.get(
						position - 3).getmName());
				break;
			}

			setListCheckBoxGroup(holder.mListCheckBox1, holder.mListCheckBox2,
					holder.mListCheckBox3, holder.mListCheckBox4,
					holder.mListCheckBox5, position);

//			isCheckedAndToggleAccordion(holder.mListAccordionView,
//					holder.mListCheckBox1, holder.mListCheckBox2,
//					holder.mListCheckBox3, holder.mListCheckBox4,
//					holder.mListCheckBox5, position);
			
			restoreQuarterType(whichQuarter, position, holder.mListCheckBox1,
					holder.mListCheckBox2, holder.mListCheckBox3,
					holder.mListCheckBox4, holder.mListCheckBox5);
			
			return v;
		}
		
		public void businessIncenLogic(){
			
			float floatCoverageAvg = 0;
			for(int i = 0 ;i < 3;i++){
				floatCoverageAvg+= Integer.parseInt(mCoverageArr[i]);
			}
			floatCoverageAvg /=3;
			mCoverageAvgArr = String.valueOf(floatCoverageAvg * mWeightAgeCoverageTotal);
			
			float floatCPAAvg = 0;
			for(int i = 0 ;i < mListProduct.size();i++){
				Log.i("CPA", mCPAArr[i]);
				floatCPAAvg+= Integer.parseInt(mCPAArr[i]) * (mWeightAgeCPATotal / mListProduct.size());
			}
			mCPAAvgArr = String.valueOf(floatCPAAvg);
			if(Float.parseFloat(mCPAAvgArr) + Float.parseFloat(mCoverageAvgArr) >= 4.5){
				mKPITotalArr = "5000";
			}else if(Float.parseFloat(mCPAAvgArr) + Float.parseFloat(mCoverageAvgArr) >= 4 && Float.parseFloat(mCPAAvgArr) + Float.parseFloat(mCoverageAvgArr) < 4.5){
				mKPITotalArr = "3750";
			}else{
				mKPITotalArr = "0";
			}
			
			mTotalTv.setText(mKPITotalArr);
			saveProductQuarter(whichQuarter);
		}
		
		public void saveProductQuarter(int quarterType) {
			switch (quarterType) {
			case 1:
				ApplicationLoader.getPreferences().setKPI1(mCPAArr);
				ApplicationLoader.getPreferences().setKPIValue1(mCPAAvgArr);
				
				ApplicationLoader.getPreferences().setKPIQ1(mCoverageArr);
				ApplicationLoader.getPreferences().setKPIQValue1(mCoverageAvgArr);
				
				ApplicationLoader.getPreferences().setKPITotalValue1(mKPITotalArr);
				break;
			case 2:
				ApplicationLoader.getPreferences().setKPI2(mCPAArr);
				ApplicationLoader.getPreferences().setKPIValue2(mCPAAvgArr);
				
				ApplicationLoader.getPreferences().setKPIQ2(mCoverageArr);
				ApplicationLoader.getPreferences().setKPIQValue2(mCoverageAvgArr);
				
				ApplicationLoader.getPreferences().setKPITotalValue2(mKPITotalArr);
				break;
			case 3:
				ApplicationLoader.getPreferences().setKPI3(mCPAArr);
				ApplicationLoader.getPreferences().setKPIValue3(mCPAAvgArr);
				
				ApplicationLoader.getPreferences().setKPIQ3(mCoverageArr);
				ApplicationLoader.getPreferences().setKPIQValue3(mCoverageAvgArr);
				
				ApplicationLoader.getPreferences().setKPITotalValue3(mKPITotalArr);
				break;
			case 4:
				ApplicationLoader.getPreferences().setKPI4(mCPAArr);
				ApplicationLoader.getPreferences().setKPIValue4(mCPAAvgArr);
				
				ApplicationLoader.getPreferences().setKPIQ4(mCoverageArr);
				ApplicationLoader.getPreferences().setKPIQValue4(mCoverageAvgArr);
				
				ApplicationLoader.getPreferences().setKPITotalValue4(mKPITotalArr);
				break;
			}

		}
		
		public void restoreQuarterType(int quarterType, int position,
				CheckBox mCheck1, CheckBox mCheck2, CheckBox mCheck3,
				CheckBox mCheck4, CheckBox mCheck5) {
			mCoverageArr = new String[3];
			mCPAArr = new String[mProduct.size()];
			
			switch (quarterType) {
			case 1:
				mCoverageArr = ApplicationLoader.getPreferences().getKPIQ1();
				mCoverageAvgArr = ApplicationLoader.getPreferences().getKPIQValue1();
				
				mCPAArr = ApplicationLoader.getPreferences().getKPI1(mProduct.size());
				mCPAAvgArr = ApplicationLoader.getPreferences().getKPIValue1();
				
				mKPITotalArr = ApplicationLoader.getPreferences().getKPITotalValue1();
				
				break;
			case 2:
				mCoverageArr = ApplicationLoader.getPreferences().getKPIQ2();
				mCoverageAvgArr = ApplicationLoader.getPreferences().getKPIQValue2();
				
				mCPAArr = ApplicationLoader.getPreferences().getKPI2(mProduct.size());
				mCPAAvgArr = ApplicationLoader.getPreferences().getKPIValue2();
				
				mKPITotalArr = ApplicationLoader.getPreferences().getKPITotalValue2();
				
				break;
			case 3:
				mCoverageArr = ApplicationLoader.getPreferences().getKPIQ3();
				mCoverageAvgArr = ApplicationLoader.getPreferences().getKPIQValue3();
				
				mCPAArr = ApplicationLoader.getPreferences().getKPI3(mProduct.size());
				mCPAAvgArr = ApplicationLoader.getPreferences().getKPIValue3();
				
				mKPITotalArr = ApplicationLoader.getPreferences().getKPITotalValue3();
				
				break;
			case 4:
				mCoverageArr = ApplicationLoader.getPreferences().getKPIQ4();
				mCoverageAvgArr = ApplicationLoader.getPreferences().getKPIQValue4();
				
				mCPAArr = ApplicationLoader.getPreferences().getKPI4(mProduct.size());
				mCPAAvgArr = ApplicationLoader.getPreferences().getKPIValue4();
				
				mKPITotalArr = ApplicationLoader.getPreferences().getKPITotalValue4();
				
				break;
			}
			checkValuesFromPreferencesNullOrNot();
			setValuesFromPreferences(position, mCheck1, mCheck2, mCheck3,
					mCheck4, mCheck5);
		}
		
		public void setValuesFromPreferences(int mPosition, CheckBox mCheck1,
				CheckBox mCheck2, CheckBox mCheck3, CheckBox mCheck4,
				CheckBox mCheck5) {
			switch (mPosition) {
			case 0:
				switch(Integer.parseInt(mCoverageArr[0])){
				case 5:
					mCheck1.setChecked(true);
					break;
				case 4:
					mCheck2.setChecked(true);
					break;
				case 3:
					mCheck3.setChecked(true);
					break;
				case 2:
					mCheck4.setChecked(true);
					break;
				case 1:
					mCheck5.setChecked(true);
					break;
				}
				break;
			case 1:
				switch(Integer.parseInt(mCoverageArr[1])){
				case 5:
					mCheck1.setChecked(true);
					break;
				case 4:
					mCheck2.setChecked(true);
					break;
				case 3:
					mCheck3.setChecked(true);
					break;
				case 2:
					mCheck4.setChecked(true);
					break;
				case 1:
					mCheck5.setChecked(true);
					break;
				}
				break;
			case 2:
				switch(Integer.parseInt(mCoverageArr[2])){
				case 5:
					mCheck1.setChecked(true);
					break;
				case 4:
					mCheck2.setChecked(true);
					break;
				case 3:
					mCheck3.setChecked(true);
					break;
				case 2:
					mCheck4.setChecked(true);
					break;
				case 1:
					mCheck5.setChecked(true);
					break;
				}
				break;
			default:
				switch(Integer.parseInt(mCPAArr[mPosition - 3])){
				case 5:
					mCheck1.setChecked(true);
					break;
				case 4:
					mCheck2.setChecked(true);
					break;
				case 3:
					mCheck3.setChecked(true);
					break;
				case 2:
					mCheck4.setChecked(true);
					break;
				case 1:
					mCheck5.setChecked(true);
					break;
				}
				break;
			}
			mTotalTv.setText(mKPITotalArr);
		}

		public void checkValuesFromPreferencesNullOrNot(){
			for(int i = 0; i < mProduct.size() ;i++){
				if(mCPAArr[i]==null){
					mCPAArr[i] = "1";
				}
			}
			
			for(int i = 0; i < 3 ;i++){
				if(mCoverageArr[i]==null){
					mCoverageArr[i] = "1";
				}
			}
			
			if(mKPITotalArr == null){
				mKPITotalArr = "0";
			}
		}

		public void isCheckedAndToggleAccordion(AccordionView mAccView,
				CheckBox mCheck1, CheckBox mCheck2, CheckBox mCheck3,
				CheckBox mCheck4, CheckBox mCheck5, int position) {
			try {
				if (mCheckBoxSelected[position] != 0) {
					switch (mCheckBoxSelected[position]) {
					case 1:
						mCheck1.setChecked(true);
						break;
					case 2:
						mCheck2.setChecked(true);
						break;
					case 3:
						mCheck3.setChecked(true);
						break;
					case 4:
						mCheck4.setChecked(true);
						break;
					case 5:
						mCheck5.setChecked(true);
						break;
					}
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}

		public void setListCheckBoxGroup(final CheckBox mCheckBox1,
				final CheckBox mCheckBox2, final CheckBox mCheckBox3,
				final CheckBox mCheckBox4, final CheckBox mCheckBox5,
				final int position) {

			mCheckBox1
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton arg0,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								mCheckBox2.setChecked(false);
								mCheckBox3.setChecked(false);
								mCheckBox4.setChecked(false);
								mCheckBox5.setChecked(false);
								mCheckBoxSelected[position] = 1;
								switch (position) {
								case 0:
									mCoverageArr[0] = "5";
									break;
								case 1:
									mCoverageArr[1] = "5";
									break;
								case 2:
									mCoverageArr[2] = "5";
									break;
								default:
									mCPAArr[position -3] = "5";
									break;
								}
								businessIncenLogic();
							}else{
								mCheckBoxSelected[position] = 1;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position -3] = "1";
									break;
								}
								businessIncenLogic();
							}
							
						}
					});

			mCheckBox2
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								mCheckBox1.setChecked(false);
								mCheckBox3.setChecked(false);
								mCheckBox4.setChecked(false);
								mCheckBox5.setChecked(false);
								mCheckBoxSelected[position] = 2;
								switch (position) {
								case 0:
									mCoverageArr[0] = "4";
									break;
								case 1:
									mCoverageArr[1] = "4";
									break;
								case 2:
									mCoverageArr[2] = "4";
									break;
								default:
									mCPAArr[position - 3] = "4";
									break;
								}
								businessIncenLogic();
							}else{
								mCheckBoxSelected[position] = 2;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position - 3] = "1";
									break;
								}
								businessIncenLogic();
							}
						}
					});

			mCheckBox3
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								mCheckBox1.setChecked(false);
								mCheckBox2.setChecked(false);
								mCheckBox4.setChecked(false);
								mCheckBox5.setChecked(false);
								mCheckBoxSelected[position] = 3;
								switch (position) {
								case 0:
									mCoverageArr[0] = "3";
									break;
								case 1:
									mCoverageArr[1] = "3";
									break;
								case 2:
									mCoverageArr[2] = "3";
									break;
								default:
									mCPAArr[position -3] = "3";
									break;
								}
								businessIncenLogic();
							}else{
								mCheckBoxSelected[position] = 3;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position -3] = "1";
									break;
								}
								businessIncenLogic();
							}
						}
					});

			mCheckBox4
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								mCheckBox1.setChecked(false);
								mCheckBox2.setChecked(false);
								mCheckBox3.setChecked(false);
								mCheckBox5.setChecked(false);
								mCheckBoxSelected[position] = 4;
								switch (position) {
								case 0:
									mCoverageArr[0] = "2";
									break;
								case 1:
									mCoverageArr[1] = "2";
									break;
								case 2:
									mCoverageArr[2] = "2";
									break;
								default:
									mCPAArr[position - 3] = "2";
									break;
								}
								businessIncenLogic();
							}else{
								mCheckBoxSelected[position] = 4;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position - 3] = "1";
									break;
								}
								businessIncenLogic();
							}
						}
					});

			mCheckBox5
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isChecked) {
								mCheckBox1.setChecked(false);
								mCheckBox2.setChecked(false);
								mCheckBox3.setChecked(false);
								mCheckBox4.setChecked(false);
								mCheckBoxSelected[position] = 5;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position - 3] = "1";
									break;
								}
								businessIncenLogic();
							}else{
								mCheckBoxSelected[position] = 5;
								switch (position) {
								case 0:
									mCoverageArr[0] = "1";
									break;
								case 1:
									mCoverageArr[1] = "1";
									break;
								case 2:
									mCoverageArr[2] = "1";
									break;
								default:
									mCPAArr[position - 3] = "1";
									break;
								}
								businessIncenLogic();
							}
						}
					});
		}
	}

	static class ViewHolder {
		AccordionView mListAccordionView;
		TextView mListName;
		TextView mListLabel;
		LinearLayout mListLabelLayout;
		CheckBox mListCheckBox1;
		CheckBox mListCheckBox2;
		CheckBox mListCheckBox3;
		CheckBox mListCheckBox4;
		CheckBox mListCheckBox5;
	}

	public void getProductJSON() {
		if(!BuildVars.debug){
			if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
					.getProductJSON())) {
				parseJSON(ApplicationLoader.getPreferences().getProductJSON());
			} else {
				showAlertDialog();
			}
		}else{
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

			for (int i = 0; i < mJSONArray.length(); i++) {
				Product mObj = new Product();
				JSONObject mInnerJSONObj = mJSONArray.getJSONObject(i);

				mObj.setmMax(mInnerJSONObj.getString("max"));
				mObj.setmMin(mInnerJSONObj.getString("min"));
				mObj.setmName(mInnerJSONObj.getString("name"));

				String[] mSlab = new String[(Integer.parseInt(mObj.getmMax()) - Integer
						.parseInt(mObj.getmMin())) + 1];
				for (int j = 0; j < (Integer.parseInt(mObj.getmMax())
						- Integer.parseInt(mObj.getmMin()) + 1); j++) {
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
				IncenKPIActivity.this);

		// set title
		alertDialogBuilder.setTitle("Sanofi Mobcast");

		// set dialog message
		alertDialogBuilder
				.setMessage("Will try again to get your respective product list")
				.setCancelable(false)
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
//								ApplicationLoader.getPreferences().setIncenFirstTime(true);
//								finish();
								if (Utilities.isInternetConnected()) {
									new AsyncDataFromApi(true).execute();
								} else {
									Toast.makeText(
											IncenKPIActivity.this,
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
	
	public class AsyncDataFromApi extends AsyncTask<Void, Void, Void>{
		private ProgressDialog mProgress;
		private boolean isProductData = false;
		private boolean forceUser = false;
		private String strJSONData;
		
		public AsyncDataFromApi(boolean forceUser){
			this.forceUser = forceUser;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if(forceUser){
				mProgress = new ProgressDialog(IncenKPIActivity.this);
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
			if(!BuildVars.debug){
				formValue.put(com.mobcast.util.Constants.user_id, temp1);
			}else{
				formValue.put(com.mobcast.util.Constants.user_id, "tushar@mobcast.in");
			}

			String str = RestClient
					.postData(Constants.INCEN_PRODUCT, formValue);
			try {
				JSONObject mJSONObj = new JSONObject(str);
				JSONArray  mJSONArray = mJSONObj.getJSONArray("products");
				if(mJSONArray.length() > 0){
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
			if(forceUser){
				if(mProgress!=null)
					mProgress.dismiss();
			}
			
			if(isProductData){
				ApplicationLoader.getPreferences().setProductJSON(strJSONData);
				ApplicationLoader.getPreferences().setIncenFirstTime(true);
			}
			getProductJSON();
		}
	}
	
	private void setSecurity(){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if(!BuildVars.debug){
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
