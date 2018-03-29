package com.example.exmtabfragment.adapter;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainTabAdapter extends FragmentStatePagerAdapter {
	private ArrayList<Fragment> mTabList = new ArrayList<Fragment>();
	private Bundle mBundle = new Bundle();
	
	public MainTabAdapter(FragmentManager fm, ArrayList<Fragment> tabList, Bundle bundle) {
		super(fm);
		mTabList = tabList;
		mBundle = bundle;
	}

	public int getCount() {
		return mTabList.size();
	}

	public Fragment getItem(int position) {
		Fragment fragment = mTabList.get(position);
		fragment.setArguments(mBundle);
		return fragment;
	}

}
