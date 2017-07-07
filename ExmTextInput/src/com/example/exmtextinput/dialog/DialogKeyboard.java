package com.example.exmtextinput.dialog;

import java.lang.reflect.Method;

import com.example.exmtextinput.R;
import com.example.exmtextinput.widget.KeyboardLayout;
import com.example.exmtextinput.widget.PayPasswodInput;
import com.example.exmtextinput.widget.PayPasswodInput.OnPasswordFinishListener;
import com.example.exmtextinput.widget.StarTransformationMethod;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;

public class DialogKeyboard implements OnTouchListener,
		OnPasswordFinishListener {
	private final static String TAG = "DialogTransferSetting";
	private Dialog dialog;
	private View view;
	private Context mContext;
	private EditText et_transfer;
	private PayPasswodInput ppi_password;
	private KeyboardLayout kl_input;

	public DialogKeyboard(Context context) {
		mContext = context;
		view = LayoutInflater.from(context).inflate(
				R.layout.dialog_keyboard, null);
		dialog = new Dialog(context, R.style.dialog_layout_buttom);

		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);

		et_transfer = (EditText) view.findViewById(R.id.et_transfer);
		ppi_password = (PayPasswodInput) view.findViewById(R.id.ppi_password);
		kl_input = (KeyboardLayout) view.findViewById(R.id.kl_input);

		disableShowSoftInput(et_transfer);
		et_transfer.requestFocus();
		et_transfer.requestFocusFromTouch();
		et_transfer.setOnTouchListener(this);
		kl_input.setInputWidget(et_transfer);

		ppi_password.setPasswordStyle(Color.BLACK, 25, 6, true, StarTransformationMethod.TYPE_STAR);
		ppi_password.setOnTouchListener(this);
		ppi_password.setOnPasswordFinishListener(this);
	}

	private void disableShowSoftInput(EditText et) {
		Class<EditText> cls = EditText.class;
		Method method;
		try {
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				et.setInputType(InputType.TYPE_NULL);
			} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
				method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(et, false);
			} else {
				method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
				method.setAccessible(true);
				method.invoke(et, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.et_transfer) {
			Log.d(TAG, "onTouch et_transfer");
			et_transfer.setCursorVisible(true);
			kl_input.setInputWidget(et_transfer);
		} else if (v.getId() == R.id.ppi_password) {
			Log.d(TAG, "onTouch ppi_password");
			et_transfer.setCursorVisible(false);
			et_transfer.clearFocus();
			ppi_password.requestFocus();
			kl_input.setInputWidget(ppi_password.getEditText());
		}
		return false;
	}

	@Override
	public void onFinishPassword(String password) {
		dismiss();
		if (onInputFinishListener != null) {
			onInputFinishListener.onInputFinish(password);
		}
	}

	private OnInputFinishListener onInputFinishListener;
	public void setInputFinishListener(OnInputFinishListener listener) {
		this.onInputFinishListener = listener;
	}

	public interface OnInputFinishListener {
		public void onInputFinish(String password);
	}

	public void show() {
		dialog.getWindow().setContentView(view);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.show();
	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public boolean isShowing() {
		if (dialog != null)
			return dialog.isShowing();
		return false;
	}

	public void setCancelable(boolean flag) {
		dialog.setCancelable(flag);
	}

}
