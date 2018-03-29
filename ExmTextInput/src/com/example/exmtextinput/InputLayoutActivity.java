package com.example.exmtextinput;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class InputLayoutActivity extends AppCompatActivity {
	private TextInputLayout til_user;
	private EditText et_user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_layout);
		til_user = (TextInputLayout) findViewById(R.id.til_user);
		et_user = (EditText) findViewById(R.id.et_user);

		til_user.setHintEnabled(true);
		til_user.setHint("请输入姓名");
		til_user.setHintAnimationEnabled(true);
		til_user.setErrorEnabled(true);
		til_user.setError("请输入姓名！");
		til_user.setCounterEnabled(true);
		til_user.setCounterMaxLength(10);
	}

}
