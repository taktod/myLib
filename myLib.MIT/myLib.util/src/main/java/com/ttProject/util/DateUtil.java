/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日付関連の処理補助
 * @author taktod
 */
public class DateUtil {
	/**
	 * 日付文字列
	 * @return
	 */
	public static String makeDate() {
		return make(new SimpleDateFormat("yyyyMMdd"));
	}
	/**
	 * 時刻文字列
	 * @return
	 */
	public static String makeDateTime() {
		return make(new SimpleDateFormat("yyyyMMdd_HHmmss"));
	}
	/**
	 * 動作補助
	 * @param dateFormat
	 * @return
	 */
	private static String make(DateFormat dateFormat) {
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		return dateFormat.format(new Date());
	}
}
