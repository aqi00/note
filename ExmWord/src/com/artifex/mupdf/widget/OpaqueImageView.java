package com.artifex.mupdf.widget;

import android.content.Context;
import android.widget.ImageView;

/**
 * 把图像变透明，在重绘时优化
 */
public class OpaqueImageView extends ImageView {
	public OpaqueImageView(Context context) {
		super(context);
	}

	@Override
	public boolean isOpaque() {
		return true;
	}
}
