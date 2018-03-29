package com.example.exmword.adapter;

import java.util.ArrayList;

import com.example.exmword.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PdfTurnAdapter extends BaseAdapter {
	private final static String TAG = "PdfTurnAdapter";
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<String> mImgArray = new ArrayList<String>();
	
	public PdfTurnAdapter(Context context, ArrayList<String> imgArray) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImgArray = imgArray;
	}
	@Override
	public int getCount() {
		return mImgArray.size();
	}

	@Override
	public Object getItem(int position) {
		return mImgArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView position="+position);
		ViewGroup layout;
		if(convertView == null) {
			layout = (ViewGroup) mInflater.inflate(R.layout.item_image, null);
		} else {
			layout = (ViewGroup) convertView;
		}
		//position有可能超出范围 oys 20150707
		if (position>=0 && position < mImgArray.size()) {
			setViewContent(layout, position);
		}
		
		return layout;
	}
	
	private void setViewContent(ViewGroup group, int position) {
		ImageView iv_content = (ImageView) group.findViewById(R.id.iv_content);
		iv_content.setImageBitmap(BitmapFactory.decodeFile(mImgArray.get(position)));
	}

}
