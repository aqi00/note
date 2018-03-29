package com.example.exmword;

import java.util.Map;

import org.yufeng.jchmlib.ChmExtractor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.util.MD5Util;

public class ChmActivity extends FragmentActivity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "ChmActivity";
	private WebView wv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chm);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		wv_content = (WebView) findViewById(R.id.wv_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"chm"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		ProgressDialog pd = ProgressDialog.show(this, "请稍候", "正在努力加载"+fileName);
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/Download/chm/" + MD5Util.encrypByMd5(path);
		try {
			String index_path = ChmExtractor.getContentFiles(path, dir);
			Log.d(TAG, "index_path="+index_path);
			wv_content.loadUrl("file:///" + index_path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pd!=null && pd.isShowing()) {
				pd.dismiss();
			}
		}
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}

	@Override
	public void onBackPressed() {
		if (wv_content.canGoBack()) {
			wv_content.goBack();
			return;
		} else {
			finish();
		}
	}

}
