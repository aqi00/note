package com.example.exmword;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.epub.EpubReader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.adapter.EpubPagerAdapter;
import com.example.exmword.util.FileUtil;
import com.example.exmword.util.MD5Util;

public class EpubActivity extends FragmentActivity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "ChmActivity";
	private TextView tv_meta;
	private ViewPager vp_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_epub);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		tv_meta = (TextView) findViewById(R.id.tv_meta);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_open) {
			FileSelectFragment.show(this, new String[] {"epub"}, null);
		}
	}

	@Override
	public void onConfirmSelect(String absolutePath, String fileName, Map<String, Object> map_param) {
		String path = String.format("%s/%s", absolutePath, fileName);
		Log.d(TAG, "path="+path);
		ProgressDialog pd = ProgressDialog.show(this, "请稍候", "正在努力加载"+fileName);
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/Download/epub/" + MD5Util.encrypByMd5(path);
		EpubReader epubReader = new EpubReader();
		Book book = null;
		try {
			InputStream inputStr = new FileInputStream(path);
			book = epubReader.readEpub(inputStr);
			setBookMeta(book);
			Resources resources = book.getResources();
			Collection<String> hrefArray = resources.getAllHrefs();
			for (String href : hrefArray) {
				Resource res = resources.getByHref(href);
				FileUtil.writeFile(dir+"/"+href, res.getData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pd!=null && pd.isShowing()) {
				pd.dismiss();
			}
		}
		
		ArrayList<String> htmlArray = new ArrayList<String>();
		List<Resource> contents = book.getContents();
		for (int i=0; i<contents.size(); i++) {
			String href = String.format("%s/%s", dir, contents.get(i).getHref());
			htmlArray.add(href);
		}
		EpubPagerAdapter adapter = new EpubPagerAdapter(getSupportFragmentManager(), htmlArray);
		vp_content.setAdapter(adapter);
		vp_content.setCurrentItem(0);
		vp_content.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}
	
	private void setBookMeta(Book book) {
		//书籍的头部信息，可获取标题、语言、作者、封面等信息
		Metadata meta = book.getMetadata();
		String title = String.format("书名：《%s》", meta.getFirstTitle());
		List<Author> authorArray = meta.getAuthors();
		String autors = "作者：";
		for (int i=0; i<authorArray.size(); i++) {
			if (i == 0) {
				autors = String.format("%s%s", autors, authorArray.get(i).toString());
			} else {
				autors = String.format("%s, %s", autors, authorArray.get(i).toString());
			}
		}
		tv_meta.setText(title+"\n"+autors);
	}
	
}
