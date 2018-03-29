package com.example.exmword;

import com.artifex.mupdf.MuPDFCore;

import android.app.Application;
import android.graphics.PointF;
import android.util.SparseArray;

public class MainApplication extends Application {
	private static MainApplication mApp;
	public MuPDFCore pdf_core;
	public SparseArray<PointF> page_sizes = new SparseArray<PointF>();
	
	public static MainApplication getInstance() {
		return mApp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
	}
	
}
