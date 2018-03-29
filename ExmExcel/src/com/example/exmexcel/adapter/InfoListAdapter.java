package com.example.exmexcel.adapter;

import java.util.List;

import com.example.exmexcel.R;
import com.example.exmexcel.bean.PersonInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InfoListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<PersonInfo> mInfoList;

	public InfoListAdapter(Context context, List<PersonInfo> file_list) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mInfoList = file_list;
	}

	@Override
	public int getCount() {
		return mInfoList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mInfoList.get(arg0);
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
			convertView = mInflater.inflate(R.layout.item_info, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
			holder.tv_age = (TextView) convertView.findViewById(R.id.tv_age);
			holder.tv_job = (TextView) convertView.findViewById(R.id.tv_job);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PersonInfo info = mInfoList.get(position);
		holder.tv_name.setText(info.name);
		holder.tv_sex.setText(PersonInfo.sexArray[info.sex]);
		holder.tv_age.setText(info.age+"");
		holder.tv_job.setText(info.job);
		
		return convertView;
	}

	public final class ViewHolder {
		public TextView tv_name;
		public TextView tv_sex;
		public TextView tv_age;
		public TextView tv_job;
	}

}
