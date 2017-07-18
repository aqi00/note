package com.artifex.mupdf.widget;

import com.artifex.mupdf.LinkInfo;
import com.artifex.mupdf.bean.PatchInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * 解析pdf
 */
public abstract class PageView extends ViewGroup {
	private static final int HIGHLIGHT_COLOR = 0x805555FF;
	private static final int LINK_COLOR = 0x80FFCC88;
	private static final int BACKGROUND_COLOR = 0xFFFFFFFF;
	private static final int PROGRESS_DIALOG_DELAY = 200;
	private final Context mContext;
	protected int mPageNumber;
	private Point mParentSize;
	protected Point mSize;
	private LruCache<String, Bitmap> mLruCache;

	/** 缩放比例 */
	protected float mSourceScale;
	/** 缩放后的图片 */
	private ImageView mEntire;
	private Bitmap mEntireBm;
	/** 绘制缩放图片的异步任务 */
	private AsyncTask<Void, Void, LinkInfo[]> mDrawEntire;
	/** 补丁被创建位置的视图大小 */
	private Point mPatchViewSize;
	private Rect mPatchArea;
	private ImageView mPatch;
	/** 绘制补丁图片的异步任务 */
	private AsyncTask<PatchInfo, Void, PatchInfo> mDrawPatch;
	private RectF mSearchBoxes[];
	private LinkInfo mLinks[];
	private View mSearchView;
	private boolean mIsBlank;
	/** 使用硬件加速 */
	private boolean mUsingHardwareAcceleration;
	private boolean mHighlightLinks;
	private ProgressBar mBusyIndicator;
	private final Handler mHandler = new Handler();

	public PageView(Context c, Point parentSize) {
		super(c);
		mContext = c;
		mParentSize = parentSize;
		setBackgroundColor(BACKGROUND_COLOR);
		mUsingHardwareAcceleration = Build.VERSION.SDK_INT >= 14;
		int maxMemory = (int) Runtime.getRuntime().maxMemory();// 获取应用的最大可用内存
		int cacheMemory = (int) (maxMemory * 0.5);
		mLruCache = new LruCache<String, Bitmap>(cacheMemory) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}

	protected abstract void drawPage(Bitmap bm, int sizeX, int sizeY,
			int patchX, int patchY, int patchWidth, int patchHeight);

	protected abstract LinkInfo[] getLinkInfo();

	/**
	 * 置空
	 */
	public void blank(int page) {
		if (mDrawEntire != null) {
			mDrawEntire.cancel(true);
			mDrawEntire = null;
		}
		mIsBlank = true;
		mPageNumber = page;
		if (mSize == null) {
			mSize = mParentSize;
		}
		if (mEntire != null) {
			mEntire.setImageBitmap(null);
		}
		if (mPatch != null) {
			mPatch.setImageBitmap(null);
		}
		if (mBusyIndicator == null) {
			mBusyIndicator = new ProgressBar(mContext);
			mBusyIndicator.setIndeterminate(true); // 设置进度条明确
			// mBusyIndicator.setBackgroundResource(R.drawable.busy);
			addView(mBusyIndicator);
		}
	}

	public void setPage(final int page, PointF size) {
		if (mDrawEntire != null) {
			mDrawEntire.cancel(true);
			mDrawEntire = null;
		}
		mIsBlank = false;
		mPageNumber = page;
		if (mEntire == null) {
			mEntire = new OpaqueImageView(mContext);
			// 把图片按比例扩大/缩小到View的宽度，居中显示
			mEntire.setScaleType(ImageView.ScaleType.FIT_CENTER);
			addView(mEntire);
		}

		// 根据屏幕限制计算出缩放后的大小
		// 这是最小缩放大小
		mSourceScale = Math.min(mParentSize.x / size.x, mParentSize.y / size.y);
		Point newSize = new Point((int) (size.x * mSourceScale),
				(int) (size.y * mSourceScale));
		mSize = newSize;

		if (mUsingHardwareAcceleration) {
			// 当硬件加速，更新位图似乎被忽略，所以要重新创建
			mEntire.setImageBitmap(null);
			mEntireBm = null;
		}
		if (mEntireBm == null || mEntireBm.getWidth() != newSize.x
				|| mEntireBm.getHeight() != newSize.y) {
			mEntireBm = Bitmap.createBitmap(mSize.x, mSize.y,
					Bitmap.Config.ARGB_8888);
		}
		
		mDrawEntire = new AsyncTask<Void, Void, LinkInfo[]>() {
			protected LinkInfo[] doInBackground(Void... v) {
				mEntireBm = mLruCache.get(page + "");
				if (mEntireBm == null) {
					mEntireBm = Bitmap.createBitmap(mSize.x, mSize.y, Bitmap.Config.ARGB_8888);
					drawPage(mEntireBm, mSize.x, mSize.y, 0, 0, mSize.x, mSize.y);
				}
				return getLinkInfo();
			}

			protected void onPreExecute() {
				mEntire.setImageBitmap(null);
				if (mBusyIndicator == null) {
					mBusyIndicator = new ProgressBar(mContext);
					mBusyIndicator.setIndeterminate(true);
					// mBusyIndicator.setBackgroundResource(R.drawable.busy);
					addView(mBusyIndicator);
					mBusyIndicator.setVisibility(INVISIBLE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (mBusyIndicator != null) {
								mBusyIndicator.setVisibility(VISIBLE);
							}
						}
					}, PROGRESS_DIALOG_DELAY);
				}
			}

