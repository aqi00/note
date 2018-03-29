package com.example.exmweb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WebActivity extends Activity 
	implements OnClickListener, OnLongClickListener {
	
	private final static String TAG = "WebActivity";
	private Context mContext;
	private EditText et_web_url;
	private Button btn_web_go;
	private WebView wv_web;
	private ProgressDialog m_pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		mContext = this;
		et_web_url = (EditText) findViewById(R.id.et_web_url);
		et_web_url.setOnLongClickListener(this);
		et_web_url.setText("blog.csdn.net/aqi00");
		wv_web = (WebView) findViewById(R.id.wv_web);
		btn_web_go = (Button) findViewById(R.id.btn_web_go);
		btn_web_go.setOnClickListener(this);
		
		initWebViewSettings();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebViewSettings() {
		WebSettings settings = wv_web.getSettings();
		//设置是否自动加载图片
		settings.setLoadsImagesAutomatically(true);
		//设置默认的文本编码
		settings.setDefaultTextEncodingName("utf-8");
		
		//设置是否支持Javascript
		settings.setJavaScriptEnabled(true);
		//设置是否允许js自动打开新窗口（window.open()）
		settings.setJavaScriptCanOpenWindowsAutomatically(false);
		
		// 设置是否支持缩放 
		settings.setSupportZoom(true);
		// 设置是否出现缩放工具 
		settings.setBuiltInZoomControls(true);
		//当容器超过页面大小时，是否放大页面大小到容器宽度
		settings.setUseWideViewPort(true);
		//当页面超过容器大小时，是否缩小页面尺寸到页面宽度
		settings.setLoadWithOverviewMode(true);
		//设置自适应屏幕。4.2.2及之前版本自适应时可能会出现表格错乱的情况
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		//设置是否启用本地存储
		settings.setDomStorageEnabled(true);
		//优先使用缓存
		//settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		//设置是否使用缓存
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		//设置是否启用app缓存
		settings.setAppCacheEnabled(true);
		//设置app缓存文件的路径
		settings.setAppCachePath("");
		//设置是否允许访问文件，如WebView访问sd卡的文件。不过assets与res文件不受此限制，仍然可以通过“file:///android_asset”和“file:///android_res”访问
		settings.setAllowFileAccess(true);
		//设置是否启用数据库
		settings.setDatabaseEnabled(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_web_go) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_web_url.getWindowToken(), 0);
			String url = "http://" + et_web_url.getText().toString();
			Log.d(TAG, "url="+url);
			//禁止执行远程代码。在4.0至4.2的Android系统上，Webview自己增加了searchBoxJavaBredge_，可能让黑客利用导致远程代码执行
			wv_web.removeJavascriptInterface("searchBoxJavaBredge_");
			wv_web.addJavascriptInterface(new MyJavaScript(), "injectedObject");
			
			//加载远程URL
			wv_web.loadUrl(url);
			//加载本地网页
			//wv_web.loadUrl("file:///android_asset/example.html");
			wv_web.setWebViewClient(mWebViewClient);
			wv_web.setWebChromeClient(mWebChrome);
			wv_web.setDownloadListener(mDownloadListener);
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.et_web_url) {
			et_web_url.setText("");
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		if (wv_web.canGoBack()) {
			wv_web.goBack();
			return;
        } else {
        	finish();
        }
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv_web.canGoBack()) {
//			wv_web.goBack();
//			return true;
//		} else {
//			return false;
//		}
//	}

	private WebViewClient mWebViewClient = new WebViewClient() {

		@Override
		public void onReceivedSslError(WebView view,
				android.webkit.SslErrorHandler handler,
				android.net.http.SslError error) {
			handler.proceed();
		};

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d(TAG, "onPageStarted:" + url);
			if (m_pd == null || m_pd.isShowing() == false) {
				m_pd = new ProgressDialog(mContext);
				m_pd.setTitle("稍等");
				m_pd.setMessage("页面加载中……");
				m_pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				m_pd.show();
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.d(TAG, "onPageFinished:" + url);
			if (m_pd != null && m_pd.isShowing() == true) {
				m_pd.dismiss();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d(TAG, "onReceivedError: url=" + failingUrl+", errorCode="+errorCode+", description="+description);
			if (m_pd != null && m_pd.isShowing() == true) {
				m_pd.dismiss();
			}
			Toast.makeText(mContext, "页面加载失败，请稍候再试",Toast.LENGTH_LONG).show();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

	};

	private WebChromeClient mWebChrome = new WebChromeClient() {
		private String mTitle = "";

		@Override
	    public void onReceivedTitle(WebView view, String title) {
			mTitle = title;
		}
		
		@Override
		public void onProgressChanged(WebView view, int progress) {
			if (m_pd != null && m_pd.isShowing() == true) {
				m_pd.setProgress(progress);
			}
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
					.setTitle(mTitle)
					.setMessage(message)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									result.confirm();
								}
							})
					//onJsAlert一般无需设置setNeutralButton，onJsConfirm才可能需要设置
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

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}
	};

	private DownloadListener mDownloadListener = new DownloadListener() {
		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition,
				String mimetype, long contentLength) {
			//此处操作文件下载
		}
	};

	class MyJavaScript {
		@JavascriptInterface
		public String toString() {
			return "injectedObject";
		}
	}

}
