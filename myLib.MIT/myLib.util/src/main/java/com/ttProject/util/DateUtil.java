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
 * util for date
 * @author taktod
 */
public class DateUtil {
	/**
	 * date
	 * @return
	 */
	public static String makeDate() {
		return make(new SimpleDateFormat("yyyyMMdd"));
	}
	/**
	 * time
	 * @return
	 */
	public static String makeDateTime() {
		return make(new SimpleDateFormat("yyyyMMdd_HHmmss"));
	}
	/**
	 * common func.
	 * @param dateFormat
	 * @return
	 */
	private static String make(DateFormat dateFormat) {
		dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
		return dateFormat.format(new Date());
	}
}
