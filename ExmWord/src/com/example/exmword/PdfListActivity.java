package com.example.exmword;

import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.artifex.mupdf.MuPDFCore;
import com.example.exmword.adapter.PdfListAdapter;

public class PdfListActivity extends Activity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "PdfListActivity";
	private ListView lv_content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_list);

		findViewById(R.id.btn_open).setOnClickListener(this);
		lv_content = (ListView) findViewById(R.id.lv_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"pdf"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		try {
			MuPDFCore core = new MuPDFCore(this, path);
			PdfListAdapter adapter = new PdfListAdapter(this, core);
			lv_content.setAdapter(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
}
