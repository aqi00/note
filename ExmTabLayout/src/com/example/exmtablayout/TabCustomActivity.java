package com.example.exmtablayout;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.ViewPagerOnTabSelectedListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.exmtablayout.adapter.GoodsPagerAdapter;

public class TabCustomActivity extends AppCompatActivity {
	private final static String TAG = "TabCustomActivity";
	private Toolbar tl_head;
	private ViewPager vp_content;
	private TabLayout tab_title;
	private TextView tv_toolbar1, tv_toolbar2;
	private ArrayList<String> mTitleArray = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_custom);
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
		tab_title.addTab(tab_title.newTab().setCustomView(R.layout.item_toolbar1));
		tv_toolbar1 = (TextView) findViewById(R.id.tv_toolbar1);
		tv_toolbar1.setText(mTitleArray.get(0));
		tab_title.addTab(tab_title.newTab().setCustomView(R.layout.item_toolbar2));
		tv_toolbar2 = (TextView) findViewById(R.id.tv_toolbar2);
		tv_toolbar2.setText(mTitleArray.get(1));
		tab_title.setOnTabSelectedListener(new ViewPagerOnTabSelectedListener(vp_content));
	}

	private void initTabViewPager() {
		GoodsPagerAdapter adapter = new GoodsPagerAdapter(
				getSupportFragmentManager(), mTitleArray);
		vp_content.setAdapter(adapter);
		vp_content.addOnPageChangeListener(new SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				tab_title.getTabAt(position).select();
			}
		});
	}

}
