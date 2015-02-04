package com.mobcast.calc;

import android.R.color;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.mobcast.util.BuildVars;
import com.sanofi.in.mobcast.R;

public class DIncenCalSliderActivity extends FragmentActivity {

	private TextView mTabMonthly;
	private TextView mTabQuarterly;
	private TextView mTabAnnually;
	private ViewPager mPager;

	private ViewPager.SimpleOnPageChangeListener mPagerListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dactivity_incentive_calculator);
		setSecurity();
		initUi();
		setViewPager();
		setUiListener();
	}

	private void setSecurity() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (!BuildVars.debug) {
				getWindow().setFlags(LayoutParams.FLAG_SECURE,
						LayoutParams.FLAG_SECURE);
			}
		}
	}

	private void initUi() {
		mTabMonthly = (TextView) findViewById(R.id.incen_tab_monthly);
		mTabQuarterly = (TextView) findViewById(R.id.incen_tab_quarterly);
		mTabAnnually = (TextView) findViewById(R.id.incen_tab_annually);

		mPager = (ViewPager) findViewById(R.id.pager);
	}

	private void setViewPager() {
		DIncentivePagerAdapter viewpageradapter = new DIncentivePagerAdapter(
				getSupportFragmentManager());
		mPager.setOffscreenPageLimit(3);
		mPager.setAdapter(viewpageradapter);
		setViewPagerListener();
		mPager.setOnPageChangeListener(mPagerListener);
		mPager.setCurrentItem(0);
	}

	private void setViewPagerListener() {
		mPagerListener = new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				super.onPageSelected(position);
				changeTabAppearnce(position);
			}
		};
	}

	private void setUiListener() {
		mTabMonthly.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(0);
			}
		});

		mTabQuarterly.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(1);
			}
		});

		mTabAnnually.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mPager.setCurrentItem(2);
			}
		});

	}

	private void changeTabAppearnce(int position) {
		switch (position) {
		case 0:
			mTabMonthly.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.incen_tab_left_shape));
			mTabQuarterly.setBackgroundColor(getResources().getColor(
					android.R.color.white));
			mTabAnnually.setBackgroundColor(getResources().getColor(
					android.R.color.white));

			mTabMonthly.setTextColor(Color.parseColor("#ffffff"));
			mTabQuarterly.setTextColor(Color.parseColor("#006999"));
			mTabAnnually.setTextColor(Color.parseColor("#006999"));
			break;
		case 1:
			mTabQuarterly.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.incen_tab_middle_shape));
			mTabMonthly.setBackgroundColor(getResources().getColor(
					android.R.color.white));
			mTabAnnually.setBackgroundColor(getResources().getColor(
					android.R.color.white));

			mTabMonthly.setTextColor(Color.parseColor("#006999"));
			mTabQuarterly.setTextColor(Color.parseColor("#ffffff"));
			mTabAnnually.setTextColor(Color.parseColor("#006999"));
			break;
		case 2:
			mTabAnnually.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.incen_tab_right_shape));
			mTabMonthly.setBackgroundColor(getResources().getColor(
					android.R.color.white));
			mTabQuarterly.setBackgroundColor(getResources().getColor(
					android.R.color.white));

			mTabMonthly.setTextColor(Color.parseColor("#006999"));
			mTabQuarterly.setTextColor(Color.parseColor("#006999"));
			mTabAnnually.setTextColor(Color.parseColor("#ffffff"));
			break;

		}
	}

}
