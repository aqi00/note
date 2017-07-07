package com.example.exmtextinput;

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
		findViewById(R.id.btn_input_layout).setOnClickListener(this);
		findViewById(R.id.btn_input_edit).setOnClickListener(this);
		findViewById(R.id.btn_pay_password).setOnClickListener(this);
		findViewById(R.id.btn_keyboard).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_input_layout) {
			Intent intent = new Intent(this, InputLayoutActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_input_edit) {
			Intent intent = new Intent(this, InputEditActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_pay_password) {
			Intent intent = new Intent(this, PayPasswordActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_keyboard) {
			Intent intent = new Intent(this, KeyboardActivity.class);
			startActivity(intent);
		}
	}
	
}
