package com.example.exmtextinput.widget;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.example.exmtextinput.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.SystemClock;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

public class KeyboardLayout extends LinearLayout {
	private final static String TAG = "KeyboardLayout";
	private KeyboardView mKeyboardView;
	private Keyboard mKeyboard;

	public KeyboardLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public KeyboardLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initKeyboard(context, attrs);
	}
	
	public void setInputWidget(EditText et) {
		mKeyboardView.setOnKeyboardActionListener(new KeyboardListener(et));
	}
	
	private void initKeyboard(Context context, AttributeSet attrs){
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.keyboard);
		if (a.hasValue(R.styleable.keyboard_xml)) {
			int xmlid = a.getResourceId(R.styleable.keyboard_xml,0);
			mKeyboard = new Keyboard(context, xmlid);
			mKeyboardView = (KeyboardView)LayoutInflater.from(context).inflate(R.layout.keyboardview, null);
//			if(a.hasValue(R.styleable.keyboard_randomkeys)){
//				boolean random = a.getBoolean(R.styleable.keyboard_randomkeys, false);
//				if(random){
//					randomdigkey(mKeyboard);
//				}
//			}
			mKeyboardView.setKeyboard(mKeyboard);
			mKeyboardView.setEnabled(true);  
			mKeyboardView.setPreviewEnabled(false);  
			addView(mKeyboardView);

//			List<Key> keyArray = mKeyboard.getKeys();
//			for (int i=0; i<keyArray.size(); i++) {
//				Key key = keyArray.get(i);
//				String label = (key.label!=null)?key.label.toString():"null";
//				String desc = String.format("i=%d,label=%s", i, label);
//				//LogUtils.d(TAG, desc);
//				if (key.label == null && i==9) {
//					key.icon = null;
//					keyArray.set(i, key);
//				}
//			}
		}
	}

//	private boolean isNumber(String str) {
//		String wordstr = "0123456789";
//		if (wordstr.indexOf(str) > -1) {
//			return true;
//		}
//		return false;
//	}
//	
//	private void randomdigkey(Keyboard keyboard){
//		if(keyboard == null){
//			return;
//		}
//		List<Key> keyList = keyboard.getKeys();
//		// 查找出0-9的数字键
//		List<Key> newkeyList = new ArrayList<Key>();
//		for (int i = 0,size = keyList.size(); i < size; i++) {
//			Key key = keyList.get(i);
//			CharSequence label = key.label;
//			if ( label!= null && isNumber(label.toString())) {
//				newkeyList.add(key);
//			}
//		}
//		int count = newkeyList.size();
//		List<KeyModel> resultList = new ArrayList<KeyModel>();
//		LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
//		for (int i = 0; i < count; i++) {
//			temp.add(new KeyModel(48 + i, i + ""));
//		}
//		Random rand = new SecureRandom();
//		rand.setSeed(SystemClock.currentThreadTimeMillis());
//		for (int i = 0; i < count; i++) {
//			int num = rand.nextInt(count - i);
//			KeyModel model = temp.get(num);
//			resultList.add(new KeyModel(model.getCode(),model.getLable()));
//			temp.remove(num);
//		}
//		for (int i = 0,size = newkeyList.size(); i < size; i++) {
//			Key newKey = newkeyList.get(i);
//			KeyModel resultmodle = resultList.get(i); 
//			newKey.label =resultmodle .getLable();
//			newKey.codes[0] = resultmodle.getCode();
//		}
//	}
//
//	class KeyModel {
//		private Integer code;
//		private String label;
//		
//		public KeyModel(Integer code,String lable){
//			this.code = code;
//			this.label = lable;
//		}
//
//		public Integer getCode() {
//			return code;
//		}
//
//		public void setCode(Integer code) {
//			this.code = code;
//		}
//
//		public String getLable() {
//			return label;
//		}
//
//		public void setLabel(String lable) {
//			this.label = lable;
//		}
//	}

	private class KeyboardListener implements OnKeyboardActionListener {
		private EditText et;
		
		public KeyboardListener(EditText et) {
			this.et = et;
		}
		
		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			Log.d(TAG, "primaryCode="+primaryCode);
			Editable editable = et.getText();
			int start = et.getSelectionStart();
			if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				if (editable != null && editable.length() > 0) {
					if (start > 0) {
						editable.delete(start - 1, start);
					}
				}
			} else if(primaryCode>='0' && primaryCode<='9') {
				//可以直接输入的字符(如0-9)，它们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
				editable.insert(start, Character.toString((char) primaryCode));
//			} else if(primaryCode >0x7f) {
//				Key mkey = getKeyByKeyCode(primaryCode);
//				//可以直接输入的字符(如0-9,.)，他们在键盘映射xml中的keycode值必须配置为该字符的ASCII码
//				editable.insert(start, mkey.label);
//			} else {
//				//其他一些暂未开放的键指令，如next到下一个输入框等指令
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
		
	};

//	private Key getKeyByKeyCode(int keyCode){
//		if(null != mKeyboard){
//			List<Key> mKeys = mKeyboard.getKeys();
//			for (int i =0,size= mKeys.size(); i < size; i++) {
//				Key mKey = mKeys.get(i);
//				int codes[] = mKey.codes;
//				if(codes[0] == keyCode){
//					return mKey;
//				}
//			}
//		}
//		return null;
//	}
	
}
