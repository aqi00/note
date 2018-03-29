package com.example.exmrefressh.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by ouyangshen on 2018/1/4.
 */
public class PullDownScrollView extends ScrollView {
    private float mOffsetX, mOffsetY;
    private float mLastPosX, mLastPosY;

    public PullDownScrollView(Context context) {
        this(context, null);
    }

    public PullDownScrollView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean result = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOffsetX = 0.0F;
                mOffsetY = 0.0F;
                mLastPosX = event.getX();
                mLastPosY = event.getY();
                result = super.onInterceptTouchEvent(event); // false传给子控件
                break;
            default:
                float thisPosX = event.getX();
                float thisPosY = event.getY();
                mOffsetX += Math.abs(thisPosX - mLastPosX); // x轴偏差
                mOffsetY += Math.abs(thisPosY - mLastPosY); // y轴偏差
                mLastPosX = thisPosX;
                mLastPosY = thisPosY;
                if (mOffsetX < 3 && mOffsetY < 3) {
                    result = false; // false传给子控件（点击事件）
                } else if (mOffsetX < mOffsetY) {
                    result = true; // true不传给子控件（垂直滑动）
                } else {
                    result = false; // false传给子控件
                }
                break;
        }
        return result;
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        boolean isScrolledToTop;
        boolean isScrolledToBottom;
        if (getScrollY() == 0) {
            // 下拉滚动到顶部
            isScrolledToTop = true;
            isScrolledToBottom = false;
        } else if (getScrollY() + getHeight() - getPaddingTop() - getPaddingBottom() == getChildAt(0).getHeight()) {
            // 上拉滚动到底部
            isScrolledToBottom = true;
            isScrolledToTop = false;
        } else {
            // 未拉到顶部，也未拉到底部
            isScrolledToTop = false;
            isScrolledToBottom = false;
        }
        if (mScrollListener != null) {
            if (isScrolledToTop) {
                // 触发下拉到顶部的事件
                mScrollListener.onScrolledToTop();
            } else if (isScrolledToBottom) {
                // 触发上拉到底部的事件
                mScrollListener.onScrolledToBottom();
            }
        }
    }

    private ScrollListener mScrollListener;
    // 设置滚动监听器的实例
    public void setScrollListener(ScrollListener listener) {
        mScrollListener = listener;
    }

    // 定义一个滚动监听器，用于捕捉到达顶部和到达底部的事件
    public interface ScrollListener {
        void onScrolledToBottom();
        void onScrolledToTop();
    }

}
