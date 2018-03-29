package com.example.exmword.fragment;

import com.artifex.mupdf.task.MuPDFPageTask;
import com.artifex.mupdf.task.MuPDFPageTask.OnPDFListener;
import com.artifex.mupdf.widget.MuPDFPageView;
import com.example.exmword.MainApplication;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PdfFragment extends Fragment implements OnPDFListener {
	private static final String TAG = "PdfFragment";
	protected View mView;
	protected Context mContext;
	private int position;

	public static PdfFragment newInstance(int position) {
		PdfFragment fragment = new PdfFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("position", position);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "width="+container.getMeasuredWidth()+", height="+container.getMeasuredHeight());
		mContext = getActivity();
		if (getArguments() != null) {
			position = getArguments().getInt("position");
		}
		MuPDFPageView pageView = new MuPDFPageView(mContext, 
				MainApplication.getInstance().pdf_core, 
				new Point(container.getMeasuredWidth(), container.getMeasuredHeight()));
		PointF pageSize = MainApplication.getInstance().page_sizes.get(position);
		if (pageSize != null) {
			pageView.setPage(position, pageSize);
		} else {
			pageView.blank(position);
			MuPDFPageTask task = new MuPDFPageTask(
					MainApplication.getInstance().pdf_core, pageView, position);
			task.setPDFListener(this);
			task.execute();
		}
		return pageView;
	}

	@Override
	public void onRead(MuPDFPageView pageView, int position, PointF result) {
		MainApplication.getInstance().page_sizes.put(position, result);
		if (pageView.getPage() == position) {
			pageView.setPage(position, result);
		}
	}
}
