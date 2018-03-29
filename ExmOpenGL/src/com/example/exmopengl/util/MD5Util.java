package com.example.exmopengl.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

	public static String encrypBytes(byte[] rawBytes) {
		String md5Str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(rawBytes);
			byte[] encryContext = md.digest();

			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < encryContext.length; offset++) {
				i = encryContext[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			md5Str = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5Str;
	}

	/*
	 * 1.一个运用基本类的实例 MessageDigest 对象开始被初始化。该对象通过使用 update 方法处理数据。 任何时候都可以调用
	 * reset 方法重置摘要。 一旦所有需要更新的数据都已经被更新了，应该调用 digest 方法之一完成哈希计算。
	 * 对于给定数量的更新数据，digest 方法只能被调用一次。 在调用 digest 之后，MessageDigest 对象被重新设置成其初始状态。
	 */
	public static String encrypByMd5(String raw) {
		String md5Str = raw;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(raw.getBytes());
			byte[] encryContext = md.digest();

			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < encryContext.length; offset++) {
				i = encryContext[offset];
				if (i < 0) {
					i += 256;
				}
				if (i < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(i));
			}
			md5Str = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5Str;
	}

}
