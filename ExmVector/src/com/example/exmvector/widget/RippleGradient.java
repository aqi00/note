package com.example.exmvector.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class RippleGradient extends LinearLayout {
	private final static String TAG = "RippleGradient";
	private int mTargetWidth, mTargetHeight;
	private int mRadiusGap, mRadius, mMaxRadius;
	private int mRealCenterX, mRealCenterY;
	private int[] mLocation = new int[2];
	private int mStartColor = 0x88ff0000, mPasueColor = 0x88ffeeee, mEndColor = 0x88ffffff;
	private int mColorGap;
	private int mDelay = 50;
	private boolean bPressed = false;
	private boolean bRunning = false;
	private boolean bFirst = true;
	private View mTouchTarget;

	public RippleGradient(Context context) {
		this(context, null);
	}

	public RippleGradient(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mColorGap = mEndColor-mStartColor;
	}

	private void initChild(MotionEvent event, View view) {
		mTargetWidth = view.getMeasuredWidth();
		mTargetHeight = view.getMeasuredHeight();
		mRadius = 0;
		bPressed = true;
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int left = location[0] - mLocation[0];
		int right = location[1] - mLocation[1];
		if (bFirst == true) {
			mRealCenterX = (int) event.getRawX() - left;
			mRealCenterY = (int) event.getRawY() - right;
		} else {
			mRealCenterX = (int) event.getX() - left;
			mRealCenterY = (int) event.getY() - right;
		}
		bFirst = false;
		if (mMaxRadius == 0) {
			mMaxRadius = Math.max(mRealCenterX, mTargetWidth - mRealCenterX);
		}
		mRadiusGap = (int) (mMaxRadius / 10);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (bRunning!=true || mTargetWidth<=0 || mTouchTarget==null) {
			return;
		}

		mRadius += mRadiusGap;
		getLocationOnScreen(mLocation);
		int[] location = new int[2];
		mTouchTarget.getLocationOnScreen(location);
		int left = location[0] - mLocation[0];
		int top = location[1] - mLocation[1];
		int right = left + mTouchTarget.getMeasuredWidth();
		int bottom = top + mTouchTarget.getMeasuredHeight();

		float width = mTouchTarget.getMeasuredWidth();
		float height = mTouchTarget.getMeasuredHeight();
		int beginColor = mStartColor + mColorGap*mRadius/mMaxRadius;
		if (beginColor > mEndColor) {
			beginColor = mEndColor;
		}
//		Log.d(TAG, "mRadius="+mRadius+", mStartColor="+mStartColor+", mEndColor="+mEndColor+", beginColor="+beginColor);
		int[] colorArray;
		if (mRadius+mRadiusGap < mMaxRadius) {
			colorArray = new int[] {beginColor, mEndColor};
		} else {
			colorArray = new int[] {mPasueColor, mPasueColor};
		}
		GradientDrawable gradient = new GradientDrawable(
				GradientDrawable.Orientation.LEFT_RIGHT, colorArray);
		gradient.setBounds(left, top, right, bottom);
		gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		gradient.setGradientCenter(mRealCenterX/width, mRealCenterY/height);
		gradient.setGradientRadius(mRadius);
		gradient.draw(canvas);

		if (mRadius <= mMaxRadius) {
			postInvalidateDelayed(mDelay, left, top, right, bottom);
		} else if (!bPressed) {
			bRunning = false;
			postInvalidateDelayed(mDelay, left, top, right, bottom);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 获取水波动画的载体
			mTouchTarget = getTouchTarget(this, event.getRawX(), event.getRawY());
			if (mTouchTarget != null) {
				initChild(event, mTouchTarget);
				bRunning = true;
				postInvalidateDelayed(mDelay);
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			bPressed = false;
			postInvalidateDelayed(mDelay);
		}
		return super.dispatchTouchEvent(event);
	}

	private View getTouchTarget(View view, float x, float y) {
		View target = null;
		ArrayList<View> touchableViews = view.getTouchables();
		for (View child : touchableViews) {
			if (isTouchInView(child, (int) x, (int) y)) {
				target = child;
				break;
			}
		}
		return target;
	}

	private boolean isTouchInView(View view, int x, int y) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int left = location[0];
		int top = location[1];
		int right = left + view.getMeasuredWidth();
		int bottom = top + view.getMeasuredHeight();
		if (view.isEnabled() && x >= left && x <= right && y >= top && y <= bottom) {
			return true;
		} else {
			return false;
		}
	}

}
