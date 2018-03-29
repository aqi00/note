package com.example.exmword;

import java.util.ArrayList;
import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.adapter.PptTextAdapter;
import com.example.exmword.util.PptUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class PptTextActivity extends Activity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "PptTextActivity";
	private ListView lv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt_text);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		lv_content = (ListView) findViewById(R.id.lv_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"ppt"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		ArrayList<String> contentArray = PptUtil.readPPT(path);
		PptTextAdapter adapter = new PptTextAdapter(this, contentArray);
		lv_content.setAdapter(adapter);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
}
