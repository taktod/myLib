/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

/**
 * dump bit status.
 * @author taktod
 */
public class BitUtil {
//	private static final char SEPARATOR = ' ';
	public static String toBit(final int value, int length) {
		String data = Integer.toBinaryString(value);
		if(data.length() > length) {
			return data.substring(data.length() - length);
		}
		else if(data.length() < length) {
			// put 0 for front.
			char[] buf = new char[length - data.length()];
			for(int i = 0;i < buf.length;i ++) {
				buf[i] = '0';
			}
			return new String(buf) + data;
		}
		return data;
	}
}
