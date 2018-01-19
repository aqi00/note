package com.example.exmweb;

import java.io.File;

import com.example.exmweb.util.DateUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class UploadActivity extends Activity {
	private final static String TAG = "UploadActivity";
	private WebView webView;
	private static ValueCallback<Uri> mUploadMessage;
	private static ValueCallback<Uri[]> mUploadMessageLollipop;
	private static final int FILE_SELECT_CODE = 1;
	private int mResultCode = Activity.RESULT_CANCELED;
	private String mCameraPhotoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		webView = (WebView) findViewById(R.id.webView);
		// 设置webView相关属性和方法
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webView.loadUrl("http://m.54php.cn/demo/h5_upload");
		webView.setWebViewClient(new MyWebViewClient(this));
		webView.setWebChromeClient(new MyWebChromeClient());
	}

	// Activity中返回上个页面使用onKeyDown
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private class MyWebChromeClient extends WebChromeClient {

		// Android 4.*
		public void openFileChooser(ValueCallback<Uri> uploadMsg,
				String acceptType, String capture) {
			Log.d(TAG, "openFileChooser 4.1");
			mUploadMessage = uploadMsg;
			openSelectDialog();
		}

		// Android 5.0+
		@Override
		public boolean onShowFileChooser(WebView webView,
				ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			Log.d(TAG, "openFileChooser 5.0+");
			mUploadMessageLollipop = filePathCallback;
			openSelectDialog();
			return true;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					UploadActivity.this)
					.setTitle("提示窗")
					.setMessage(message)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									result.confirm();
								}
							})
					// onJsAlert一般无需设置setNeutralButton，onJsConfirm才可能需要设置
					.setNeutralButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									result.cancel();
								}
							});
			builder.setCancelable(true).create().show();
			return true;
		}
	}

	private class MyWebViewClient extends WebViewClient {

		public MyWebViewClient(Context context) {
			super();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
	}

	private void openSelectDialog() {
		// 声明相机的拍照行为
		Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (photoIntent.resolveActivity(getPackageManager()) != null) {
			mCameraPhotoPath = "file:" + getExternalFilesDir(Environment.DIRECTORY_PICTURES)
							.toString() + "/" + DateUtil.getNowDateTime("") + ".jpg";
			Log.d(TAG, "photoFile=" + mCameraPhotoPath);
			photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(mCameraPhotoPath));
		}
		Intent[] intentArray = new Intent[] { photoIntent };
		// 声明相册的打开行为
		Intent selectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
		selectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
		selectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		selectionIntent.setType("image/*");
		// 弹出含相机和相册在内的列表对话框
		Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
		chooserIntent.putExtra(Intent.EXTRA_INTENT, selectionIntent);
		chooserIntent.putExtra(Intent.EXTRA_TITLE, "请拍照或选择图片");
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
		startActivityForResult(Intent.createChooser(chooserIntent, "选择图片"), 1);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode);
		if (requestCode != FILE_SELECT_CODE
				|| (mUploadMessage == null && mUploadMessageLollipop == null)) {
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		mResultCode = resultCode;
		Log.d(TAG, "mCameraPhotoPath=" + mCameraPhotoPath);
		if (resultCode == Activity.RESULT_OK) {
			uploadPhoto(resultCode, data);
		}
	}

	private void uploadPhoto(int resultCode, Intent data) {
		long fileSize = 0;
		try {
			String file_path = mCameraPhotoPath.replace("file:", "");
			File file = new File(file_path);
			fileSize = file.length();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (data != null || mCameraPhotoPath != null) {
			Integer count = 1;
			ClipData images = null;
			try {
				images = data.getClipData();
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (images == null && data != null && data.getDataString() != null) {
				count = data.getDataString().length();
			} else if (images != null) {
				count = images.getItemCount();
			}
			Uri[] results = new Uri[count];
			// Check that the response is a good one
			if (resultCode == Activity.RESULT_OK) {
				Log.d(TAG, "fileSize=" + fileSize);
				if (fileSize != 0) {
					// If there is not data, then we may have taken a photo
					if (mCameraPhotoPath != null) {
						results = new Uri[] { Uri.parse(mCameraPhotoPath) };
					}
				} else if (data.getClipData() == null) {
					results = new Uri[] { Uri.parse(data.getDataString()) };
				} else {
					for (int i = 0; i < images.getItemCount(); i++) {
						results[i] = images.getItemAt(i).getUri();
					}
				}
			}
			// 区分不同系统分别返回上传结果
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mUploadMessageLollipop.onReceiveValue(results);
				mUploadMessageLollipop = null;
			} else {
				mUploadMessage.onReceiveValue(results[0]);
				mUploadMessage = null;
			}
		}
	}

	// onActivityResult在onResume之前调用
	@Override
	protected void onResume() {
		super.onResume();
		// 取消选择时需要回调onReceiveValue，否则网页会挂住，不会再响应点击事件
		if (mResultCode == Activity.RESULT_CANCELED) {
			try {
				if (mUploadMessageLollipop != null) {
					mUploadMessageLollipop.onReceiveValue(null);
				}
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}