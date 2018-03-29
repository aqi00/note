package com.artifex.mupdf.task;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.widget.MuPDFPageView;

import android.graphics.PointF;
import android.os.AsyncTask;

public class MuPDFPageTask extends AsyncTask<Void, Void, PointF> {
	private MuPDFCore mPdfCore;
	private MuPDFPageView mPageView;
	private int mPos;
	
	public MuPDFPageTask(MuPDFCore core, MuPDFPageView pageView, int position) {
		mPdfCore = core;
		mPageView = pageView;
		mPos = position;
	}
	
	@Override
	protected PointF doInBackground(Void... arg0) {
		return mPdfCore.getPageSize(mPos);
	}

	@Override
	protected void onPostExecute(PointF result) {
		super.onPostExecute(result);
		if (mListener != null) {
			mListener.onRead(mPageView, mPos, result);
		}
	}

	private OnPDFListener mListener;
	public void setPDFListener(OnPDFListener listener) {
		mListener = listener;
	}

	public static interface OnPDFListener {
		public abstract void onRead(MuPDFPageView pageView, int position, PointF result);
	}

}
