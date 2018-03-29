package com.example.exmtablayout.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.exmtablayout.R;

public class GoodsDetailFragment extends Fragment {
	private static final String TAG = "GoodsDetailFragment";
	protected View mView;
	protected Context mContext;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		mView = inflater.inflate(R.layout.fragment_goods_detail, container, false);
		
		return mView;
	}
	
}
