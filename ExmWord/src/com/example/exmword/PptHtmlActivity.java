package com.example.exmword;

import java.util.Map;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.adapter.PptHtmlAdapter;
import com.example.exmword.util.PptUtil;

public class PptHtmlActivity extends FragmentActivity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "PptHtmlActivity";
	private ViewPager vp_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ppt_html);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"pptx"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		PptUtil pu = new PptUtil(path);
		Log.d(TAG, "pu.htmlArray.size()="+pu.htmlArray.size());
		PptHtmlAdapter adapter = new PptHtmlAdapter(
				getSupportFragmentManager(), pu.htmlArray);
		vp_content.setAdapter(adapter);
		vp_content.setCurrentItem(0);
		vp_content.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
}
