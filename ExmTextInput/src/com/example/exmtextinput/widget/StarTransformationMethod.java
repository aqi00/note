package com.example.exmtextinput.widget;

import android.text.method.ReplacementTransformationMethod;

public class StarTransformationMethod extends ReplacementTransformationMethod {
	public static int TYPE_DOT = 0;
	public static int TYPE_STAR = 1;
	private char[] mCharArray = new char[] {'\u2022', '\u002A'};
	private int mType = TYPE_DOT;
	private static char[] ORIGINAL = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\r' };
	private static char[] REPLACEMENT;

	public StarTransformationMethod(int type) {
		mType = type;
		REPLACEMENT = new char[ORIGINAL.length];
		for (int i=0; i<ORIGINAL.length; i++) {
			if (ORIGINAL[i]>='0' && ORIGINAL[i]<='9') {
				REPLACEMENT[i] = mCharArray[mType];
			} else {
				REPLACEMENT[i] = '\uFEFF';
			}
		}
	}
	
	protected char[] getOriginal() {
		return ORIGINAL;
	}

	protected char[] getReplacement() {
		return REPLACEMENT;
	}

	private static StarTransformationMethod sInstance;
	public static StarTransformationMethod getInstance(int type) {
		if (sInstance != null)
			return sInstance;

		sInstance = new StarTransformationMethod(type);
		return sInstance;
	}

}
