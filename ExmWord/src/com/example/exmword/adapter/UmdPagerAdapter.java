package com.example.exmword.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.exmword.fragment.TextFragment;

public class UmdPagerAdapter extends FragmentStatePagerAdapter {
	private List<String> mContentArray;
	
	public UmdPagerAdapter(FragmentManager fm, List<String> contentArray) {
		super(fm);
		mContentArray = contentArray;
	}

	public int getCount() {
		return mContentArray.size();
	}

	public Fragment getItem(int position) {
		return TextFragment.newInstance(mContentArray.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "第"+(position+1)+"页";
	}

}
