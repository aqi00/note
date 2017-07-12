package com.example.exmvector;

import android.app.Activity;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class RubbishClearActivity extends Activity implements OnClickListener {
	private final static String TAG = "RubbishClearActivity";
	private ImageView iv_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rubbish_clear);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear.setImageResource(R.drawable.vector_rubbish_clear);
		findViewById(R.id.btn_clear).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_clear) {
			iv_clear.setVisibility(View.VISIBLE);
			iv_clear.setImageResource(R.drawable.animated_rubbish_clear);
			Drawable drawable = iv_clear.getDrawable();
			if (drawable instanceof AnimatedVectorDrawable) {
				((AnimatedVectorDrawable) drawable).start();
			}
		}
	}
	
}
