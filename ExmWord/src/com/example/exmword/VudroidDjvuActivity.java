package com.example.exmword;

import java.io.File;
import java.util.Map;

import org.vudroid.core.DecodeService;
import org.vudroid.core.DecodeServiceBase;
import org.vudroid.core.DocumentView;
import org.vudroid.djvudroid.codec.DjvuContext;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;

public class VudroidDjvuActivity extends Activity implements OnClickListener,
		FileSelectCallbacks {
	private final static String TAG = "VudroidDjvuActivity";
	private FrameLayout fr_content;
	private DecodeService decodeService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vudroid_djvu);

		decodeService = new DecodeServiceBase(new DjvuContext());
		findViewById(R.id.btn_open).setOnClickListener(this);
		fr_content = (FrameLayout) findViewById(R.id.fr_content);
	}

	@Override
	protected void onDestroy() {
		decodeService.recycle();
		decodeService = null;
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] { "djvu" }, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path=" + path);
		DocumentView documentView = new DocumentView(this);
		documentView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		decodeService.setContentResolver(getContentResolver());
		decodeService.setContainerView(documentView);
		documentView.setDecodeService(decodeService);
		decodeService.open(Uri.fromFile(new File(path)));
		fr_content.addView(documentView);
		documentView.showDocument();
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}

}
