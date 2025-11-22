package com.joe.epmediademo.Utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtil {
    private final static String TAG = "EditActivity";
    public static String savePath;

    /**
     * 获取缓存路径
     *
     * @return
     */
    public static String getSavePath() {
        return savePath;
    }

    public static void initRessFile(Context context) {
        savePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/EpMedia/";
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String ttfPath = FileUtil.getSavePath() + "msyh.ttf";
        File ttfFile = new File(ttfPath);
        if (!ttfFile.exists()) {
            copyFilesFassets(context, "Ress", savePath);
        }
    }

    /**
     * 从assets目录中复制文件到本地
     *
     * @param context Context
     * @param oldPath String  原文件路径
     * @param newPath String  复制后路径
     */
    public static void copyFilesFassets(Context context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                File file = new File(newPath);
                file.mkdirs();
                for (String fileName : fileNames) {
                    copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                InputStream is = context.getAssets().open(oldPath);
                File ff = new File(newPath);
                if (!ff.exists()) {
                    FileOutputStream fos = new FileOutputStream(ff);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将私有目录文件复制到公共Download目录
     * @param privateFile 私有目录中的源文件
     */
    public static Uri copyToPublicDownload(Context context, File privateFile) {
        // 目标文件名（可自定义，示例：保留原文件名+时间戳）
        String targetFileName = new SimpleDateFormat("yyyyMMdd_HHmmss_", Locale.getDefault()).format(new Date())
                + privateFile.getName();

        ContentResolver resolver = context.getContentResolver();
        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            // 1. 配置公共目录文件信息（通过MediaStore）
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DISPLAY_NAME, targetFileName); // 文件名
            // 设置MIME类型（根据文件类型修改，示例：视频）
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");

            // 2. 插入记录到MediaStore，获取目标文件的Uri
            Uri targetUri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (targetUri == null) {
                throw new IOException("无法创建公共目录文件Uri");
            }

            // 3. 打开输入流（私有文件）和输出流（公共目录文件）
            inputStream = new FileInputStream(privateFile);
            outputStream = resolver.openOutputStream(targetUri);
            if (outputStream == null) {
                throw new IOException("无法打开公共目录文件输出流");
            }

            // 4. 流对拷（复制文件内容）
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 5. 完成写入后，更新状态为已完成（Android 10+）
            values.clear();
            resolver.update(targetUri, values, null, null);
            return targetUri;
            //Toast.makeText(this, "文件已复制到Download目录：" + targetFileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
