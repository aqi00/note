package com.example.exmword.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.exmword.R;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ImageFragment extends Fragment {
	private static final String TAG = "ImageFragment";
	protected View mView;
	protected Context mContext;
	private String mPath;

	public static ImageFragment newInstance(String path) {
		ImageFragment fragment = new ImageFragment();
		Bundle bundle = new Bundle();
		bundle.putString("path", path);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		if (getArguments() != null) {
			mPath = getArguments().getString("path");
		}
		Log.d(TAG, "path="+mPath);
		mView = inflater.inflate(R.layout.fragment_image, container, false);
		ImageView iv_content = (ImageView) mView.findViewById(R.id.iv_content);
		iv_content.setImageBitmap(BitmapFactory.decodeFile(mPath));
		return mView;
	}
	
}
