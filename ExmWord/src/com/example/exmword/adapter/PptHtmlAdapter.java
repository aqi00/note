package com.example.exmword.adapter;

import java.util.ArrayList;

import com.example.exmword.fragment.HtmlFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PptHtmlAdapter extends FragmentStatePagerAdapter {
	private ArrayList<String> mHtmlArray = new ArrayList<String>();
	
	public PptHtmlAdapter(FragmentManager fm, ArrayList<String> htmlArray) {
		super(fm);
		mHtmlArray = htmlArray;
	}

	public int getCount() {
		return mHtmlArray.size();
	}

	public Fragment getItem(int position) {
		return HtmlFragment.newInstance(mHtmlArray.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "第"+(position+1)+"页";
	}

}
