package com.example.exmword.fragment;

import com.example.exmword.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class HtmlFragment extends Fragment {
	private static final String TAG = "HtmlFragment";
	protected View mView;
	protected Context mContext;
	private String htmlPath;

	public static HtmlFragment newInstance(String htmlPath) {
		HtmlFragment fragment = new HtmlFragment();
		Bundle bundle = new Bundle();
		bundle.putString("htmlPath", htmlPath);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		if (getArguments() != null) {
			htmlPath = getArguments().getString("htmlPath");
		}
		mView = inflater.inflate(R.layout.fragment_html, container, false);
		WebView wv_content = (WebView) mView.findViewById(R.id.wv_content);
		wv_content.loadUrl("file:///" + htmlPath);
		return mView;
	}

}