			protected void onPostExecute(LinkInfo[] v) {
				removeView(mBusyIndicator);
				mBusyIndicator = null;
				mEntire.setImageBitmap(mEntireBm);
				try {
					mLruCache.put(page + "", mEntireBm);
				} catch (Exception e) {
					e.printStackTrace();
				}

				mLinks = v;
				invalidate();
			}
		};

		mDrawEntire.execute();

		if (mSearchView == null) {
			mSearchView = new View(mContext) {
				@SuppressLint("DrawAllocation")
				@Override
				protected void onDraw(Canvas canvas) {
					super.onDraw(canvas);
					float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
					Paint paint = new Paint();

					if (!mIsBlank && mSearchBoxes != null) {
						paint.setColor(HIGHLIGHT_COLOR);
						for (RectF rect : mSearchBoxes)
							canvas.drawRect(rect.left * scale,
									rect.top * scale, rect.right * scale,
									rect.bottom * scale, paint);
					}

					if (!mIsBlank && mLinks != null && mHighlightLinks) {
						// Work out current total scale factor
						// from source to view
						paint.setColor(LINK_COLOR);
						for (RectF rect : mLinks)
							canvas.drawRect(rect.left * scale,
									rect.top * scale, rect.right * scale,
									rect.bottom * scale, paint);
					}
				}
			};

			addView(mSearchView);
		}
		requestLayout();
	}

	public void setSearchBoxes(RectF searchBoxes[]) {
		mSearchBoxes = searchBoxes;
		if (mSearchView != null) {
			mSearchView.invalidate();
		}
	}

	public void setLinkHighlighting(boolean f) {
		mHighlightLinks = f;
		if (mSearchView != null) {
			mSearchView.invalidate();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int x, y;
		if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			x = mSize.x;
		} else {
			x = View.MeasureSpec.getSize(widthMeasureSpec);
		}
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			y = mSize.y;
		} else {
			y = View.MeasureSpec.getSize(heightMeasureSpec);
		}
		setMeasuredDimension(x, y);
		if (mBusyIndicator != null) {
			int limit = Math.min(mParentSize.x, mParentSize.y) / 2;
			mBusyIndicator.measure(View.MeasureSpec.AT_MOST | limit,
					View.MeasureSpec.AT_MOST | limit);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int w = right - left;
		int h = bottom - top;
		if (mEntire != null) {
			mEntire.layout(0, 0, w, h);
		}
		if (mSearchView != null) {
			mSearchView.layout(0, 0, w, h);
		}

		if (mPatchViewSize != null) {
			if (mPatchViewSize.x != w || mPatchViewSize.y != h) {
				// Zoomed since patch was created
				mPatchViewSize = null;
				mPatchArea = null;
				if (mPatch != null) {
					mPatch.setImageBitmap(null);
				}
			} else {
				mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
			}
		}

		if (mBusyIndicator != null) {
			// 所以此方法得到的是View的内容占据的实际宽度
			int bw = mBusyIndicator.getMeasuredWidth();
			int bh = mBusyIndicator.getMeasuredHeight();
			mBusyIndicator.layout((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
		}
	}

	public void addHq() {
		Rect viewArea = new Rect(getLeft(), getTop(), getRight(), getBottom());
		if (viewArea.width() != mSize.x || viewArea.height() != mSize.y) {
			Point patchViewSize = new Point(viewArea.width(), viewArea.height());
			Rect patchArea = new Rect(0, 0, mParentSize.x, mParentSize.y);
			if (!patchArea.intersect(viewArea)) {
				return;
			}
			patchArea.offset(-viewArea.left, -viewArea.top);
			if (patchArea.equals(mPatchArea) && patchViewSize.equals(mPatchViewSize)) {
				return;
			}
			if (mDrawPatch != null) {
				mDrawPatch.cancel(true);
				mDrawPatch = null;
			}
			if (mPatch == null) {
				mPatch = new OpaqueImageView(mContext);
				mPatch.setScaleType(ImageView.ScaleType.FIT_CENTER);
				addView(mPatch);
				mSearchView.bringToFront(); // 把当前View提到画面图层的最上面来显示
			}
			Bitmap bm = Bitmap.createBitmap(patchArea.width(),
					patchArea.height(), Bitmap.Config.ARGB_8888);
			mDrawPatch = new AsyncTask<PatchInfo, Void, PatchInfo>() {
				protected PatchInfo doInBackground(PatchInfo... v) {
					drawPage(v[0].bm, v[0].patchViewSize.x,
							v[0].patchViewSize.y, v[0].patchArea.left,
							v[0].patchArea.top, v[0].patchArea.width(),
							v[0].patchArea.height());
					return v[0];
				}

				protected void onPostExecute(PatchInfo v) {
					mPatchViewSize = v.patchViewSize;
					mPatchArea = v.patchArea;
					mPatch.setImageBitmap(v.bm);
					mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
					invalidate();
				}
			};
			mDrawPatch.execute(new PatchInfo(bm, patchViewSize, patchArea));
		}
	}

	public void removeHq() {
		if (mDrawPatch != null) {
			mDrawPatch.cancel(true);
			mDrawPatch = null;
		}
		mPatchViewSize = null;
		mPatchArea = null;
		if (mPatch != null) {
			mPatch.setImageBitmap(null);
		}
	}

	public int getPage() {
		return mPageNumber;
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
}
