package com.example.exmword;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.aqi00.lib.dialog.FileSelectFragment;
import com.aqi00.lib.dialog.FileSelectFragment.FileSelectCallbacks;
import com.example.exmword.util.FileUtil;
import com.example.exmword.util.MD5Util;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PdfOpenglActivity extends Activity implements OnClickListener, FileSelectCallbacks {
	private final static String TAG = "PdfOpenglActivity";
	private CurlView cv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf_opengl);
		
		findViewById(R.id.btn_open).setOnClickListener(this);
		cv_content = (CurlView) findViewById(R.id.cv_content);
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
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + 
				"/Download/pdf/" + MD5Util.encrypByMd5(path);
		ArrayList<String> imgArray = new ArrayList<String>();
		int cv_width = cv_content.getMeasuredWidth();
		int cv_height = cv_content.getMeasuredHeight();
		ProgressDialog pd = ProgressDialog.show(this, "请稍候", "正在努力加载"+fileName);
		try {
			ParcelFileDescriptor fd = ParcelFileDescriptor.open(
					new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
			PdfRenderer pdfRenderer = new PdfRenderer(fd);
			Log.d(TAG, "page count="+pdfRenderer.getPageCount());
			for (int i=0; i<pdfRenderer.getPageCount(); i++) {
				String imgPath = String.format("%s/%d.jpg", dir, i);
				imgArray.add(imgPath);
				final PdfRenderer.Page page = pdfRenderer.openPage(i);
				if (i == 0) {
					cv_height = (int) (1.5f * cv_width * page.getHeight() / page.getWidth());
				}
				Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(),  
		                Bitmap.Config.ARGB_8888);
				page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
				FileUtil.saveBitmap(imgPath, bitmap);
				page.close();
			}
			pdfRenderer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pd!=null && pd.isShowing()) {
				pd.dismiss();
			}
		}

		LayoutParams params = cv_content.getLayoutParams();
		params.height = cv_height;
		cv_content.setLayoutParams(params);
		cv_content.setPageProvider(new PageProvider(imgArray));
		cv_content.setSizeChangedObserver(new SizeChangedObserver());
		cv_content.setCurrentIndex(0);
		cv_content.setBackgroundColor(Color.LTGRAY);
	}

	@Override
	public boolean isFileValid(String absolutePath, String fileName, Map<String, Object> map_param) {
		return true;
	}

//	@Override
//	public void onPause() {
//		super.onPause();
//		cv_content.onPause();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		cv_content.onResume();
//	}
//
//	@Override
//	public Object onRetainNonConfigurationInstance() {
//		return cv_content.getCurrentIndex();
//	}

	/**
	 * Bitmap provider.
	 */
	private class PageProvider implements CurlView.PageProvider {
		private ArrayList<String> mPathArray = new ArrayList<String>();
		
		public PageProvider(ArrayList<String> pathArray) {
			mPathArray = pathArray;
		}
		
		@Override
		public int getPageCount() {
			return mPathArray.size();
		}

		private Bitmap loadBitmap(int width, int height, int index) {
			Bitmap b = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			b.eraseColor(0xFFFFFFFF);
			Canvas c = new Canvas(b);
			//Drawable d = getResources().getDrawable(mBitmapIds[index]);
			Bitmap image = BitmapFactory.decodeFile(mPathArray.get(index));
			BitmapDrawable d = new BitmapDrawable(getResources(), image);

			int margin = 0;
			int border = 1;
			Rect r = new Rect(margin, margin, width - margin, height - margin);

			int imageWidth = r.width() - (border * 2);
			int imageHeight = imageWidth * d.getIntrinsicHeight()
					/ d.getIntrinsicWidth();
			if (imageHeight > r.height() - (border * 2)) {
				imageHeight = r.height() - (border * 2);
				imageWidth = imageHeight * d.getIntrinsicWidth()
						/ d.getIntrinsicHeight();
			}

			r.left += ((r.width() - imageWidth) / 2) - border;
			r.right = r.left + imageWidth + border + border;
			r.top += ((r.height() - imageHeight) / 2) - border;
			r.bottom = r.top + imageHeight + border + border;

			Paint p = new Paint();
			p.setColor(0xFFC0C0C0);
			c.drawRect(r, p);
			r.left += border;
			r.right -= border;
			r.top += border;
			r.bottom -= border;

			d.setBounds(r);
			d.draw(c);

			return b;
		}

		@Override
		public void updatePage(CurlPage page, int width, int height, int index) {
			Bitmap front = loadBitmap(width, height, index);
			page.setTexture(front, CurlPage.SIDE_BOTH);
		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			cv_content.setViewMode(CurlView.SHOW_ONE_PAGE);
			cv_content.setMargins(0f, 0f, 0f, 0f);
		}
	}

}
