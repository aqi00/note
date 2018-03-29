package com.example.exmtabfragment.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.exmtabfragment.MainApplication;
import com.example.exmtabfragment.PersonalCustomActivity;
import com.example.exmtabfragment.R;

public class TabFourthFragment extends Fragment implements OnClickListener {
	private static final String TAG = "TabFourthFragment";
	protected View mView;
	protected Context mContext;
	private String mTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mView = inflater.inflate(R.layout.fragment_tab_fourth, container, false);

		mTitle = mContext.getResources().getString(R.string.menu_fourth);
		String desc = String.format("我是%s页面，来自%s", 
				mTitle, getArguments().getString("tag"));
		TextView tv_fourth = (TextView) mView.findViewById(R.id.tv_fourth);
		tv_fourth.setText(desc);

		mView.findViewById(R.id.btn_custom).setOnClickListener(this);

		MainApplication.getInstance().TabCreateName = TabFourthFragment.class.getName();
		return mView;
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "setUserVisibleHint isVisibleToUser="+isVisibleToUser
				+", mContext is "+(mContext==null?"null":"not null"));
		if (isVisibleToUser) {
			MainApplication.getInstance().TabPagerName = TabFourthFragment.class.getName();
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_custom) {
			Intent intent = new Intent(mContext, PersonalCustomActivity.class);
			mContext.startActivity(intent);
		}
	}
	
}
