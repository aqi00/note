package com.artifex.mupdf.bean;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

/**
 * 补丁信息
 */
public class PatchInfo {
	public Bitmap bm;
	public Point patchViewSize;
	public Rect patchArea;

	public PatchInfo(Bitmap aBm, Point aPatchViewSize, Rect aPatchArea) {
		bm = aBm;
		patchViewSize = aPatchViewSize;
		patchArea = aPatchArea;
	}
}
