package com.example.exmrefressh.widget;

import java.util.ArrayList;

import com.example.exmrefressh.R;
import com.example.exmrefressh.util.Utils;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

public class BannerFlipper extends RelativeLayout {
    private static final String TAG = "BannerFlipper";
    private Context mContext;
    private ViewFlipper mFlipper;
    private RadioGroup mGroup;
    private int dip_15;
    private GestureDetector mGesture;
    private float mFlipGap = 20f;

    public BannerFlipper(Context context) {
        this(context, null);
    }

    public BannerFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void start() {
        startFlip();
    }

    public void setImage(ArrayList<Integer> imageList) {
        for (Integer imageID : imageList) {
            ImageView iv_item = new ImageView(mContext);
            iv_item.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            iv_item.setScaleType(ImageView.ScaleType.FIT_XY);
            iv_item.setImageResource(imageID);
            mFlipper.addView(iv_item);
        }
        for (int i = 0; i < imageList.size(); i++) {
            RadioButton radio = new RadioButton(mContext);
            radio.setLayoutParams(new RadioGroup.LayoutParams(dip_15, dip_15));
            radio.setGravity(Gravity.CENTER);
            radio.setButtonDrawable(R.drawable.indicator_selector);
            mGroup.addView(radio);
        }
        mFlipper.setDisplayedChild(imageList.size() - 1);
        startFlip();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.banner_flipper, null);
        mFlipper = (ViewFlipper) view.findViewById(R.id.banner_flipper);
        mGroup = (RadioGroup) view.findViewById(R.id.rg_indicator);
        addView(view);
        dip_15 = Utils.dip2px(mContext, 15);
        // 该手势的onSingleTapUp事件是点击时进入广告页
        mGesture = new GestureDetector(mContext, new BannerGestureListener());
        mHandler.postDelayed(mRefresh, 200);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        mGesture.onTouchEvent(event);
        return true;
    }

    final class BannerGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public final boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > mFlipGap) {
                startFlip();
                return true;
            }
            if (e1.getX() - e2.getX() < -mFlipGap) {
                backFlip();
                return true;
            }
            return false;
        }

        @Override
        public final void onLongPress(MotionEvent event) {
        }

        @Override
        public final boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //return false;
            // 如果外层是普通的ScrollView，则此处不允许父容器的拦截动作
            // CustomScrollActivity里面通过自定义ScrollView，来区分水平滑动还是垂直滑动
            // BannerOptimizeActivity使用系统ScrollView，则此处需要下面代码禁止父容器的拦截
            if (Math.abs(distanceY) < Math.abs(distanceX)) {
                BannerFlipper.this.getParent().requestDisallowInterceptTouchEvent(true);
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final void onShowPress(MotionEvent event) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            int position = mFlipper.getDisplayedChild();
            mListener.onBannerClick(position);
            return false;
        }

    }

    private void startFlip() {
        mFlipper.startFlipping();
        mFlipper.showNext();
    }

    private void backFlip() {
        mFlipper.startFlipping();
        mFlipper.showPrevious();
    }

    private Handler mHandler = new Handler();
    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            int pos = mFlipper.getDisplayedChild();
            ((RadioButton) mGroup.getChildAt(pos)).setChecked(true);
            mHandler.postDelayed(this, 200);
        }
    };

    private BannerClickListener mListener;

    public void setOnBannerListener(BannerClickListener listener) {
        mListener = listener;
    }

    public interface BannerClickListener {
        void onBannerClick(int position);
    }

}
