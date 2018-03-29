package com.example.exmtextinput.widget;

import com.example.exmtextinput.R;
import com.example.exmtextinput.util.Utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PayPasswodInput extends RelativeLayout implements TextWatcher {
	private final static String TAG = "PayPasswodInput";
	private Context mContext;
	private EditText mEditText; // 文本编辑框，实际看不见
	private LinearLayout mShowLayout; // 真正显示着的文本区域
	private TextView[] mTextViews; // 分隔开的密码框
	private int mBorderColor = Color.GRAY; // 边框与分隔线颜色
	private int mPasswordColor = Color.BLACK; // 密码文字颜色
	private int mPasswordSize = 30; // 密码文字大小
	private int mPasswordLength = 6; // 密码长度
	private TransformationMethod mPasswordMethod; // 密码的显示方式
	private int mSplitWidth; // 分隔线的宽度

	public PayPasswodInput(Context context) {
		this(context, null);
	}

	public PayPasswodInput(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PayPasswodInput(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		mBorderColor = mContext.getResources().getColor(R.color.gray);
		mSplitWidth = Utils.dp2px(mContext, 1);
		mPasswordMethod = HideReturnsTransformationMethod.getInstance();
	}

	public void setPasswordStyle(int pwd_color, int pwd_size, int pwd_length, 
			boolean pwd_show, int pwd_type) {
		mPasswordColor = pwd_color;
		mPasswordSize = pwd_size;
		mPasswordLength = pwd_length;
		mPasswordMethod = pwd_show ? 
				HideReturnsTransformationMethod.getInstance() : //明文密码
					StarTransformationMethod.getInstance(pwd_type); //密文密码
		removeAllViews();
		showTextLayout();
	}

	private void showTextLayout() {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// 添加看不见的编辑框
		mEditText = new EditText(mContext);
		mEditText.setBackgroundResource(R.drawable.editext_selector);
		mEditText.setCursorVisible(false);
		mEditText.setTextSize(mPasswordSize);
		mEditText.setTextColor(Color.TRANSPARENT);
		// 设置最大长度
		mEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(mPasswordLength) });
		mEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD
				| InputType.TYPE_CLASS_NUMBER);
		mEditText.addTextChangedListener(this);
		addView(mEditText, layoutParams);

		// 添加可见的密码框布局
		mShowLayout = new LinearLayout(mContext);
		mShowLayout.setLayoutParams(layoutParams);
		mShowLayout.setGravity(Gravity.CENTER);
		mShowLayout.setOrientation(LinearLayout.HORIZONTAL);
		addView(mShowLayout);

		// 添加密码文本队列
		mTextViews = new TextView[mPasswordLength];
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
				0, LayoutParams.WRAP_CONTENT, 1);
		textParams.gravity = Gravity.CENTER;
		LinearLayout.LayoutParams splitParams = new LinearLayout.LayoutParams(
				mSplitWidth, LayoutParams.MATCH_PARENT);
		for (int i = 0; i < mTextViews.length; i++) {
			TextView textView = new TextView(mContext);
			textView.setLayoutParams(textParams);
			textView.setGravity(Gravity.CENTER);
			textView.setTextSize(mPasswordSize);
			textView.setTextColor(mPasswordColor);
			textView.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD
					| InputType.TYPE_CLASS_NUMBER);
			textView.setTransformationMethod(mPasswordMethod);
			textView.setPadding(0, Utils.dp2px(mContext, 5), 0, 0);
			mTextViews[i] = textView;
			mShowLayout.addView(mTextViews[i]);
			if (i < mTextViews.length - 1) {
				View view = new View(mContext);
				view.setBackgroundColor(mBorderColor);
				mShowLayout.addView(view, splitParams);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		Editable edit = mEditText.getText();
		Selection.setSelection(edit, edit.length());
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (s.length() > 0) {
			int length = s.length();
			for (int i = 0; i < mPasswordLength; i++) {
				if (i < length) {
					for (int j = 0; j < length; j++) {
						char ch = s.charAt(j);
						mTextViews[j].setText(String.valueOf(ch));
					}
				} else {
					mTextViews[i].setText("");
				}
			}
		} else {
			for (int i = 0; i < mPasswordLength; i++) {
				mTextViews[i].setText("");
			}
		}
		if (s.length() == mPasswordLength) {
			if (onPasswordFinishListener != null) {
				onPasswordFinishListener.onFinishPassword(s.toString().trim());
			}
		}
	}

	public EditText getEditText() {
		return mEditText;
	}

	private OnPasswordFinishListener onPasswordFinishListener;
	public void setOnPasswordFinishListener(OnPasswordFinishListener listener) {
		onPasswordFinishListener = listener;
		if (mEditText == null) {
			showTextLayout();
		}
	}

	public interface OnPasswordFinishListener {
		void onFinishPassword(String password);
	}

}
