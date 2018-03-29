package com.example.exmopengl;

import java.util.ArrayList;

import com.example.exmopengl.util.AssetsUtil;

import fi.harism.curl.CurlPage;
import fi.harism.curl.CurlView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;

public class GlTurnActivity extends Activity {
	private final static String TAG = "GlTurnActivity";
	private CurlView cv_content;
	private String[] imgArray = {"000.jpg", "001.jpg", "002.jpg", "003.jpg"};
	private int cv_height;
	private ArrayList<String> imgList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gl_turn);
		cv_content = (CurlView) findViewById(R.id.cv_content);
		copyImage();
		showImage();
	}
	
	private void copyImage() {
		cv_height = cv_content.getMeasuredHeight();
        String dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/";
        for (int i=0; i<imgArray.length; i++) {
        	String imgName = imgArray[i];
        	String imgPath = dir + imgName;
        	AssetsUtil.Assets2Sd(this, imgName, imgPath);
        	imgList.add(imgPath);
        	if (i == 0) {
        		Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        		cv_height = (int) (bitmap.getHeight() * 1.2);
        	}
        }
	}
	
	private void showImage() {
		LayoutParams params = cv_content.getLayoutParams();
		params.height = cv_height;
		cv_content.setLayoutParams(params);
		cv_content.setPageProvider(new PageProvider(imgList));
		cv_content.setSizeChangedObserver(new SizeChangedObserver());
		cv_content.setCurrentIndex(0);
		cv_content.setBackgroundColor(Color.LTGRAY);
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
