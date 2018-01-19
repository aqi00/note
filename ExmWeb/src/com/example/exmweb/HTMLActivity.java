package com.example.exmweb;

import com.example.exmweb.bean.MyObject;

import android.app.Activity;  
import android.os.Bundle;  
import android.os.Handler;  
import android.webkit.WebView;  
  
public class HTMLActivity extends Activity {  
    private WebView webView = null;  
    public Handler handler = new Handler();  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_html);  
          
        webView = (WebView)this.findViewById(R.id.webView);  
        //设置字符集编码  
        webView.getSettings().setDefaultTextEncodingName("UTF-8");  
        //开启JavaScript支持  
        webView.getSettings().setJavaScriptEnabled(true);  
        webView.addJavascriptInterface(new MyObject(this,handler), "myObject");  
        //加载assets目录下的文件  
        String url = "file:///android_asset/index.html";  
        webView.loadUrl(url);  
    }  
}  