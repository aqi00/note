package com.example.exmvector.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class RippleView extends LinearLayout {
	private final static String TAG = "RippleView";
	private Paint mPaint;
	private int mTargetWidth, mTargetHeight, mMinSize;
	private int mRadiusGap, mRadius, mMaxRadius;
	private float mCenterX, mCenterY;
	private int[] mLocation = new int[2];
	private int mPaintColor = 0x22ff0000, mPaintHalfColor = 0x22ffaaaa;
	private int mDelay = 50;
	private boolean bPressed = false;
	private View mTouchTarget;

	public RippleView(Context context) {
		this(context, null);
	}

	public RippleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	private void initChild(MotionEvent event, View view) {
		mCenterX = event.getX();
		mCenterY = event.getY();
		mTargetWidth = view.getMeasuredWidth();
		mTargetHeight = view.getMeasuredHeight();
		mMinSize = Math.min(mTargetWidth, mTargetHeight);
		mRadius = 0;
		bPressed = true;
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int left = location[0] - mLocation[0];
		int realCenterX = (int) mCenterX - left;
		if (mMaxRadius == 0) {
			mMaxRadius = Math.max(realCenterX, mTargetWidth - realCenterX);
			mRadiusGap = mMinSize / 8;
		} else {
			mRadiusGap = (int) (mMaxRadius / 8);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mPaint.getColor()==Color.TRANSPARENT || mTargetWidth<=0 || mTouchTarget==null) {
			return;
		}

		if (mRadius > mMinSize / 2) {
			mRadius += mRadiusGap * 4;
		} else {
			mRadius += mRadiusGap;
		}
		getLocationOnScreen(mLocation);
		int[] location = new int[2];
		mTouchTarget.getLocationOnScreen(location);
		int left = location[0] - mLocation[0];
		int top = location[1] - mLocation[1];
		int right = left + mTouchTarget.getMeasuredWidth();
		int bottom = top + mTouchTarget.getMeasuredHeight();

		canvas.save();
		canvas.clipRect(left, top, right, bottom); // 裁剪水波的范围
		canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint); // 画水波
		canvas.restore();

		Log.d(TAG, "mRadius="+mRadius+", mCenterX="+mCenterX+", mCenterY="+mCenterY);
		if (mRadius <= mMaxRadius) {
			postInvalidateDelayed(mDelay, left, top, right, bottom);
		} else if (!bPressed) {
			if (mPaint.getColor() == mPaintColor) {
				mPaint.setColor(mPaintHalfColor); // 最后一次画水波，颜色减淡
			} else {
				mPaint.setColor(Color.TRANSPARENT); // 结束水波动画
			}
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
				mPaint.setColor(mPaintColor);
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
