package com.example.exmtabfragment.util;

import com.example.exmtabfragment.R;
import com.example.exmtabfragment.fragment.TabFirstFragment;
import com.example.exmtabfragment.fragment.TabFourthFragment;
import com.example.exmtabfragment.fragment.TabSecondFragment;
import com.example.exmtabfragment.fragment.TabThirdFragment;

import android.content.Context;
import android.content.SharedPreferences;

public class TabUtil {
	public static int[] TabSelectorArray = {
		R.drawable.tab_first_selector, R.drawable.tab_second_selector, 
		R.drawable.tab_third_selector, R.drawable.tab_fourth_selector};
	public static int[] TabNameArray = {
		R.string.menu_first,  R.string.menu_second, 
		R.string.menu_third, R.string.menu_fourth};
	public static Class<?>[] TabClassArray = {
		TabFirstFragment.class, TabSecondFragment.class, 
		TabThirdFragment.class, TabFourthFragment.class};
	private static SharedPreferences mShared;
	
	public static String readTabInfo(Context ctx) {
		mShared = ctx.getSharedPreferences("share", Context.MODE_PRIVATE);
		String tabInfo = mShared.getString("tabInfo", "1111");
		return tabInfo;
	}

	public static void writeTabInfo(Context ctx, String tabInfo) {
		mShared = ctx.getSharedPreferences("share", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = mShared.edit();
		editor.putString("tabInfo", tabInfo);
		editor.commit(); 
	}

}
