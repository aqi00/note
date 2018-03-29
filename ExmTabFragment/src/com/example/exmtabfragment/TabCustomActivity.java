package com.example.exmtabfragment;

import com.example.exmtabfragment.util.TabUtil;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class TabCustomActivity extends FragmentActivity {
	private static final String TAG = "TabCustomActivity";
	private FragmentTabHost mTabHost;
	private Bundle mBundle = new Bundle();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_custom);
		mBundle.putString("tag", TAG);
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	}

	@Override
	protected void onResume() {
		mTabHost.clearAllTabs();
		Log.d(TAG, "TabName=" + MainApplication.getInstance().TabCreateName);
		int tabPos = 0;
		// addTab(标题，跳转的Fragment，传递参数的Bundle)
		String tabInfo = TabUtil.readTabInfo(this);
		for (int i = 0, j = 0; i < tabInfo.length(); i++) {
			if (tabInfo.substring(i, i + 1).equals("1")) {
				mTabHost.addTab(getTabView(TabUtil.TabNameArray[i],
						TabUtil.TabSelectorArray[i]), TabUtil.TabClassArray[i], mBundle);
				if (MainApplication.getInstance().TabCreateName.equals(TabUtil.TabClassArray[i].getName())) {
					tabPos = j;
				}
				j++;
			}
		}
		mTabHost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
		mTabHost.setCurrentTab(tabPos);
		super.onResume();
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
