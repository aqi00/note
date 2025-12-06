package com.example.cj.videoeditor.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.cj.videoeditor.R;
import com.example.cj.videoeditor.utils.PermissionUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button recordBtn = (Button) findViewById(R.id.record_activity);
        Button selectBtn = (Button) findViewById(R.id.select_activity);
        Button audioBtn = (Button) findViewById(R.id.audio_activity);
        Button videoBtn = (Button) findViewById(R.id.video_connect);

        recordBtn.setOnClickListener(this);
        selectBtn.setOnClickListener(this);
        audioBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_activity:
                //startActivity(new Intent(MainActivity.this , RecordedActivity.class));
                if (PermissionUtil.checkPermission(this, new String[] {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, (int) v.getId() % 65536)) {
                    startActivity(new Intent(MainActivity.this , RecordedActivity.class));
                }
                break;
            case R.id.select_activity:
                //VideoSelectActivity.openActivity(this);
                if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, (int) v.getId() % 65536)) {
                    VideoSelectActivity.openActivity(this);
                }
                break;
            case R.id.audio_activity:
                startActivity(new Intent(MainActivity.this , AudioEditorActivity.class));
                break;
            case R.id.video_connect:
//                Toast.makeText(this,"该功能还未完成！！！",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this , VideoConnectActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == R.id.record_activity % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                startActivity(new Intent(MainActivity.this , RecordedActivity.class));
            } else {
                Toast.makeText(this, "需要允许摄像头和录音权限才能录像噢", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == R.id.select_activity % 65536) {
            if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
                VideoSelectActivity.openActivity(this);
            } else {
                Toast.makeText(this, "需要允许存储卡权限才能写入公共空间噢", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
