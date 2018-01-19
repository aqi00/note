package com.example.exmweb;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("JavascriptInterface")
public class LocalActivity extends Activity implements OnClickListener,
		OnLongClickListener {

	private final static String TAG = "LocalActivity";
	private Context mContext;
	private EditText et_local;
	private Button btn_local;
	private WebView wv_local;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local);

		mContext = this;
		et_local = (EditText) findViewById(R.id.et_local);
		et_local.setOnLongClickListener(this);
		wv_local = (WebView) findViewById(R.id.wv_local);
		btn_local = (Button) findViewById(R.id.btn_local);
		btn_local.setOnClickListener(this);

		initWebViewSettings();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebViewSettings() {
		wv_local.getSettings().setJavaScriptEnabled(true);
		//内容的渲染需要webviewChromClient去实现，
		//设置webviewChromClient基类，解决js中alert不弹出的问题和其他内容渲染问题
		wv_local.setWebChromeClient(new WebChromeClient());
		//wv_local.loadUrl("file:///android_asset/example.html");
		wv_local.addJavascriptInterface(new Contact(), "contact");
		wv_local.loadUrl("file:///android_asset/sample.html");
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_local) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_local.getWindowToken(), 0);

			//wv_local.loadUrl("javascript:showMsg()");
			
			wv_local.evaluateJavascript("getMsg()", new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String value) {
					AlertDialog.Builder builder = new AlertDialog.Builder(LocalActivity.this);
					builder.setMessage(UnicodeToString(value)).create().show();
				}
			});
		}
	}

	@Override
	public boolean onLongClick(View v) {
		if (v.getId() == R.id.et_local) {
			et_local.setText("");
		}
		return true;
	}
	
	private final class Contact {
		// JavaScript调用此方法拨打电话
		@JavascriptInterface
		public void call(String phone) {
		}

		// Html调用此方法传递数据
		@JavascriptInterface
		public void showcontacts() {
			String json = "[{\"name\":\"zxx\", \"amount\":\"9999999\", \"phone\":\"18600012345\"}]";
			Log.d(TAG, "json="+json);
			// 调用JS中的方法
			wv_local.loadUrl("javascript:show('" + json + "')");
		}
		
		@JavascriptInterface
		public void showMsg(String msg) {  //如要返回值可把void改为String等等类型
			AlertDialog.Builder builder = new AlertDialog.Builder(LocalActivity.this);
			builder.setMessage(msg).create().show();
		}

		@JavascriptInterface
		public String showMsg2(String msg) {
			return "Android收到消息啦："+msg;
		}
	
	}

	public static String UnicodeToString(String str) {
		if (str != null && str.trim().length() > 0) {
			String un = str.trim();
			StringBuffer sb = new StringBuffer();
			int idx = un.indexOf("\\u");
			while (idx >= 0) {
				if (idx > 0) {
					sb.append(un.substring(0, idx));
				}

				String hex = un.substring(idx + 2, idx + 2 + 4);
				sb.append((char) Integer.parseInt(hex, 16));
				un = un.substring(idx + 2 + 4);
				idx = un.indexOf("\\u");
			}
			sb.append(un);
			return sb.toString();
		}
		return "";
	}

}
