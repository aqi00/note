package com.example.exmtabfragment;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import com.example.exmtabfragment.adapter.MainTabAdapter;
import com.example.exmtabfragment.util.TabUtil;

public class TabSlidingActivity extends FragmentActivity implements 
		OnTabChangeListener, OnPageChangeListener {
	private static final String TAG = "TabSlidingActivity";
	private FragmentTabHost mTabHost;
	private Bundle mBundle = new Bundle();
	private ViewPager vp_main;
	private ArrayList<String> mNameArray = new ArrayList<String>();
	private ArrayList<Fragment> mTabList = new ArrayList<Fragment>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_sliding);
		mBundle.putString("tag", TAG);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
       	mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
       	vp_main = (ViewPager) findViewById(R.id.vp_main);
	}

	@Override
	protected void onResume() {
		mNameArray.clear();
		mTabList.clear();
		mTabHost.clearAllTabs();
		Log.d(TAG, "TabName="+MainApplication.getInstance().TabPagerName);
		int tabPos = 0;
		//addTab(标题，跳转的Fragment，传递参数的Bundle)
       	String tabInfo = TabUtil.readTabInfo(this);
       	for (int i=0, j=0; i<tabInfo.length(); i++) {
       		if (tabInfo.substring(i, i+1).equals("1")) {
       			mTabHost.addTab(getTabView(TabUtil.TabNameArray[i], TabUtil.TabSelectorArray[i]), 
       					TabUtil.TabClassArray[i], mBundle);
       			if (MainApplication.getInstance().TabPagerName.equals(TabUtil.TabClassArray[i].getName())) {
       				tabPos = j;
       			}
       			j++;
       			mTabList.add(Fragment.instantiate(this, TabUtil.TabClassArray[i].getName()));
       		}
       	}
		mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
		mTabHost.setOnTabChangedListener(this);
		
		vp_main.setAdapter(new MainTabAdapter(getSupportFragmentManager(), mTabList, mBundle));
		vp_main.addOnPageChangeListener(this);
		vp_main.setCurrentItem(tabPos);
		super.onResume();
	}
	
	private TabSpec getTabView(int textId, int imgId) {
		String text = getResources().getString(textId);
		mNameArray.add(text);  //滑动页面需要添加这行
		Drawable drawable = getResources().getDrawable(imgId);
		//必须设置图片大小，否则不显示
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		View item_tabbar = getLayoutInflater().inflate(R.layout.item_tabbar, null);
		TextView tv_item = (TextView) item_tabbar.findViewById(R.id.tv_item_tabbar);
		tv_item.setText(text);
		tv_item.setCompoundDrawables(null, drawable, null, null);
		TabSpec spec = mTabHost.newTabSpec(text).setIndicator(item_tabbar);
		return spec;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if (mTabHost.getCurrentTab() != arg0) {
			mTabHost.setCurrentTab(arg0);
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mNameArray.indexOf(tabId);
		if (vp_main.getCurrentItem() != position) {
			vp_main.setCurrentItem(position);
		}
	}

}
