package com.mobcast.calc;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DIncentivePagerAdapter extends FragmentPagerAdapter {
	public static final int PAGE_COUNT = 3;

	public DIncentivePagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int item) {
		switch (item) {
		case 0:
			DFragmentInceMonthly fragmentFeedAll = new DFragmentInceMonthly();
			return fragmentFeedAll;
		case 1:
			DFragmentInceQuaterly fragmentFeedAll1 = new DFragmentInceQuaterly();
			return fragmentFeedAll1;
		case 2:
			DFragmentInceAnnually fragmentFeedAll3 = new DFragmentInceAnnually();
			return fragmentFeedAll3;
			/*
			 * case 2: FragmentFeedRecent fragmentFeedAll2 = new
			 * FragmentFeedRecent(); return fragmentFeedAll2; case 3:
			 * FragmentFeedPopular fragmentFeedAll3 = new FragmentFeedPopular();
			 * return fragmentFeedAll3;
			 */
		}
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return PAGE_COUNT;
	}

}
