package com.example.exmopengl;

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

		findViewById(R.id.btn_gl_cube).setOnClickListener(this);
		findViewById(R.id.btn_gl_ball).setOnClickListener(this);
		findViewById(R.id.btn_gl_globe).setOnClickListener(this);
		findViewById(R.id.btn_gl_turn).setOnClickListener(this);
		findViewById(R.id.btn_gl_panorama).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_gl_cube) {
			Intent intent = new Intent(this, GlCubeActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_gl_ball) {
			Intent intent = new Intent(this, GlBallActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_gl_globe) {
			Intent intent = new Intent(this, GlGlobeActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_gl_turn) {
			Intent intent = new Intent(this, GlTurnActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_gl_panorama) {
			Intent intent = new Intent(this, GlPanoramaActivity.class);
			startActivity(intent);
		}
	}

}
