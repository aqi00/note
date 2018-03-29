package com.example.exmtabfragment;

import com.example.exmtabfragment.fragment.TabFirstFragment;
import com.example.exmtabfragment.fragment.TabFourthFragment;
import com.example.exmtabfragment.fragment.TabSecondFragment;
import com.example.exmtabfragment.fragment.TabThirdFragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabSimpleActivity extends FragmentActivity {
	private static final String TAG = "TabSimpleActivity";
	private FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_simple);
		Bundle bundle = new Bundle();
		bundle.putString("tag", TAG);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
       	mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		
		//addTab(标题，跳转的Fragment，传递参数的Bundle)
		mTabHost.addTab(getTabView(R.string.menu_first, R.drawable.tab_first_selector),
				TabFirstFragment.class, bundle);
		mTabHost.addTab(getTabView(R.string.menu_second, R.drawable.tab_second_selector),
				TabSecondFragment.class, bundle);
		mTabHost.addTab(getTabView(R.string.menu_third, R.drawable.tab_third_selector),
				TabThirdFragment.class, bundle);
		mTabHost.addTab(getTabView(R.string.menu_fourth, R.drawable.tab_fourth_selector),
				TabFourthFragment.class, bundle);
		//设置tabs之间的分隔线不显示
		mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
	}
	
	private TabSpec getTabView(int textId, int imgId) {
		String text = getResources().getString(textId);
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
	
}
