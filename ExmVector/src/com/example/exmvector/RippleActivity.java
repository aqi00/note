package com.example.exmvector;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class RippleActivity extends Activity implements OnClickListener {
	private ImageButton btn_gradient_button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ripple);
		findViewById(R.id.btn_ripple_simple).setOnClickListener(this);
		findViewById(R.id.btn_ripple_mask).setOnClickListener(this);
		findViewById(R.id.btn_ripple_button).setOnClickListener(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			findViewById(R.id.btn_ripple_simple).setBackgroundResource(R.drawable.ripple_simple);
			findViewById(R.id.btn_ripple_mask).setBackgroundResource(R.drawable.ripple_mask);
			findViewById(R.id.btn_ripple_button).setBackgroundResource(R.drawable.ripple_button);
		}
		findViewById(R.id.btn_ripple_view).setOnClickListener(this);
		findViewById(R.id.btn_ripple_gradient).setOnClickListener(this);
		btn_gradient_button = (ImageButton) findViewById(R.id.btn_gradient_button);
		btn_gradient_button.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// item节点加不加android:id="@android:id/mask"都会限制水波边界
		// 区别在于，加了mask表示本图形只是做为边界限制，实际并不显示
		if (v.getId() == R.id.btn_ripple_simple) {
		} else if (v.getId() == R.id.btn_ripple_mask) {
		} else if (v.getId() == R.id.btn_ripple_button) {
		} else if (v.getId() == R.id.btn_ripple_view) {
			Message msg = mHandler.obtainMessage();
			msg.what = 0;
			msg.obj = "您点击了自定义水波动画按钮";
			mHandler.sendMessageDelayed(msg, 500); // 等待水波动画400毫秒后再响应点击事件
		} else if (v.getId() == R.id.btn_ripple_gradient) {
			Message msg = mHandler.obtainMessage();
			msg.what = 0;
			msg.obj = "您点击了会渐变的水波动画按钮";
			mHandler.sendMessageDelayed(msg, 500); // 等待水波动画400毫秒后再响应点击事件
		} else if (v.getId() == R.id.btn_gradient_button) {
			GradientDrawable gradient = new GradientDrawable(
					GradientDrawable.Orientation.LEFT_RIGHT,
					new int[] {0xffff0000, 0xffffaaaa});
			gradient.setShape(GradientDrawable.OVAL);
			gradient.setSize(100, 100);
			gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
			gradient.setGradientCenter(0.2f, 0.2f);
			gradient.setGradientRadius(50);
			btn_gradient_button.setBackground(gradient);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				Toast.makeText(RippleActivity.this, (String) (msg.obj),
						Toast.LENGTH_LONG).show();
			}
		}
	};

}
