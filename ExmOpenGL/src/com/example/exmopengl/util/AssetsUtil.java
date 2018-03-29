package com.example.exmopengl.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

public class AssetsUtil {

    public static void Assets2Sd(Context context, String assetFile, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                copyAssetToStorage(context, assetFile, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssetToStorage(Context context, String fileAssetPath, String filePath) throws IOException {
        File dir = new File(filePath.substring(0, filePath.lastIndexOf("/")));
        if (!dir.exists()) {
            dir.mkdir();
        }
        InputStream is = context.getAssets().open(fileAssetPath);
        OutputStream os = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int length = is.read(buffer);
        while (length > 0) {
            os.write(buffer, 0, length);
            length = is.read(buffer);
        }
        os.flush();
        os.close();
        is.close();
    }
}
