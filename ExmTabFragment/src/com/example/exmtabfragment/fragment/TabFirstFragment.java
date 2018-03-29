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

public class TabFirstFragment extends Fragment {
	private static final String TAG = "TabFirstFragment";
	protected View mView;
	protected Context mContext;
	private String mTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mView = inflater.inflate(R.layout.fragment_tab_first, container, false);

		mTitle = mContext.getResources().getString(R.string.menu_first);
		Log.d(TAG, "onCreateView mContext is "+(mContext==null?"null":"not null"));
		Log.d(TAG, "onCreateView mTitle="+mTitle);
		String desc = String.format("我是%s页面，来自%s", 
				mTitle, getArguments().getString("tag"));
		TextView tv_first = (TextView) mView.findViewById(R.id.tv_first);
		tv_first.setText(desc);

		MainApplication.getInstance().TabCreateName = TabFirstFragment.class.getName();
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "setUserVisibleHint isVisibleToUser="+isVisibleToUser
				+", mContext is "+(mContext==null?"null":"not null"));
//		if (isVisibleToUser) {
//			MainApplication.getInstance().TabPagerName = TabFirstFragment.class.getName();
//			//只在ViewPager中显示提示对话框
//			if (mContext != null) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//				builder.setTitle(mTitle).setMessage("提示信息")
//					.setNegativeButton("取消", null);
//				builder.create().show();
//			}
//		}
	}

	@Override
	public void onAttach(Context context) {
		Log.d(TAG, "onAttach context is "+(context==null?"null":"not null"));
		//此处只对mContext赋值，不对mTitlle赋值；则ViewPager的第一页为首页页面时，对话框将没有标题
		//原因是初始生命周期流程为onAttach->setUserVisibleHint->onCreateView
		mContext = context;
		super.onAttach(context);
	}
	
}
