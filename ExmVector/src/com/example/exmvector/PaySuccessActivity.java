package com.example.exmvector;

import com.example.exmvector.util.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

@TargetApi(Build.VERSION_CODES.M)
public class PaySuccessActivity extends Activity implements OnClickListener {
	private Button btn_success;
	private Drawable mPayDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_success);
		
		btn_success = (Button) findViewById(R.id.btn_success);
		findViewById(R.id.btn_pay).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_pay) {
			btn_success.setVisibility(View.VISIBLE);
			startVectorAnim(R.drawable.animated_pay_circle);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				((AnimatedVectorDrawable) mPayDrawable)
					.registerAnimationCallback(new VectorAnimListener());
			} else {
				mHandler.postDelayed(mHookRunnable, 1000);
			}
		}
	}

	private void startVectorAnim(int drawableId) {
		mPayDrawable = getResources().getDrawable(drawableId, null);
		int dip_50 = Utils.dp2px(this, 50);
		mPayDrawable.setBounds(0, 0, dip_50, dip_50);
		btn_success.setCompoundDrawables(mPayDrawable, null, null, null);
		if (mPayDrawable instanceof AnimatedVectorDrawable) {
			((AnimatedVectorDrawable) mPayDrawable).start();
		}
	}
	
	private class VectorAnimListener extends Animatable2.AnimationCallback {
		@Override
		public void onAnimationStart(Drawable drawable) {
		}
		
		@Override
		public void onAnimationEnd(Drawable drawable) {
			startVectorAnim(R.drawable.animated_pay_success);
		}
	}
	
	private Handler mHandler = new Handler();
	private Runnable mHookRunnable = new Runnable() {
		@Override
		public void run() {
			startVectorAnim(R.drawable.animated_pay_success);
		}
	};

}
