package com.example.exmcapture.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.exmcapture.MainApplication;
import com.example.exmcapture.R;
import com.example.exmcapture.util.DisplayUtil;
import com.example.exmcapture.util.FileUtil;
import com.example.exmcapture.util.Utils;
import com.example.exmcapture.widget.FloatView;
import com.example.exmcapture.widget.FloatView.FloatClickListener;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CaptureService extends Service implements FloatClickListener {
	private static final String TAG = "CaptureService";
	private MediaProjectionManager mMpMgr;
	private MediaProjection mMP;
	private ImageReader mImageReader;
	private String mImagePath, mImageName;
	private int mScreenWidth, mScreenHeight, mScreenDensity;
	private VirtualDisplay mVirtualDisplay;
	private FloatView mFloatView;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mImagePath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/ScreenShots/";
		mMpMgr = MainApplication.getInstance().getMpMgr();
		mScreenWidth = DisplayUtil.getSreenWidth(this);
		mScreenHeight = DisplayUtil.getSreenHeight(this);
		mScreenDensity = DisplayUtil.getSreenDensityDpi(this);
		mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 2);
		if (mFloatView == null) {
			mFloatView = new FloatView(MainApplication.getInstance());
			mFloatView.setLayout(R.layout.float_capture);
		}
		mFloatView.setOnFloatListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (mFloatView != null && mFloatView.isShow() == false) {
			mFloatView.show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onFloatClick(View v) {
		Toast.makeText(this, "准备截图", Toast.LENGTH_SHORT).show();
		mHandler.postDelayed(mStartVirtual, 100); // 准备屏幕
		mHandler.postDelayed(mCapture, 500); // 进行截图
		mHandler.postDelayed(mStopVirtual, 1000); // 释放屏幕
	}
	
	private Handler mHandler = new Handler();
	private Runnable mStartVirtual = new Runnable() {
		@Override
		public void run() {
			mFloatView.mContentView.setVisibility(View.INVISIBLE);
			if (mMP == null) {
				mMP = mMpMgr.getMediaProjection(MainApplication.getInstance().getResultCode(), 
						MainApplication.getInstance().getResultIntent());
			}
			mVirtualDisplay = mMP.createVirtualDisplay("capture_screen", mScreenWidth, mScreenHeight,
					mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
					mImageReader.getSurface(), null, null);
		}
	};

	private Runnable mCapture = new Runnable() {
		@Override
		public void run() {
			mImageName = Utils.getNowDateTime() + ".png";
			Log.d(TAG, "mImageName=" + mImageName);
			Bitmap bitmap = FileUtil.getBitmap(mImageReader.acquireLatestImage());
			if (bitmap != null) {
				FileUtil.createFile(mImagePath, mImageName);
				FileUtil.saveBitmap(mImagePath+mImageName, bitmap, "PNG", 100);
				Toast.makeText(CaptureService.this, "截图成功："+mImagePath+mImageName, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(CaptureService.this, "截图失败：未截到屏幕图片", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private Runnable mStopVirtual = new Runnable() {
		@Override
		public void run() {
			mFloatView.mContentView.setVisibility(View.VISIBLE);
			if (mVirtualDisplay != null) {
				mVirtualDisplay.release();
				mVirtualDisplay = null;
			}
		}
	};

	@Override
	public void onDestroy() {
		if (mFloatView != null && mFloatView.isShow() == true) {
			mFloatView.close();
		}
		if (mMP != null) {
			mMP.stop();
		}
		super.onDestroy();
	}

}
