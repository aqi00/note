package com.example.exmvector;

import com.example.exmvector.widget.CircleHookView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class VectorHookActivity extends Activity implements OnClickListener {
	private ImageView iv_vector_hook;
	private CircleHookView chv_hook;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vector_hook);
		iv_vector_hook = (ImageView) findViewById(R.id.iv_vector_hook);
		chv_hook = (CircleHookView) findViewById(R.id.chv_hook);
		findViewById(R.id.btn_circle_view).setOnClickListener(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById(R.id.btn_vector_pay).setOnClickListener(this);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_circle_view) {
			setVectorShow(false);
			chv_hook.render();
		} else if (v.getId() == R.id.btn_vector_pay) {
			setVectorShow(true);
			startVectorAnim(R.drawable.animated_vector_pay_circle);
			try {
				((AnimatedVectorDrawable) iv_vector_hook.getDrawable())
					.registerAnimationCallback(new VectorAnimListener());
			} catch (Throwable e) { //运行时异常需捕获Throwable，不能只捕获Exception
				e.printStackTrace();
				mHandler.postDelayed(mHook, 1000);
			}
		}
	}
	
	private void setVectorShow(boolean isShown) {
		iv_vector_hook.setVisibility(isShown?View.VISIBLE:View.INVISIBLE);
		chv_hook.setVisibility(isShown?View.INVISIBLE:View.VISIBLE);
	}

	private void startVectorAnim(int drawableId) {
		iv_vector_hook.setImageResource(drawableId);
		Drawable drawable = iv_vector_hook.getDrawable();
		if (drawable instanceof AnimatedVectorDrawable) {
			((AnimatedVectorDrawable) drawable).start();
		}
	}
	
	@SuppressLint("NewApi")
	private class VectorAnimListener extends Animatable2.AnimationCallback {
		@Override
		public void onAnimationStart(Drawable drawable) {
		}
		
		@Override
		public void onAnimationEnd(Drawable drawable) {
			startVectorAnim(R.drawable.animated_vector_pay_success);
		}
	}
	
	private Handler mHandler = new Handler();
	private Runnable mHook = new Runnable() {
		@Override
		public void run() {
			startVectorAnim(R.drawable.animated_vector_pay_success);
		}
	};

}
