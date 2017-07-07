package com.example.exmtextinput.widget;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.exmtextinput.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Build;
import android.os.SystemClock;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class MyKeyboard extends EditText implements OnKeyboardActionListener {
	private KeyboardView mKeyboardView;
	private Keyboard mKeyboard;
	private Window mWindow;
	private View mDecorView;
	private View mContentView;
	private PopupWindow mKeyboardWindow;

	private boolean needcustomkeyboard = true; // 是否启用自定义键盘
	private boolean randomkeys = false; // 数字按键是否随机
	private int scrolldis = 0; // 输入框在键盘被弹出时，要被推上去的距离
	public static int screenw = -1;// 未知宽高
	public static int screenh = -1;
	public static int screenh_nonavbar = -1; // 不包含导航栏的高度
	public static int real_scontenth = -1; // 实际内容高度， 计算公式:屏幕高度-导航栏高度-电量栏高度
	public static float density = 1.0f;
	public static int densityDpi = 160;

	public MyKeyboard(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyKeyboard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttributes(context);
		initKeyboard(context, attrs);
	}

	private void initKeyboard(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.keyboard);
		if (a.hasValue(R.styleable.keyboard_xml)) {
			needcustomkeyboard = true;
			int xmlid = a.getResourceId(R.styleable.keyboard_xml, 0);
			mKeyboard = new Keyboard(context, xmlid);
			mKeyboardView = (KeyboardView) LayoutInflater.from(context)
					.inflate(R.layout.keyboardview, null);
			if (a.hasValue(R.styleable.keyboard_randomkeys)) {
				boolean random = a.getBoolean(R.styleable.keyboard_randomkeys, false);
				randomkeys = random;
				if (random) {
					randomdigkey(mKeyboard);
				}
			}
			mKeyboardView.setKeyboard(mKeyboard);
			mKeyboardView.setEnabled(true);
			mKeyboardView.setPreviewEnabled(false);
			mKeyboardView.setOnKeyboardActionListener(this);
			mKeyboardWindow = new PopupWindow(mKeyboardView,
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			mKeyboardWindow.setAnimationStyle(R.style.AnimationFade);
			mKeyboardWindow.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					if (scrolldis > 0) {
						int temp = scrolldis;
						scrolldis = 0;
						if (null != mContentView) {
							mContentView.scrollBy(0, -temp);
						}
					}
				}
			});
		} else {
			needcustomkeyboard = false;
		}
		a.recycle();
	}

	public void showKeyboard() {
		if (null != mKeyboardWindow) {
			if (!mKeyboardWindow.isShowing()) {
				if (randomkeys) {
					randomdigkey(mKeyboard);
				}
				mKeyboardView.setKeyboard(mKeyboard);
				mKeyboardWindow.showAtLocation(this.mDecorView, Gravity.BOTTOM, 0, 0);
				// TODO 华为Android7 手机出现自定义键盘飘逸。屏蔽词代码后正常
				// mKeyboardWindow.update();

				if (null != mDecorView && null != mContentView) {
					int[] pos = new int[2];
					getLocationOnScreen(pos);
					float height = dpToPx(getContext(), 240);
					Rect outRect = new Rect();
					mDecorView.getWindowVisibleDisplayFrame(outRect);
					int screen = real_scontenth;
					scrolldis = (int) ((pos[1] + getMeasuredHeight() - outRect.top) - (screen - height));
					if (scrolldis > 0) {
						mContentView.scrollBy(0, scrolldis);
					}
				}
			}
		}
	}

	public void hideKeyboard() {
		if (null != mKeyboardWindow) {
			if (mKeyboardWindow.isShowing()) {
				mKeyboardWindow.dismiss();
			}
		}
	}

	public void hideSysInput() {
		if (this.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		requestFocus();
		requestFocusFromTouch();
		if (needcustomkeyboard) {
			hideSysInput();
			showKeyboard();
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != mKeyboardWindow) {
				if (mKeyboardWindow.isShowing()) {
					mKeyboardWindow.dismiss();
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mWindow = ((Activity) getContext()).getWindow();
		mDecorView = mWindow.getDecorView();
		mContentView = mWindow.findViewById(Window.ID_ANDROID_CONTENT);
		hideSysInput();
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		hideKeyboard();
		mKeyboardWindow = null;
		mKeyboardView = null;
		mKeyboard = null;
		mDecorView = null;
		mContentView = null;
		mWindow = null;
	}

	@Override
	public void onPress(int primaryCode) {
	}

	@Override
	public void onRelease(int primaryCode) {
	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		Editable editable = this.getText();
		int start = this.getSelectionStart();
		if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 隐藏键盘
			hideKeyboard();
		} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
			if (editable != null && editable.length() > 0) {
				if (start > 0) {
					editable.delete(start - 1, start);
				}
			}
		} else if (0x0 <= primaryCode && primaryCode <= 0x7f) {
			// 可以直接输入的字符(如0-9,.)，他们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
			editable.insert(start, Character.toString((char) primaryCode));
		} else if (primaryCode > 0x7f) {
			Key mkey = getKeyByKeyCode(primaryCode);
			// 可以直接输入的字符(如0-9,.)，他们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
			editable.insert(start, mkey.label);
		} else {
			// 其他一些暂未开放的键指令，如next到下一个输入框等指令
		}
	}

	@Override
	public void onText(CharSequence text) {
	}

	@Override
	public void swipeLeft() {
	}

	@Override
	public void swipeRight() {
	}

	@Override
	public void swipeDown() {
	}

	@Override
	public void swipeUp() {
	}

	private Key getKeyByKeyCode(int keyCode) {
		if (null != mKeyboard) {
			List<Key> mKeys = mKeyboard.getKeys();
			for (int i = 0, size = mKeys.size(); i < size; i++) {
				Key mKey = mKeys.get(i);

				int codes[] = mKey.codes;
				if (codes[0] == keyCode) {
					return mKey;
				}
			}
		}
		return null;
	}

	private void initAttributes(Context context) {
		initScreenParams(context);
		this.setLongClickable(false);
		this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		removeCopyAbility();

		if (this.getText() != null) {
			this.setSelection(this.getText().length());
		}
		this.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard();
				}
			}
		});
	}

	private void removeCopyAbility() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			this.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
				
				@Override
				public boolean onCreateActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
					return false;
				}

				@Override
				public void onDestroyActionMode(ActionMode mode) {
				}

				@Override
				public boolean onActionItemClicked(ActionMode mode,
						MenuItem item) {
					return false;
				}
			});
		}
	}

	private boolean isNumber(String str) {
		String wordstr = "0123456789";
		if (wordstr.indexOf(str) > -1) {
			return true;
		}
		return false;
	}

	private void randomdigkey(Keyboard mKeyboard) {
		if (mKeyboard == null) {
			return;
		}

		List<Key> keyList = mKeyboard.getKeys();
		// 查找出0-9的数字键
		List<Key> newkeyList = new ArrayList<Key>();
		for (int i = 0, size = keyList.size(); i < size; i++) {
			Key key = keyList.get(i);
			CharSequence label = key.label;
			if (label != null && isNumber(label.toString())) {
				newkeyList.add(key);
			}
		}

		int count = newkeyList.size();
		List<KeyModel> resultList = new ArrayList<KeyModel>();
		LinkedList<KeyModel> temp = new LinkedList<KeyModel>();

		for (int i = 0; i < count; i++) {
			temp.add(new KeyModel(48 + i, i + ""));
		}

		Random rand = new SecureRandom();
		rand.setSeed(SystemClock.currentThreadTimeMillis());

		for (int i = 0; i < count; i++) {
			int num = rand.nextInt(count - i);
			KeyModel model = temp.get(num);
			resultList.add(new KeyModel(model.getCode(), model.getLable()));
			temp.remove(num);
		}

		for (int i = 0, size = newkeyList.size(); i < size; i++) {
			Key newKey = newkeyList.get(i);
			KeyModel resultmodle = resultList.get(i);
			newKey.label = resultmodle.getLable();
			newKey.codes[0] = resultmodle.getCode();
		}
	}

	class KeyModel {
		private Integer code;
		private String label;

		public KeyModel(Integer code, String lable) {
			this.code = code;
			this.label = lable;
		}

		public Integer getCode() {
			return code;
		}

		public void setCode(Integer code) {
			this.code = code;
		}

		public String getLable() {
			return label;
		}

		public void setLabel(String lable) {
			this.label = lable;
		}

	}

	public static int dpToPx(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	private void initScreenParams(Context context) {
		DisplayMetrics dMetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		display.getMetrics(dMetrics);

		screenw = dMetrics.widthPixels;
		screenh = dMetrics.heightPixels;
		density = dMetrics.density;
		densityDpi = dMetrics.densityDpi;
		screenh_nonavbar = screenh;

		int ver = Build.VERSION.SDK_INT;
		// 新版本的android 系统有导航栏，造成无法正确获取高度
		if (ver == 13) {
			try {
				Method mt = display.getClass().getMethod("getRealHeight");
				screenh_nonavbar = (Integer) mt.invoke(display);
			} catch (Exception e) {
			}
		} else if (ver > 13) {
			try {
				Method mt = display.getClass().getMethod("getRawHeight");
				screenh_nonavbar = (Integer) mt.invoke(display);
			} catch (Exception e) {
			}
		}
		real_scontenth = screenh_nonavbar - getStatusBarHeight(context);
	}

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

}
