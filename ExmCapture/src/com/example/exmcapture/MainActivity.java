package com.example.exmcapture;

import com.example.exmcapture.service.CaptureService;
import com.example.exmcapture.service.RecordService;
import com.example.exmcapture.util.AuthorityUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG = "MainActivity";
    private MediaProjectionManager mMpMgr;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private Intent mResultIntent = null;
    private int mResultCode = 0;
    private boolean isCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.btn_screen_capture).setOnClickListener(this);
            findViewById(R.id.btn_screen_record).setOnClickListener(this);
            mMpMgr = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            mResultIntent = MainApplication.getInstance().getResultIntent();
            mResultCode = MainApplication.getInstance().getResultCode();
        } else {
        	Toast.makeText(this, "截图和录屏功能需要Android5.0以上版本", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_screen_capture) {
            isCapture = true;
            startIntent();
            stopService(new Intent(this, RecordService.class ));
        } else if (view.getId() == R.id.btn_screen_record) {
            isCapture = false;
            startIntent();
            stopService(new Intent(this, CaptureService.class ));
        }
    }

    private void startIntent() {
        if (mResultIntent != null && mResultCode != 0) {
            startService(isCapture);
        } else {
        	//在YunOS上报错“android.content.ActivityNotFoundException: Unable to find explicit activity class {com.android.systemui/com.android.systemui.media.MediaProjectionPermissionActivity}; have you declared this activity in your AndroidManifest.xml?”
        	//即使添加了权限定义与Activity声明，也仍然报错
        	//怀疑是该操作系统为了安全把这个组件删除了
        	try {
                startActivityForResult(mMpMgr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        	} catch (Exception e) {
        		e.printStackTrace();
            	Toast.makeText(this, "当前系统不支持截图和录屏功能", Toast.LENGTH_SHORT).show();
        	}
        }
    }

    private void startService(boolean capture) {
        startService(new Intent(this, capture?CaptureService.class:RecordService.class));
        Log.d(TAG, "startService "+(capture?"CaptureService":"RecordService"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"onActivityResult requestCode="+requestCode+", resultCode="+resultCode);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
            	//AppOpsManager.OP_SYSTEM_ALERT_WINDOW是隐藏变量（值为24），不能直接引用
            	if (AuthorityUtil.checkOp(this, 24) != true) {
            		Toast.makeText(this, "截图和录屏功能需要开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            	} else {
                    Log.e(TAG,"get capture permission success! isCapture="+isCapture);
                    mResultCode = resultCode;
                    mResultIntent = data;
                    MainApplication.getInstance().setResultCode(resultCode);
                    MainApplication.getInstance().setResultIntent(data);
                    MainApplication.getInstance().setMpMgr(mMpMgr);
                    startService(isCapture);
            	}
            }
        }
    }

}
