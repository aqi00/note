package com.example.exmtablayout.adapter;

import java.util.ArrayList;

import com.example.exmtablayout.fragment.GoodsDetailFragment;
import com.example.exmtablayout.fragment.GoodsCoverFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class GoodsPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<String> mTitleArray;

	public GoodsPagerAdapter(FragmentManager fm, ArrayList<String> titleArray) {
		super(fm);
		mTitleArray = titleArray;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			return new GoodsCoverFragment();
		} else if (position == 1) {
			return new GoodsDetailFragment();
		}
		return new GoodsCoverFragment();
	}

	@Override
	public int getCount() {
		return mTitleArray.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mTitleArray.get(position);
	}
}
