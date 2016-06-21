package com.android.util;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.style.AbsoluteSizeSpan;

public class StringUtils {

	public final static String regularEx = "[^\\w]";

	public static String replace(String originUrl) {
		if (null == originUrl)
			return null;
		return originUrl.replaceAll(regularEx, "");
	}

	public static String getPicName(String originUrl) {
		if (null == originUrl) {
			return null;
		}
		String temp[] = originUrl.split("/");
		int length = temp.length;
		return temp[length - 1].replace(" ", "");
	}

	public static String replace(String originUrl, String tail) {
		if (null == originUrl)
			return null;
		return originUrl.replaceAll(regularEx, "") + tail;
	}

	// public static String getPicFileName(String originUrl) {
	// String temp[] = originUrl.split("/");
	// int length = temp.length;
	// return temp[length - 1];
	// }
	//

	public static String replaceWithTail(String originUrl) {
		if (null == originUrl)
			return null;
		return originUrl.replaceAll(regularEx, "") + ".jpg";
	}

	public static SpannedString getHintValue(Context c, int resId) {
		SpannableString ss = new SpannableString(c.getResources().getString(resId));
		// 新建一个属性对象,设置文字的大小
		AbsoluteSizeSpan ass = new AbsoluteSizeSpan(SNSAPI.HintSize, true);

		ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		// 附加属性到文本
		return new SpannedString(ss);
	}

	/**
	 * 判断Android系统API的版本
	 * 
	 * @return
	 */
	public static int getAPIVersion() {
		int APIVersion;
		try {
			APIVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
		} catch (NumberFormatException e) {
			APIVersion = 0;
		}
		return APIVersion;
	}

}
