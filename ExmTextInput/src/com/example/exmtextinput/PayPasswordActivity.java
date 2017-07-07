package com.example.exmtextinput;

import com.example.exmtextinput.widget.PayPasswodInput;
import com.example.exmtextinput.widget.PayPasswodInput.OnPasswordFinishListener;
import com.example.exmtextinput.widget.StarTransformationMethod;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

public class PayPasswordActivity extends AppCompatActivity 
		implements OnTouchListener, OnPasswordFinishListener {
	private EditText et_account;
	private PayPasswodInput ppi_password;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_password);
		et_account = (EditText) findViewById(R.id.et_account);
		ppi_password = (PayPasswodInput) findViewById(R.id.ppi_password);
		tv_result = (TextView) findViewById(R.id.tv_result);

		et_account.setOnTouchListener(this);
		ppi_password.setOnTouchListener(this);
		ppi_password.setPasswordStyle(Color.BLACK, 45, 6, 
				false, StarTransformationMethod.TYPE_STAR);
		ppi_password.setOnPasswordFinishListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.et_account) {
			et_account.setCursorVisible(true);
		} else if (v.getId() == R.id.ppi_password) {
			et_account.setCursorVisible(false);
			et_account.clearFocus();
			ppi_password.requestFocus();
		}
		return false;
	}

	@Override
	public void onFinishPassword(String password) {
		String result = String.format("您输入的密码是：%s", password);
		tv_result.setText(result);
	}

}
