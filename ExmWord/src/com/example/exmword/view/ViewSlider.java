package com.example.exmword.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ViewSlider extends FrameLayout implements BookView.OnScrollListener {
	private final static String TAG = "ViewSlider";
	private Context mContext;
	private int mWidth, mHeight;
	private float rawX = 0;
	private ArrayList<String> mPathArray = new ArrayList<String>();
	private int mPos = 0;
	private BookView mPreView, mCurrentView, mNextView;
	private int mShowPage;
	private static int SHOW_NONE = 0;
	private static int SHOW_PRE = 1;
	private static int SHOW_NEXT = 2;
	private boolean isScroll = false;

	public ViewSlider(Context context) {
		this(context, null);
	}

	public ViewSlider(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewSlider(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}
	
	public void setFilePath(ArrayList<String> pathArray) {
		removeAllViews();
		mPathArray = pathArray;
		if (mPathArray.size() > 0) {
			mCurrentView = getBookPage(0, true);
			addView(mCurrentView);
		}
		if (mPathArray.size() > 1) {
			mNextView = getBookPage(1, false);
			addView(mNextView, 0);
		}
	}
	
	private BookView getBookPage(int position, boolean isUp) {
		BookView page = new BookView(mContext);
		MarginLayoutParams params = new LinearLayout.LayoutParams(
				mWidth, LayoutParams.WRAP_CONTENT);
		page.setLayoutParams(params);
		ImageView iv = new ImageView(mContext);
		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_CENTER);
		iv.setImageBitmap(BitmapFactory.decodeFile(mPathArray.get(position)));
		page.addView(iv);
		page.setUp(isUp);
		return page;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isScroll) {
			return super.onTouchEvent(event);
		}
		int distanceX = (int) (event.getRawX() - rawX);
		Log.d(TAG, "action="+event.getAction()+", distanceX="+distanceX);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			rawX = event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			if (distanceX > 0) {  //展示上一页
				if (mPos == 0) {
					mShowPage = SHOW_NONE;
				} else {
					mShowPage = SHOW_PRE;
					mPreView.setUp(true);
					mPreView.setMargin(-mWidth + distanceX);
					mCurrentView.setUp(false);
				}
			} else {  //展示下一页
				if (mPos == mPathArray.size()-1 || mNextView==null) {
					mShowPage = SHOW_NONE;
				} else if (mNextView != null) {
					mShowPage = SHOW_NEXT;
					mCurrentView.setMargin(distanceX);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mShowPage == SHOW_PRE) {
				int direction = Math.abs(distanceX)<mWidth/2 ? BookView.DIRECTION_LEFT : BookView.DIRECTION_RIGHT;
				//Log.d(TAG, "direction="+direction+", mShowPage="+mShowPage+", distanceX="+distanceX);
				mPreView.scrollView(direction, -mWidth+distanceX, this);
				isScroll = true;
			} else if (mShowPage == SHOW_NEXT) {
				int direction = Math.abs(distanceX)>mWidth/2 ? BookView.DIRECTION_LEFT : BookView.DIRECTION_RIGHT;
				//Log.d(TAG, "direction="+direction+", mShowPage="+mShowPage+", distanceX="+distanceX);
				mCurrentView.scrollView(direction, distanceX, this);
				isScroll = true;
			} else {
				isScroll = false;
			}
			break;
		}
		return true;
	}

	@Override
	public void onScrollEnd(int direction) {
		//Log.d(TAG, "direction="+direction+", mPos="+mPos);
		if (mShowPage == SHOW_PRE) {
			if (direction == BookView.DIRECTION_RIGHT) {
				mPos--;
				if (mNextView != null) {
					removeView(mNextView);
				}
				mNextView = mCurrentView;
				mCurrentView = mPreView;
				if (mPos > 0) {
					mPreView = getBookPage(mPos-1, false);
					addView(mPreView);
					mPreView.setMargin(-mWidth);
				} else {
					mPreView = null;
				}
			}
			mCurrentView.setUp(true);
		} else if (mShowPage == SHOW_NEXT) {
			if (direction == BookView.DIRECTION_LEFT) {
				mPos++;
				if (mPreView != null) {
					removeView(mPreView);
				}
				mPreView = mCurrentView;
				mCurrentView = mNextView;
				if (mPos < mPathArray.size()-1) {
					mNextView = getBookPage(mPos+1, false);
					addView(mNextView, 0);
				} else {
					mNextView = null;
				}
			}
			mCurrentView.setUp(true);
		}
		isScroll = false;
	}

}
