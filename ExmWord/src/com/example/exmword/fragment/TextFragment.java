package com.example.exmword.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.exmword.R;

public class TextFragment extends Fragment {
	private static final String TAG = "TextFragment";
	protected View mView;
	protected Context mContext;
	private String mContent;

	public static TextFragment newInstance(String text) {
		TextFragment fragment = new TextFragment();
		Bundle bundle = new Bundle();
		bundle.putString("text", text);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		if (getArguments() != null) {
			mContent = getArguments().getString("text");
		}
		mView = inflater.inflate(R.layout.fragment_text, container, false);
		TextView tv_content = (TextView) mView.findViewById(R.id.tv_content);
		tv_content.setText(mContent.replace(" 　　", "\n 　　"));
		return mView;
	}

}
