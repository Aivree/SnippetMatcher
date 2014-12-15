package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author : majun
 * @date :2014年9月9日上午11:06:46
 * @version:v1.0+
 * @FileName:ActivityTool.java
 * @ProjectName:去哪了
 * @PackageName:com.utils
 * @EnclosingType:
 * @Description:activity 工具类
 */
public class ActivityTool {

	public static void activityJump(Context context, Class<?> clazz) {
		Intent intent = new Intent(context, clazz);
		context.startActivity(intent);
	}

	public static void activityForResult(Activity context, Class<?> cls,
			int requestCode) {
		Intent intent = new Intent(context, cls);
		//intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		//intent.setClass(context, cls);
		context.startActivityForResult(intent, requestCode);
	}
}
