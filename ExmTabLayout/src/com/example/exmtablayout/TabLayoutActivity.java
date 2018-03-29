package com.example.exmtablayout;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.exmtablayout.adapter.GoodsPagerAdapter;

public class TabLayoutActivity extends AppCompatActivity implements 
		OnTabSelectedListener, OnPageChangeListener {
	private final static String TAG = "TabLayoutActivity";
	private Toolbar tl_head;
	private ViewPager vp_content;
	private TabLayout tab_title;
	private ArrayList<String> mTitleArray = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_layout);
		tl_head = (Toolbar) findViewById(R.id.tl_head);
		tab_title = (TabLayout) findViewById(R.id.tab_title);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		setSupportActionBar(tl_head);
		tl_head.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		
		mTitleArray.add("商品");
		mTitleArray.add("详情");
		initTabLayout();
		initTabViewPager();
	}
	
	private void initTabLayout() {
		tab_title.addTab(tab_title.newTab().setText(mTitleArray.get(0)));
		tab_title.addTab(tab_title.newTab().setText(mTitleArray.get(1)));
		tab_title.setOnTabSelectedListener(this);
	}

	private void initTabViewPager() {
		GoodsPagerAdapter adapter = new GoodsPagerAdapter(
				getSupportFragmentManager(), mTitleArray);
		vp_content.setAdapter(adapter);
		vp_content.addOnPageChangeListener(this);
	}

	@Override
	public void onTabReselected(Tab tab) {
	}

	@Override
	public void onTabSelected(Tab tab) {
		vp_content.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab) {
	}

	@Override
	public void onPageScrollStateChanged(int position) {
	}

	@Override
	public void onPageScrolled(int position, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		tab_title.getTabAt(position).select();
	}

}
