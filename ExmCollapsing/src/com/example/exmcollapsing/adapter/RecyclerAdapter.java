package com.example.exmcollapsing.adapter;

import com.example.exmcollapsing.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
	private final static String TAG = "RecyclerAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private String[] mTitleArray;
	
	public RecyclerAdapter(Context context, String[] titleArray) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mTitleArray = titleArray;
	}

	@Override
	public int getItemCount() {
		return mTitleArray.length;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup vg, int viewType) {
		View v = null;
		ViewHolder holder = null;
		v = mInflater.inflate(R.layout.list_title, vg, false);
		holder = new TitleHolder(v);
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, final int position) {
		TitleHolder holder = (TitleHolder) vh;
		holder.tv_seq.setText(""+(position+1));
		holder.tv_title.setText(mTitleArray[position]);
	}
	
	@Override
	public int getItemViewType(int position) {
		//这里返回每项的类型，开发者可自定义头部类型与一般类型，
		//然后在onCreateViewHolder方法中根据类型加载不同的布局，从而实现带头部的网格布局
		return 0;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	public class TitleHolder extends RecyclerView.ViewHolder {
		public LinearLayout ll_item;
		public TextView tv_seq;
		public TextView tv_title;

		public TitleHolder(View v) {
			super(v);
			ll_item = (LinearLayout) v.findViewById(R.id.ll_item);
			tv_seq = (TextView) v.findViewById(R.id.tv_seq);
			tv_title = (TextView) v.findViewById(R.id.tv_title);
		}
		
	}

}
