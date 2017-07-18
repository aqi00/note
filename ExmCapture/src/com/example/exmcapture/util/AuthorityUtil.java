package com.example.exmcapture.util;

import java.lang.reflect.Method;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;

public class AuthorityUtil {

	public static boolean checkOp(Context context, int op) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
			try {
				Method method = manager.getClass().getDeclaredMethod("checkOp", 
						int.class, int.class, String.class);
				int property = (Integer) method.invoke(manager, op,
						Binder.getCallingUid(), context.getPackageName());
				if (AppOpsManager.MODE_ALLOWED == property) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
}
