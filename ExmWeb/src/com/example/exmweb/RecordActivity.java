package com.example.exmweb;

import java.io.File;

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
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.exmweb.util.DateUtil;
import com.example.exmweb.util.MediaUtility;

public class RecordActivity extends Activity {
	private final static String TAG = "RecordActivity";
	private WebView webView;
	private static ValueCallback<Uri> mUploadMessage;
	private static ValueCallback<Uri[]> mUploadMessageLollipop;
	private static final int FILE_SELECT_CODE = 1;
	private int mResultCode = Activity.RESULT_CANCELED;
	private String mCameraPhotoPath = null;
	private String mCameraVideoPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		setContentView(R.layout.activity_record);
		webView = (WebView) findViewById(R.id.webView);
		initSetting();
		// 设置webView相关属性和方法
		//webView.loadUrl("http://m.54php.cn/demo/h5_upload");
		//webView.loadUrl("file:///android_asset/upload.html");
		webView.loadUrl("file:///android_asset/video/index.html");
		//webView.loadUrl("http://172.16.40.50:8080/FNllms/bmsh/test/index31/500/18705055291");
		//webView.loadUrl("https://www.baidu.com");
		webView.setWebViewClient(new MyWebViewClient(this));
		webView.setWebChromeClient(new MyWebChromeClient());
	}
	
	private void initSetting() {
//		WebSettings webSettings = webView.getSettings();
//		webSettings.setJavaScriptEnabled(true);
//		webSettings.setBuiltInZoomControls(true);
//		webSettings.setDomStorageEnabled(true);
//		webSettings.setPluginState(PluginState.ON);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //webSettings.setUseWideViewPort(true);
        
        webSettings.setDomStorageEnabled(true);
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
			recordVideo();
		}

		// Android 5.0+
		@Override
		public boolean onShowFileChooser(WebView webView,
				ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
			Log.d(TAG, "openFileChooser 5.0+");
			mUploadMessageLollipop = filePathCallback;
			recordVideo();
			return true;
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					RecordActivity.this)
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
			Log.d(TAG, "url="+url);
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

	private final static int VIDEO_REQUEST = 120;
	private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        //限制时长
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
        //开启摄像机
        startActivityForResult(intent, VIDEO_REQUEST);
    }

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	Log.d(TAG, "onActivityResult requestCode="+requestCode);
        if (requestCode == VIDEO_REQUEST) {
            if (null == mUploadMessage && null == mUploadMessageLollipop) {
            	Log.d(TAG, "onActivityResult null");
            	return;
            }
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
        	Log.d(TAG, "onActivityResult path="+result.getPath());
            if (mUploadMessageLollipop != null) {
                if (resultCode == RESULT_OK) {
                	Log.d(TAG, "onActivityResult 1");
                    mUploadMessageLollipop.onReceiveValue(new Uri[]{result});
                    mUploadMessageLollipop = null;
                } else {
                	Log.d(TAG, "onActivityResult 2");
                    mUploadMessageLollipop.onReceiveValue(new Uri[]{});
                    mUploadMessageLollipop = null;
                }
            } else if (mUploadMessage != null) {
                if (resultCode == RESULT_OK) {
//                    String path = MediaUtility.getPath(this, result);
//                    Uri uri = Uri.fromFile(new File(path));
//                	Log.d(TAG, "onActivityResult 3 path="+path);
//                    mUploadMessage.onReceiveValue(uri);
                    
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                } else {
                	Log.d(TAG, "onActivityResult 4");
                    mUploadMessage.onReceiveValue(Uri.EMPTY);
                    mUploadMessage = null;
                }
            }
        	Log.d(TAG, "onActivityResult 5");
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
