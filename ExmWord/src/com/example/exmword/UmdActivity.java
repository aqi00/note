package com.example.exmword;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.adapter.UmdPagerAdapter;
import com.luzaimou.umd.UMD;
import com.luzaimou.umd.UMDDecoder;

public class UmdActivity extends FragmentActivity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "UmdActivity";
	private ViewPager vp_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_umd);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"umd"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		ArrayList<String> contentList = new ArrayList<String>();
		ProgressDialog pd = ProgressDialog.show(this, "请稍候", "正在努力加载"+fileName);
		UMDDecoder umdDecoder = new UMDDecoder();
		try {
			umdDecoder.decode(new File(path));
			UMD umd = umdDecoder.umd;
			contentList = (ArrayList<String>) umd.getChapterContents();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pd!=null && pd.isShowing()) {
				pd.dismiss();
			}
		}
		
		UmdPagerAdapter adapter = new UmdPagerAdapter(getSupportFragmentManager(), contentList);
		vp_content.setAdapter(adapter);
		vp_content.setCurrentItem(0);
		vp_content.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
}
