package com.example.exmword.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.artifex.mupdf.MuPDFCore;
import com.example.exmword.MainApplication;
import com.example.exmword.fragment.PdfFragment;

public class PdfPagerAdapter extends FragmentStatePagerAdapter {
	
	public PdfPagerAdapter(FragmentManager fm, MuPDFCore core) {
		super(fm);
		MainApplication.getInstance().pdf_core = core;
	}

	public int getCount() {
		return MainApplication.getInstance().pdf_core.countPages();
	}

	public Fragment getItem(int position) {
		return PdfFragment.newInstance(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "第"+(position+1)+"页";
	}

}
