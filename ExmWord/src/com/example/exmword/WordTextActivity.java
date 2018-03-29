package com.example.exmword;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

//import org.textmining.text.extraction.WordExtractor;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WordTextActivity extends Activity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "WordTextActivity";
	private TextView tv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_text);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		tv_content = (TextView) findViewById(R.id.tv_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"doc"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		//tm-extractors-0.4.jar与poi的包在编译时会冲突，二者只能同时导入一个
//		String content = readWord(path).trim();
//		Log.d(TAG, "content="+content);
//		tv_content.setText(content);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
//	private String readWord(String file) {
//		String text = "";
//		try {
//			FileInputStream in = new FileInputStream(new File(file));
//			WordExtractor extractor = new WordExtractor();
//			text = extractor.extractText(in);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return text;
//	}

}
