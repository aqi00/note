package com.example.exmrefressh;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.exmrefressh.constant.ImageList;
import com.example.exmrefressh.util.StatusBarUtil;
import com.example.exmrefressh.util.Utils;
import com.example.exmrefressh.widget.BannerFlipper;
import com.example.exmrefressh.widget.PullDownRefreshLayout;

/**
 * Created by ouyangshen on 2018/1/4.
 */
public class MainActivity extends Activity implements
        BannerFlipper.BannerClickListener, PullDownRefreshLayout.PullRefreshListener {
    private static final String TAG = "PullRefreshActivity";
    private PullDownRefreshLayout pdrl_main;
    private TextView tv_flipper;
    private LinearLayout ll_title;
    private ImageView iv_scan;
    private ImageView iv_msg;
    private boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        pdrl_main = (PullDownRefreshLayout) findViewById(R.id.pdrl_main);
        pdrl_main.setOnRefreshListener(this);
        tv_flipper = (TextView) findViewById(R.id.tv_flipper);
        ll_title = (LinearLayout) findViewById(R.id.ll_title);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        iv_msg = (ImageView) findViewById(R.id.iv_msg);

        BannerFlipper banner = (BannerFlipper) findViewById(R.id.banner_flipper);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
        params.height = (int) (Utils.getScreenWidth(this) * 250f / 640f);
        banner.setLayoutParams(params);
        banner.setImage(ImageList.getDefault());
        banner.setOnBannerListener(this);
        // 添加悬浮状态栏效果
        floatStatusBar();
    }

    private void floatStatusBar() {
        StatusBarUtil.fullScreen(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) ll_title.getLayoutParams();
            // 标题栏在上方留出一段距离，看起来仍在状态栏下方
            titleParams.topMargin = StatusBarUtil.getStatusBarHeight(this);
            ll_title.setLayoutParams(titleParams);
        }
    }

    @Override
    public void onBannerClick(int position) {
        String desc = String.format("您点击了第%d张图片", position + 1);
        tv_flipper.setText(desc);
    }

    private Handler mHandler = new Handler();
    private ProgressDialog pd;

    // 开始页面刷新
    private void beginRefresh() {
        if (pd == null || !pd.isShowing()) {
            pd = ProgressDialog.show(this, "请稍等", "正在努力刷新页面");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    endRefresh();
                }
            }, 1000);
        }
    }

    // 结束页面刷新
    private void endRefresh() {
        if (isDragging) {
            pd.dismiss();
            pdrl_main.finishRefresh();
            isDragging = false;
        }
    }

    @Override
    public void pullRefresh() {
        isDragging = true;
        beginRefresh();
    }

    @Override
    public void pullUp() {
        ll_title.setBackgroundResource(R.color.white);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_gray);
        iv_msg.setImageResource(R.drawable.icon_msg_gray);
        // 上拉页面，把状态栏背景改为灰色
        StatusBarUtil.setStatusBarColor(this, Color.DKGRAY);
    }

    @Override
    public void pullDown() {
        ll_title.setBackgroundResource(R.color.transparent);
        ll_title.setVisibility(View.VISIBLE);
        iv_scan.setImageResource(R.drawable.icon_scan_white);
        iv_msg.setImageResource(R.drawable.icon_msg_white);
        // 下拉到顶了，把状态栏背景改为透明
        StatusBarUtil.setStatusBarColor(this, Color.TRANSPARENT);
    }

    @Override
    public void hideTitle() {
        ll_title.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showTitle() {
        ll_title.setVisibility(View.VISIBLE);
    }

}
