package com.example.exmtabfragment.adapter;

import com.example.exmtabfragment.R;
import com.example.exmtabfragment.util.TabUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TabCustomAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private String mTabInfo;

	public TabCustomAdapter(Context context, String tabInfo) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mTabInfo = tabInfo;
	}
	
	@Override
	public int getCount() {
		return mTabInfo.length();
	}
	
	@Override
	public Object getItem(int position) {
		return mTabInfo.substring(position, position+1);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_tab_custom, null);
			holder.cb_tab_custom = (CheckBox) convertView.findViewById(R.id.cb_tab_custom);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String flag = mTabInfo.substring(position, position+1);
		holder.cb_tab_custom.setText(TabUtil.TabNameArray[position]);
		holder.cb_tab_custom.setChecked(flag.equals("1")?true:false);
		if (TabUtil.TabNameArray[position] == R.string.menu_fourth) {
			holder.cb_tab_custom.setClickable(false);
		} else {
			holder.cb_tab_custom.setClickable(true);
		}
		holder.cb_tab_custom.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String tabInfo = TabUtil.readTabInfo(mContext);
				StringBuilder sb = new StringBuilder(tabInfo);
				sb.replace(position, position+1, isChecked?"1":"0");
				TabUtil.writeTabInfo(mContext, sb.toString());
			}
			
		});
		return convertView;
	}

	public final class ViewHolder {
		private CheckBox cb_tab_custom;
	}

}
