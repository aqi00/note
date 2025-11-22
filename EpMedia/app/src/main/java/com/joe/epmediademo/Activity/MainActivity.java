package com.joe.epmediademo.Activity;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.joe.epmediademo.R;
import com.joe.epmediademo.Utils.PermissionUtil;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.bt_one).setOnClickListener(v -> {
			if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.bt_one % 65536)) {
				startActivity(new Intent(MainActivity.this,EditActivity.class));
			}
		});
		findViewById(R.id.bt_more).setOnClickListener(v -> {
			if (PermissionUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, R.id.bt_more % 65536)) {
				startActivity(new Intent(MainActivity.this,MergeActivity.class));
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		// requestCode不能为负数，也不能大于2的16次方即65536
		if (requestCode == R.id.bt_one % 65536) {
			if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
				startActivity(new Intent(MainActivity.this,EditActivity.class));
			} else {
				//ToastUtil.show(this, "需要允许存储卡权限才能写入公共空间噢");
				Toast.makeText(this, "需要允许存储卡权限才能写入公共空间噢", Toast.LENGTH_SHORT).show();
			}
		} else if (requestCode == R.id.bt_more % 65536) {
			if (PermissionUtil.checkGrant(grantResults)) { // 用户选择了同意授权
				startActivity(new Intent(MainActivity.this,MergeActivity.class));
			} else {
				//ToastUtil.show(this, "需要允许存储卡权限才能写入公共空间噢");
				Toast.makeText(this, "需要允许存储卡权限才能写入公共空间噢", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
