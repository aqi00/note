package com.example.exmcapture.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;

public class FileUtil {

	public static void createDir(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public static File createFile(String path, String file_name) {
		createDir(path);
		File file = new File(path, file_name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static Bitmap getBitmap(Image image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Image.Plane[] planes = image.getPlanes();
		ByteBuffer buffer = planes[0].getBuffer();
		int pixelStride = planes[0].getPixelStride();
		int rowStride = planes[0].getRowStride();
		int rowPadding = rowStride - pixelStride * width;
		Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride,
				height, Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(buffer);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
		image.close();
		return bitmap;
	}

	public static void saveBitmap(String path, Bitmap bitmap, String format, int quality) {
		Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
		if (format.toUpperCase(Locale.getDefault()).equals("PNG") == true) {
			compressFormat = Bitmap.CompressFormat.PNG;
		}
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
			bitmap.compress(compressFormat, quality, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
