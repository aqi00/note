package com.artifex.mupdf.widget;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

/**
 * 功能：操作pdf页面，监听相关手势
 */
@SuppressLint("ClickableViewAccessibility")
public class ReaderView extends AdapterView<Adapter> implements
		GestureDetector.OnGestureListener, View.OnTouchListener,
		ScaleGestureDetector.OnScaleGestureListener, Runnable {
	public static boolean NoTouch = true;
	/**
	 *  对角
	 */
	private static final int MOVING_DIAGONALLY = 0;
	private static final int MOVING_LEFT = 1;
	private static final int MOVING_RIGHT = 2;
	private static final int MOVING_UP = 3;
	private static final int MOVING_DOWN = 4;
	/**
	 * 
	 */
	private static final int FLING_MARGIN = 100;
	/**
	 * 
	 */
	private static final int GAP = 20;

	private static final float MIN_SCALE = 1.0f;
	private static final float MAX_SCALE = 5.0f;
	private Adapter mAdapter;
	/**
	 * 适配器的索引
	 */
	private int mCurrent;
	
	/**
	 * 是否重绘布局
	 */
	
	private boolean mResetLayout;
	private final SparseArray<View> mChildViews = new SparseArray<View>(3);
	// Shadows the children of the adapter view
	// but with more sensible indexing
	private final LinkedList<View> mViewCache = new LinkedList<View>();
	private boolean mUserInteracting; // Whether the user is interacting
	private boolean mScaling; // Whether the user is currently pinch zooming
	private float mScale = 1.0f;
	private int mXScroll; // Scroll amounts recorded from events.
	private int mYScroll; // and then accounted for in onLayout
	private final GestureDetector mGestureDetector;
	private final ScaleGestureDetector mScaleGestureDetector;
	private final Scroller mScroller;
	private int mScrollerLastX;
	private int mScrollerLastY;
	private boolean mScrollDisabled;

	public static float scalingFactor = 1;// pdf缩放因子
	public static Bitmap screenShotBitmap;
	public static int screenWidth;
	public static int screenHeight;

	public ReaderView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(context, this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller = new Scroller(context);
	}

	public ReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mGestureDetector = new GestureDetector(context, this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller = new Scroller(context);
	}

	public ReaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(context, this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller = new Scroller(context);
	}

	public int getDisplayedViewIndex() {
		return mCurrent;
	}

	/**
	 * 设置显示视图的索引
	 */
	public void setDisplayedViewIndex(int i) {
		if (0 <= i && i < mAdapter.getCount()) {
			mCurrent = i;
			onMoveToChild(i);
			mResetLayout = true;
			requestLayout();
		}
	}

	public void moveToNext(){
		View v = mChildViews.get(mCurrent + 1);
		if (v != null) {
			slideViewOntoScreen(v);
		}
	}

	public void moveToPrevious() {
		View v = mChildViews.get(mCurrent - 1);
		if (v != null) {
			slideViewOntoScreen(v);
		}
	}

	public void resetupChildren() {
		for (int i = 0; i < mChildViews.size(); i++) {
			onChildSetup(mChildViews.keyAt(i), mChildViews.valueAt(i));
		}
	}

	protected void onChildSetup(int i, View v) {
	}

	protected void onMoveToChild(int i) {
	}

	protected void onSettle(View v) {
	};

	protected void onUnsettle(View v) {
	};

	public View getDisplayedView() {
		return mChildViews.get(mCurrent);
	}

	public void run() {
		if (!mScroller.isFinished()) {
			mScroller.computeScrollOffset();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			mXScroll += x - mScrollerLastX;
			mYScroll += y - mScrollerLastY;
			mScrollerLastX = x;
			mScrollerLastY = y;
			requestLayout();
			post(this);
		} else if (!mUserInteracting) {
			View v = mChildViews.get(mCurrent);
			postSettle(v);
		}
	}

	/**
	 * 用户按下屏幕就会触发，1个MotionEvent ACTION_DOWN
	 * */
	public boolean onDown(MotionEvent arg0) {
		mScroller.forceFinished(true);  //强制停止滚动
		// Log.e("info", "-->onDown");
		return true;
	}

	/**
	 * 用户按下触摸屏、快速移动后松开 由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
	 * 
	 * @param e1
	 *            第1个ACTION_DOWN MotionEvent
	 * @param e2
	 *            最后一个ACTION_MOVE MotionEvent
	 * @param velocityX
	 *            ：X轴上的移动速度，像素/秒
	 * @param velocityY
	 *            ：Y轴上的移动速度，像素/秒
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (mScrollDisabled) {
			return true;
		}
		View v = mChildViews.get(mCurrent);
		if (v != null) {
			Rect bounds = getScrollBounds(v);
			switch (directionOfTravel(velocityX, velocityY)) {
			case MOVING_LEFT:
				if (bounds.left >= 0){
					View vl = mChildViews.get(mCurrent + 1);
					if (vl != null) {
						slideViewOntoScreen(vl);
						return true;
					}
				}
				break;
			case MOVING_RIGHT:
				if (bounds.right <= 0) {
					View vr = mChildViews.get(mCurrent - 1);
					if (vr != null){
						slideViewOntoScreen(vr);
						return true;
					}
				}
				break;
			}
			mScrollerLastX = mScrollerLastY = 0;
			Rect expandedBounds = new Rect(bounds);
			expandedBounds.inset(-FLING_MARGIN, -FLING_MARGIN);
			if (withinBoundsInDirectionOfTravel(bounds, velocityX, velocityY)
					&& expandedBounds.contains(0, 0)) {
				mScroller.fling(0, 0, (int) velocityX, (int) velocityY,
						bounds.left, bounds.right, bounds.top, bounds.bottom);
				post(this);
			}
		}
		return true;
	}

	/**
	 * 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	 * */
	public void onLongPress(MotionEvent e) {
	}

	/**
	 * 功能：扑捉屏幕手势滑动动作 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发。
	 * */
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// 这里控制pdf文件翻页
		if (!mScrollDisabled) {
			mXScroll -= distanceX;
			mYScroll -= distanceY;
			requestLayout();
		}
		return true;
	}

	/**
	 * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
	 * 注意和onDown()的区别，强调的是没有松开或者拖动的状态 .
	 * */
	public void onShowPress(MotionEvent e) {
	}

	/**
	 * 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
	 * */
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	/**
	 * 处理对屏幕的缩放比例
	 * */
	public boolean onScale(ScaleGestureDetector detector) {
		// 截屏视图不显示时，手势操作可以进行
		float previousScale = mScale;
		mScale = Math.min(
				Math.max(mScale * detector.getScaleFactor(), MIN_SCALE),
				MAX_SCALE);
		scalingFactor = mScale / previousScale;// 缩放比例
		// Log.e("info", "--->scalingFactor="+scalingFactor);
		View v = mChildViews.get(mCurrent);
		if (v != null) {
			// Work out the focus point relative to the view top left
			int viewFocusX = (int) detector.getFocusX() - (v.getLeft() + mXScroll);
			int viewFocusY = (int) detector.getFocusY() - (v.getTop() + mYScroll);
			// Scroll to maintain the focus point
			mXScroll += viewFocusX - viewFocusX * scalingFactor;
			mYScroll += viewFocusY - viewFocusY * scalingFactor;
			requestLayout();
		}
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		mScaling = true;
		// Ignore any scroll amounts yet to be accounted for: the
		// screen is not showing the effect of them, so they can
		// only confuse the user
		mXScroll = mYScroll = 0;
		// Avoid jump at end of scaling by disabling scrolling
		// until the next start of gesture
		mScrollDisabled = true;
		return true;// 一定要返回true才会进入onScale()这个函数
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		mScaling = false;
	}

	/**
	 * 在onTouch()方法中，我们调用GestureDetector的onTouchEvent()方法，
	 * 将捕捉到的MotionEvent交给GestureDetector 来分析是否有合适的callback函数来处理用户的手势
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		if (!mScaling) {
			mGestureDetector.onTouchEvent(event);
		}
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			mUserInteracting = true;
		}
		if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			mScrollDisabled = false;
			mUserInteracting = false;
			if (event.getAction() == MotionEvent.ACTION_UP) {
				View v = mChildViews.get(mCurrent);
				if (v != null) {
					if (mScroller.isFinished()) {
						slideViewOntoScreen(v);
					}
					if (mScroller.isFinished()) {
						postSettle(v);
					}
				}
			}
		}
		requestLayout();
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int n = getChildCount();
		for (int i = 0; i < n; i++) {
			measureView(getChildAt(i));
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		View cv = mChildViews.get(mCurrent);
		Point cvOffset;
		if (!mResetLayout) {
			// Move to next or previous if current is sufficiently off center
			if (cv != null) {
				cvOffset = subScreenSizeOffset(cv);
				// cv.getRight() may be out of date with the current scale
				// so add left to the measured width for the correct position
				if (cv.getLeft() + cv.getMeasuredWidth() + cvOffset.x + GAP / 2
						+ mXScroll < getWidth() / 2
						&& mCurrent + 1 < mAdapter.getCount()) {
					postUnsettle(cv);
					// post to invoke test for end of animation
					// where we must set hq area for the new current view
					post(this);
					mCurrent++;
					onMoveToChild(mCurrent);
				}
				if (cv.getLeft() - cvOffset.x - GAP / 2 + mXScroll >= getWidth() / 2 && mCurrent > 0) {
					postUnsettle(cv);
					// post to invoke test for end of animation
					// where we must set hq area for the new current view
					post(this);
					mCurrent--;
					onMoveToChild(mCurrent);
				}
			}

			// Remove not needed children and hold them for reuse
			int numChildren = mChildViews.size();
			int childIndices[] = new int[numChildren];
			for (int i = 0; i < numChildren; i++) {
				childIndices[i] = mChildViews.keyAt(i);
			}

			for (int i = 0; i < numChildren; i++) {
				int ai = childIndices[i];
				if (ai < mCurrent - 1 || ai > mCurrent + 1) {
					View v = mChildViews.get(ai);
					mViewCache.add(v);
					removeViewInLayout(v);
					mChildViews.remove(ai);
				}
			}
		} else {
			mResetLayout = false;
			mXScroll = mYScroll = 0;
			// Remove all children and hold them for reuse
			int numChildren = mChildViews.size();
			for (int i = 0; i < numChildren; i++) {
				View v = mChildViews.valueAt(i);
				postUnsettle(v);
				mViewCache.add(v);
				removeViewInLayout(v);
			}
			mChildViews.clear();
			// post to ensure generation of hq area
			post(this);
		}

		// Ensure current view is present
		int cvLeft, cvRight, cvTop, cvBottom;
		boolean notPresent = (mChildViews.get(mCurrent) == null);
		cv = getOrCreateChild(mCurrent);
		cvOffset = subScreenSizeOffset(cv);
		if (notPresent) {
			cvLeft = cvOffset.x;
			cvTop = cvOffset.y;
		} else {
			cvLeft = cv.getLeft() + mXScroll;
			cvTop = cv.getTop() + mYScroll;
		}
		mXScroll = mYScroll = 0;
		cvRight = cvLeft + cv.getMeasuredWidth();
		cvBottom = cvTop + cv.getMeasuredHeight();

		if (!mUserInteracting && mScroller.isFinished()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			cvRight += corr.x;
			cvLeft += corr.x;
			cvTop += corr.y;
			cvBottom += corr.y;
		} else if (cv.getMeasuredHeight() <= getHeight()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			cvTop += corr.y;
			cvBottom += corr.y;
		}
		cv.layout(cvLeft, cvTop, cvRight, cvBottom);

		if (mCurrent > 0) {
			View lv = getOrCreateChild(mCurrent - 1);
			Point leftOffset = subScreenSizeOffset(lv);
			int gap = leftOffset.x + GAP + cvOffset.x;
			lv.layout(cvLeft - lv.getMeasuredWidth() - gap,
					(cvBottom + cvTop - lv.getMeasuredHeight()) / 2, cvLeft - gap,
					(cvBottom + cvTop + lv.getMeasuredHeight()) / 2);
		}

		if (mCurrent + 1 < mAdapter.getCount()) {
			View rv = getOrCreateChild(mCurrent + 1);
			Point rightOffset = subScreenSizeOffset(rv);
			int gap = cvOffset.x + GAP + rightOffset.x;
			rv.layout(cvRight + gap,
					(cvBottom + cvTop - rv.getMeasuredHeight()) / 2, 
					cvRight + rv.getMeasuredWidth() + gap,
					(cvBottom + cvTop + rv.getMeasuredHeight()) / 2);
		}
		invalidate();
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		throw new UnsupportedOperationException("Not supported");
	}

	@Override
	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
		mChildViews.clear();
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int arg0) {
		throw new UnsupportedOperationException("Not supported");
	}

	private View getCached() {
		if (mViewCache.size() == 0) {
			return null;
		} else {
			return mViewCache.removeFirst();
		}
	}

	private View getOrCreateChild(int i) {
		View v = mChildViews.get(i);
		if (v == null) {
			v = mAdapter.getView(i, getCached(), this);
			addAndMeasureChild(i, v);
		}
		onChildSetup(i, v);
		return v;
	}

	private void addAndMeasureChild(int i, View v) {
		LayoutParams params = v.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(v, 0, params, true);
		mChildViews.append(i, v); // Record the view against it's adapter index
		measureView(v);
	}

	private void measureView(View v) {
		v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		float scale = Math.min(
				(float) getWidth() / (float) v.getMeasuredWidth(),
				(float) getHeight() / (float) v.getMeasuredHeight());
		v.measure(View.MeasureSpec.EXACTLY
				| (int) (v.getMeasuredWidth() * scale * mScale),
				View.MeasureSpec.EXACTLY | (int) (v.getMeasuredHeight() * scale * mScale));
	}
	/**
	 * 获得滚动边界
	 */
	private Rect getScrollBounds(int left, int top, int right, int bottom){
		int xmin = getWidth() - right;
		int xmax = -left;
		int ymin = getHeight() - bottom;
		int ymax = -top;
		//如果缩小后小于屏幕大小，则放在中间位置
		if (xmin > xmax) {
			xmin = xmax = (xmin + xmax) / 2;
		}
		if (ymin > ymax) {
			ymin = ymax = (ymin + ymax) / 2;
		}
		return new Rect(xmin, ymin, xmax, ymax);
	}
	/**
	 * 获得滚动边界
	 */
	private Rect getScrollBounds(View v) {
		//计算边界时加上滚动量
		return getScrollBounds(v.getLeft() + mXScroll, v.getTop() + mYScroll,
				v.getLeft() + v.getMeasuredWidth() + mXScroll,
				v.getTop() + v.getMeasuredHeight() + mYScroll);
	}

	/**
	 * 更正坐标
	 * @param bounds
	 * @return
	 */
	private Point getCorrection(Rect bounds) {
		return new Point(Math.min(Math.max(0, bounds.left), bounds.right),
				Math.min(Math.max(0, bounds.top), bounds.bottom));
	}

	private void postSettle(final View v) {
		post(new Runnable() {
			public void run() {
				onSettle(v);
			}
		});
	}

	private void postUnsettle(final View v) {
		post(new Runnable() {
			public void run() {
				onUnsettle(v);
			}
		});
	}

	private void slideViewOntoScreen(View v) {
		Point corr = getCorrection(getScrollBounds(v));
		if (corr.x != 0 || corr.y != 0) {
			mScrollerLastX = mScrollerLastY = 0;
			mScroller.startScroll(0, 0, corr.x, corr.y, 400);
			post(this);
		}
	}

	/**
	 * 屏幕偏移量
	 */
	private Point subScreenSizeOffset(View v) {
		return new Point(Math.max((getWidth() - v.getMeasuredWidth()) / 2, 0),
				Math.max((getHeight() - v.getMeasuredHeight()) / 2, 0));
	}

	/**
	 * 向哪个方向滑动
	 * @param vx X轴方向滑动距离
	 * @param vy Y轴方向滑动距离
	 * @return 滑动方向
	 */
	private static int directionOfTravel(float vx, float vy) {
		if (Math.abs(vx) > 2 * Math.abs(vy)) {
			return (vx > 0) ? MOVING_RIGHT : MOVING_LEFT;
		} else if (Math.abs(vy) > 2 * Math.abs(vx)) {
			return (vy > 0) ? MOVING_DOWN : MOVING_UP;			
		} else {
			return MOVING_DIAGONALLY;
		}
	}

	/**
	 * 一定范围内的滑动方向
	 * @param bounds
	 * @param vx
	 * @param vy
	 * @return
	 */
	private static boolean withinBoundsInDirectionOfTravel(Rect bounds, float vx, float vy) {
		switch (directionOfTravel(vx, vy)) {
		case MOVING_DIAGONALLY:
			return bounds.contains(0, 0);  //返回0,0是否在边界内
		case MOVING_LEFT:
			return bounds.left <= 0;
		case MOVING_RIGHT:
			return bounds.right >= 0;
		case MOVING_UP:
			return bounds.top <= 0;
		case MOVING_DOWN:
			return bounds.bottom >= 0;
		default:
			throw new NoSuchElementException();
		}
	}
}