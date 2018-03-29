package com.example.exmtablayout;

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
		findViewById(R.id.btn_tab_layout).setOnClickListener(this);
		findViewById(R.id.btn_tab_custom).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_tab_layout) {
			Intent intent = new Intent(this, TabLayoutActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_tab_custom) {
			Intent intent = new Intent(this, TabCustomActivity.class);
			startActivity(intent);
		}
	}
	
}
