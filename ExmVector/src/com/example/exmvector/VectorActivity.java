package com.example.exmvector;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class VectorActivity extends Activity implements OnClickListener {
	private ImageView iv_vector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vector);
		iv_vector = (ImageView) findViewById(R.id.iv_vector);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById(R.id.btn_vector_heart).setOnClickListener(this);
			findViewById(R.id.btn_vector_face).setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_vector_heart) {
			iv_vector.setImageResource(R.drawable.vector_heart);
		} else if (v.getId() == R.id.btn_vector_face) {
			iv_vector.setImageResource(R.drawable.vector_face);
		}
	}

}
