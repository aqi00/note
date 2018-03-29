package com.example.exmtextinput;

import com.example.exmtextinput.dialog.DialogKeyboard;
import com.example.exmtextinput.dialog.DialogKeyboard.OnInputFinishListener;
import com.example.exmtextinput.widget.MyKeyboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class KeyboardActivity extends Activity implements
		OnClickListener, OnInputFinishListener {
	private MyKeyboard mk_account;
	private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        
        mk_account = (MyKeyboard) findViewById(R.id.mk_account);
        tv_result = (TextView) findViewById(R.id.tv_result);
		findViewById(R.id.btn_dialog).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_dialog) {
			DialogKeyboard dialog = new DialogKeyboard(this);
			dialog.setInputFinishListener(this);
			dialog.show();
		}
	}

	@Override
	public void onInputFinish(String password) {
		tv_result.setText("您输入的密码是"+password);
	}
	

}
