package com.example.exmword.adapter;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.exmword.fragment.ImageFragment;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PdfSelfAdapter extends FragmentStatePagerAdapter {
	private ArrayList<String> mImgArray = new ArrayList<String>();
	
	public PdfSelfAdapter(FragmentManager fm, ArrayList<String> imgArray) {
		super(fm);
		mImgArray = imgArray;
	}

	public int getCount() {
		return mImgArray.size();
	}

	public Fragment getItem(int position) {
		return ImageFragment.newInstance(mImgArray.get(position));
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "第"+(position+1)+"页";
	}

}
