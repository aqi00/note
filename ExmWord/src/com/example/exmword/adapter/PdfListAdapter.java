package com.example.exmword.adapter;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.task.MuPDFPageTask;
import com.artifex.mupdf.task.MuPDFPageTask.OnPDFListener;
import com.artifex.mupdf.widget.MuPDFPageView;

public class PdfListAdapter extends BaseAdapter implements OnPDFListener {
	private final static String TAG = "PdfListAdapter";
	private Context mContext;
	private MuPDFCore mPdfCore;
	/**
	 * SparseArray<E>采用了二分法方式代替HashMap<Integer,E> PointF 表示在二维平面中定义点的浮点 x 和 y
	 * 坐标的有序对
	 */
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();

	public PdfListAdapter(Context context, MuPDFCore core) {
		mContext = context;
		mPdfCore = core;
	}

	public int getCount() {
		return mPdfCore.countPages();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "width="+parent.getWidth()+", height="+parent.getHeight());
		MuPDFPageView pageView;
		PointF pageSize = mPageSizes.get(position);
		if (convertView == null) {
			pageView = new MuPDFPageView(mContext, mPdfCore, 
					new Point(parent.getWidth(), parent.getHeight()));
		} else {
			pageView = (MuPDFPageView) convertView;
		}
		if (pageSize != null) {
			pageView.setPage(position, pageSize);
		} else {
			pageView.blank(position);
			MuPDFPageTask task = new MuPDFPageTask(mPdfCore, pageView, position);
			task.setPDFListener(this);
			task.execute();
		}
		return pageView;
	}

	@Override
	public void onRead(MuPDFPageView pageView, int position, PointF result) {
		mPageSizes.put(position, result);
		if (pageView.getPage() == position) {
			pageView.setPage(position, result);
		}
	}
}
