package com.example.exmvector.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class CircleHookView extends View {
	private Context mContext;
	private Paint mPaint;
	private int mInterval = 30;
	private int mCircleProgress = 0;
	private int mDownX = 0, mDownY = 0;
	private int mUpX = 0, mUpY = 0;
	private int mLineColor = 0xff3570be;
	private int mLineWidth = 5;
	private int mWidth, mHeight, mRadius, mHookDown;
	private Handler mHandler = new Handler();
	private boolean mRunning = false;

	public CircleHookView(Context context) {
		this(context, null);
	}

	public CircleHookView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mPaint = new Paint();
		mPaint.setColor(mLineColor); //颜色
		mPaint.setStrokeWidth(mLineWidth); //线宽
		mPaint.setStyle(Paint.Style.STROKE); //画线条
		mPaint.setAntiAlias(true); //消除锯齿
	}

	public void render() {
		mCircleProgress = 0;
		mDownX = 0;
		mDownY = 0;
		mUpX = 0;
		mUpY = 0;
		mRunning = true;
		mHandler.postDelayed(mRefresh, 0);
	}

	private Runnable mRefresh = new Runnable() {
		@Override
		public void run() {
			if (mRunning == true) {
				if (mCircleProgress < 100) {
					mCircleProgress += 2;
				}
				postInvalidate();
				mHandler.postDelayed(this, mInterval);
			}
		}
	};

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(heightMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
		mRadius = Math.min(mWidth, mHeight)/2;
		mHookDown = mRadius - mWidth/5;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//画圆弧/圆圈
		RectF rectF = new RectF(0+mLineWidth/2, 0+mLineWidth/2, 
				mWidth-mLineWidth/2, mHeight-mLineWidth/2);
		canvas.drawArc(rectF, 235, -360 * mCircleProgress / 100, false, mPaint);
		if (mCircleProgress >= 100) {
			if (mDownX < mRadius / 3) {
				mDownX += 2;
				mDownY += 2;
			}
			//画打勾的第一段线
			canvas.drawLine(mHookDown, mRadius, mHookDown+mDownX, mRadius+mDownY, mPaint);
			if (mDownX >= mRadius/3 && mUpX==0) {
				mUpX = mDownX;
				mUpY = mDownY;
			} else if (mDownX >= mRadius/3 && mUpX < mRadius) {
				mUpX += 2;
				mUpY -= 2;
			} else if (mUpX-mDownX > mRadius) {
				mRunning = false;
			}
			//画打勾的第二段线
			canvas.drawLine(mHookDown+mDownX, mRadius+mDownY, mHookDown+mUpX, mRadius+mUpY, mPaint);
		}
	}
	
}
