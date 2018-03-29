package com.example.exmrefressh.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by ouyangshen on 2017/9/11.
 */
public class Utils {
    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    //根据手机的分辨率从 px(像素) 的单位 转成为 dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //获得屏幕的宽度
    public static int getScreenWidth(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    //获得屏幕的高度
    public static int getScreenHeight(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    //获得屏幕的像素密度
    public static float getScreenDensity(Context ctx) {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    //获得线性布局的实际高度
    public static int getRealHeight(View child) {
        LinearLayout llayout = (LinearLayout) child;
        ViewGroup.LayoutParams params = llayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        int widthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int heightSpec;
        if (params.height > 0) {
            heightSpec = View.MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        } else {
            heightSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        llayout.measure(widthSpec, heightSpec);
        return llayout.getMeasuredHeight();
    }

}

