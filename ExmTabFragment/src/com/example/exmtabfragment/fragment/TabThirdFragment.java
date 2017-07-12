package com.example.exmtabfragment.fragment;

import com.example.exmtabfragment.MainApplication;
import com.example.exmtabfragment.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabThirdFragment extends Fragment {
	private static final String TAG = "TabThirdFragment";
	protected View mView;
	protected Context mContext;
	private String mTitle;
	private boolean checkVisible = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mView = inflater.inflate(R.layout.fragment_tab_third, container, false);

		mTitle = mContext.getResources().getString(R.string.menu_third);
		String desc = String.format("我是%s页面，来自%s", 
				mTitle, getArguments().getString("tag"));
		TextView tv_third = (TextView) mView.findViewById(R.id.tv_third);
		tv_third.setText(desc);

		Log.d(TAG, "onCreateView");
//		//在非ViewPager与ViewPager中都显示提示对话框
//		//加上checkVisible的判断，是为了避免ViewPager打开旁边页时触发本页的提示框
//		if (!checkVisible) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//			builder.setTitle(mTitle).setMessage("提示信息")
//				.setNegativeButton("取消", null);
//			builder.create().show();
//		}
		//此处的TabCreateName给TabHost使用
		MainApplication.getInstance().TabCreateName = TabThirdFragment.class.getName();
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "setUserVisibleHint isVisibleToUser="+isVisibleToUser
				+", mContext is "+(mContext==null?"null":"not null"));
		checkVisible = true;
		if (isVisibleToUser) {
			//此处的TabPagerName给ViewPager使用
			MainApplication.getInstance().TabPagerName = TabThirdFragment.class.getName();
		}
	}
	
}
