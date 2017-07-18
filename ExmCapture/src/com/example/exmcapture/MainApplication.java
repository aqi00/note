package com.example.exmcapture;

import android.app.Application;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

public class MainApplication extends Application {
	private static MainApplication mApp;
    private Intent mResultIntent = null;
    private int mResultCode = 0;
    private MediaProjectionManager mMpMgr;

	public static MainApplication getInstance() {
		return mApp;
	}

    public Intent getResultIntent() {
        return mResultIntent;
    }

    public void setResultIntent(Intent mResultIntent) {
        this.mResultIntent = mResultIntent;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public void setResultCode(int mResultCode) {
        this.mResultCode = mResultCode;
    }

    public MediaProjectionManager getMpMgr() {
        return mMpMgr;
    }

    public void setMpMgr(MediaProjectionManager mMpMgr) {
        this.mMpMgr = mMpMgr;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }
}
