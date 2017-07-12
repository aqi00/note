package com.example.exmvector;

import android.app.Activity;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class VectorSmileActivity extends Activity implements OnClickListener {
	private ImageView iv_vector_smile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vector_smile);
		iv_vector_smile = (ImageView) findViewById(R.id.iv_vector_smile);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById(R.id.btn_vector_smile).setOnClickListener(this);
			findViewById(R.id.btn_eye_smile).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_vector_smile) {
			startVectorAnim(R.drawable.animated_vector_smile);
		} else if (v.getId() == R.id.btn_eye_smile) {
			startVectorAnim(R.drawable.animated_vector_smile_eye);
		}
	}
	
	private void startVectorAnim(int drawableId) {
		iv_vector_smile.setImageResource(drawableId);
		Drawable drawable = iv_vector_smile.getDrawable();
		if (drawable instanceof AnimatedVectorDrawable) {
			((AnimatedVectorDrawable) drawable).start();
		}
	}
	
}
