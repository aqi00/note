package com.artifex.mupdf;

import android.graphics.RectF;

/**
 * 这个类包含一个矩形的四个单精度浮点坐标
 */
public class LinkInfo extends RectF {
	public static int pageNumber;

	public LinkInfo(float l, float t, float r, float b, int p) {
		super(l, t, r, b);
		pageNumber = p;
	}
}