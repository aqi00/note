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

public class TabSecondFragment extends Fragment {
	private static final String TAG = "TabSecondFragment";
	protected View mView;
	protected Context mContext;
	private String mTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//第一次打开ViewPager时，setUserVisibleHint在onCreateView之前执行，也就意味着，此时mContext还没来得及赋值
		mContext = getActivity();
		mView = inflater.inflate(R.layout.fragment_tab_second, container, false);

		mTitle = mContext.getResources().getString(R.string.menu_second);
		String desc = String.format("我是%s页面，来自%s", 
				mTitle, getArguments().getString("tag"));
		TextView tv_second = (TextView) mView.findViewById(R.id.tv_second);
		tv_second.setText(desc);

		MainApplication.getInstance().TabCreateName = TabSecondFragment.class.getName();
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "setUserVisibleHint isVisibleToUser="+isVisibleToUser
				+", mContext is "+(mContext==null?"null":"not null"));
//		if (isVisibleToUser) {
//			MainApplication.getInstance().TabPagerName = TabSecondFragment.class.getName();
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
		Log.d(TAG, "onAttach");
		mContext = context;
		//下面这行保证ViewPager的第一页为分类页面时，也能正确显示对话框的标题
		mTitle = mContext.getResources().getString(R.string.menu_second);
		super.onAttach(context);
	}
	
}
