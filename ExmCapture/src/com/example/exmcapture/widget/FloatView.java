package com.example.exmcapture.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class FloatView extends View {
	private final static String TAG = "FloatView";
	private Context mContext;
	private WindowManager wm;
	private static WindowManager.LayoutParams wmParams;
	public View mContentView;
	private float mScreenX, mScreenY;
	private float mLastX, mLastY;
	private float mDownX, mDownY;
	private boolean bShow = false;

	public FloatView(Context context) {
		super(context);
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
		}
		mContext = context;
	}
	
	public void setLayout(int layout_id) {
		mContentView = LayoutInflater.from(mContext).inflate(layout_id, null);
		mContentView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				mScreenX = event.getRawX();
				mScreenY = event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mDownX = mScreenX;
					mDownY = mScreenY;
					break;
				case MotionEvent.ACTION_MOVE:
					updateViewPosition();
					break;
				case MotionEvent.ACTION_UP:
					updateViewPosition();
					//响应点击事件
					if (Math.abs(mScreenX-mDownX)<3 && Math.abs(mScreenY-mDownY)<3) {
						if (mListener != null) {
							mListener.onFloatClick(v);
						}
					}
					break;
				}
				mLastX = mScreenX;
				mLastY = mScreenY;
				return true;
			}
		});
	}

	private void updateViewPosition() {
		//此处不能直接转为整型，因为小数部分会被截掉，重复多次后就会造成偏移越来越大
		wmParams.x = Math.round(wmParams.x + mScreenX - mLastX);
		wmParams.y = Math.round(wmParams.y + mScreenY - mLastY);
		wm.updateViewLayout(mContentView, wmParams);
	}
	
	public void show() {
		if (mContentView != null) {
			wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
			wmParams.format = PixelFormat.RGBA_8888;
			wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
			wmParams.alpha = 1.0f;
			wmParams.gravity = Gravity.LEFT | Gravity.TOP;
			wmParams.x = 0;
			wmParams.y = 0;
			wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
			wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
			// 显示自定义悬浮窗口
			wm.addView(mContentView, wmParams);
			bShow = true;
		}
	}

	public void close() {
		if (mContentView != null) {
			wm.removeView(mContentView);
			bShow = false;
		}
	}
	
	public boolean isShow() {
		return bShow;
	}

	private FloatClickListener mListener;
	public void setOnFloatListener(FloatClickListener listener) {
		mListener = listener;
	}

	public static interface FloatClickListener {
		public abstract void onFloatClick(View v);
	}

}
