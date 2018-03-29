package com.example.exmword;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.util.WordUtil;

public class WordHtmlActivity extends Activity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "WordHtmlActivity";
	private WebView wv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_html);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		wv_content = (WebView) findViewById(R.id.wv_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"doc", "docx"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		//tm-extractors-0.4.jar与poi的包在编译时会冲突，二者只能同时导入一个
		WordUtil wu = new WordUtil(path);
		Log.d(TAG, "htmlPath="+wu.htmlPath);
		wv_content.loadUrl("file:///" + wu.htmlPath);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
}
