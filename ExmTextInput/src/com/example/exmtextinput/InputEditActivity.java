package com.example.exmtextinput;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

public class InputEditActivity extends AppCompatActivity {
	private TextInputLayout til_user;
	private TextInputEditText tiet_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_edit);
		til_user = (TextInputLayout) findViewById(R.id.til_user);
		tiet_user = (TextInputEditText) findViewById(R.id.tiet_user);

		til_user.setHint("请输入姓名");
	}

}
