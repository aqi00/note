package com.example.exmword.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.widget.FrameLayout;

public class BookView extends FrameLayout {
	private final static String TAG = "BookView";
	private Context mContext;
	private int mWidth, mHeight;
	private boolean mIsUp = false;
	private MarginLayoutParams mParams;
	public static int DIRECTION_LEFT = -1;
	public static int DIRECTION_RIGHT = 1;

	public BookView(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mIsUp) {
			canvas.drawColor(Color.TRANSPARENT);
		} else {
			canvas.drawColor(0x55000000);
		}
	}
	
	public void setUp(boolean isUp) {
		mIsUp = isUp;
		invalidate();
	}
	
	public void setMargin(int margin) {
		mParams = (MarginLayoutParams) getLayoutParams();
		mParams.leftMargin = margin;
		setLayoutParams(mParams);
		invalidate();
	}
	
	public void scrollView(int direction, int distance, OnScrollListener listener) {
		mListener = listener;
		mHandler.postDelayed(new ScrollRunnable(direction, distance), mTimeGap);
	}

	private OnScrollListener mListener;
	public static interface OnScrollListener {
		public abstract void onScrollEnd(int direction);
	}

	private int mTimeGap = 20;
	private int mDistanceGap = 20;
	private Handler mHandler = new Handler();
	private class ScrollRunnable implements Runnable {
		private int mDirection;
		private int mDistance;
		public ScrollRunnable(int direction, int distance) {
			mDirection = direction;
			mDistance = distance;
		}
		
		@Override
		public void run() {
			if (mDirection==DIRECTION_LEFT && mDistance>-mWidth) {
				mDistance -= mDistanceGap;
				if (mDistance < -mWidth) {
					mDistance = -mWidth;
				}
				mParams.leftMargin = mDistance;
				setLayoutParams(mParams);
				mHandler.postDelayed(new ScrollRunnable(mDirection, mDistance), mTimeGap);
			} else if (mDirection==DIRECTION_RIGHT && mDistance<0) {
				mDistance += mDistanceGap;
				if (mDistance > 0) {
					mDistance = 0;
				}
				mParams.leftMargin = mDistance;
				setLayoutParams(mParams);
				mHandler.postDelayed(new ScrollRunnable(mDirection, mDistance), mTimeGap);
			} else if (mListener != null) {
				mListener.onScrollEnd(mDirection);
			}
		}
	}
}
