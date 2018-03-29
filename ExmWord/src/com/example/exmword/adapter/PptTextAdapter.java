package com.example.exmword.adapter;

import java.util.ArrayList;

import com.example.exmword.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PptTextAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<String> mContentList;

	public PptTextAdapter(Context context, ArrayList<String> contentList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mContentList = contentList;
	}

	@Override
	public int getCount() {
		return mContentList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mContentList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_text, null);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_content.setText(mContentList.get(position));
		return convertView;
	}

	public final class ViewHolder {
		public TextView tv_content;
	}

}
