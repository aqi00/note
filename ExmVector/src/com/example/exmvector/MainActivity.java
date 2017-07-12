package com.example.exmvector;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		findViewById(R.id.btn_ripple).setOnClickListener(this);
		findViewById(R.id.btn_vector).setOnClickListener(this);
		findViewById(R.id.btn_vector_smile).setOnClickListener(this);
		findViewById(R.id.btn_vector_hook).setOnClickListener(this);
		findViewById(R.id.btn_pay_success).setOnClickListener(this);
		findViewById(R.id.btn_vector_clear).setOnClickListener(this);
		findViewById(R.id.btn_rubbish_clear).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_ripple) {
			Intent intent = new Intent(this, RippleActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_vector) {
			Intent intent = new Intent(this, VectorActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_vector_smile) {
			Intent intent = new Intent(this, VectorSmileActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_vector_hook) {
			Intent intent = new Intent(this, VectorHookActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_pay_success) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, PaySuccessActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		} else if (v.getId() == R.id.btn_vector_clear) {
			Intent intent = new Intent(this, VectorClearActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_rubbish_clear) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Intent intent = new Intent(this, RubbishClearActivity.class);
				startActivity(intent);
			} else {
				Toast.makeText(this, "该功能需要Android5.0以上支持", 
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
