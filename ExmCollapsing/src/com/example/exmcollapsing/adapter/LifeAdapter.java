package com.example.exmcollapsing.adapter;

import java.util.ArrayList;

import com.example.exmcollapsing.R;
import com.example.exmcollapsing.bean.LifeItem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class LifeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<LifeItem> mItemArray;

	public LifeAdapter(Context context, ArrayList<LifeItem> itemArray) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mItemArray = itemArray;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = null;
		ViewHolder holder = null;
		v = mInflater.inflate(R.layout.item_life, parent, false);
		holder = new ItemHolder(v);
		return holder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
		ItemHolder holder = (ItemHolder) vh;
		holder.iv_pic.setImageResource(mItemArray.get(position).pic);
		holder.tv_title.setText(mItemArray.get(position).title);
	}

	@Override
	public int getItemCount() {
		return mItemArray.size();
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ItemHolder extends RecyclerView.ViewHolder {
		public ImageView iv_pic;
		public TextView tv_title;

		public ItemHolder(View v) {
			super(v);
			iv_pic = (ImageView) v.findViewById(R.id.iv_pic);
			tv_title = (TextView) v.findViewById(R.id.tv_title);
		}
	}

}
