package com.example.exmtabfragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.btn_tab_simple).setOnClickListener(this);
		findViewById(R.id.btn_tab_custom).setOnClickListener(this);
		findViewById(R.id.btn_tab_sliding).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_tab_simple) {
			Intent intent = new Intent(this, TabSimpleActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_tab_custom) {
			Intent intent = new Intent(this, TabCustomActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_tab_sliding) {
			Intent intent = new Intent(this, TabSlidingActivity.class);
			startActivity(intent);
		}
	}

}
