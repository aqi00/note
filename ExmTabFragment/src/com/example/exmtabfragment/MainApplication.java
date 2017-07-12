package com.example.exmtabfragment;

import android.app.Application;

public class MainApplication extends Application {
	private static MainApplication mApp;
	public static String TabCreateName = "";
	public static String TabPagerName = "";
	
	public static MainApplication getInstance() {
		return mApp;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (mApp == null) {
			mApp = this;
		}
	}
	
}
