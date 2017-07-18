package com.example.exmword.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.exmword.R;

public class PdfStackAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<String> mPathList;

	public PdfStackAdapter(Context context, ArrayList<String> pathList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mPathList = pathList;
	}

	@Override
	public int getCount() {
		return mPathList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mPathList.get(arg0);
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
			convertView = mInflater.inflate(R.layout.item_image, null);
			holder.iv_content = (ImageView) convertView.findViewById(R.id.iv_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Bitmap bitmap = BitmapFactory.decodeFile(mPathList.get(position));
		holder.iv_content.setImageBitmap(bitmap);
		return convertView;
	}

	public final class ViewHolder {
		public ImageView iv_content;
	}

}
