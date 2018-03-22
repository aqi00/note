package com.example.exmweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.btn_web).setOnClickListener(this);
		findViewById(R.id.btn_local).setOnClickListener(this);
		findViewById(R.id.btn_html).setOnClickListener(this);
		findViewById(R.id.btn_script).setOnClickListener(this);
		findViewById(R.id.btn_upload).setOnClickListener(this);
		findViewById(R.id.btn_record).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_web) {
			Intent intent = new Intent(this, WebActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_local) {
			Intent intent = new Intent(this, LocalActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_html) {
			Intent intent = new Intent(this, HTMLActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_script) {
			Intent intent = new Intent(this, ScriptActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_upload) {
			Intent intent = new Intent(this, UploadActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_record) {
			Intent intent = new Intent(this, RecordActivity.class);
			startActivity(intent);
		}
	}

}
