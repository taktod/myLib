/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.util;

/**
 * 数値系の便利関数
 * @author taktod
 */
public class IntUtil {
	public static String makeHexString(int data) {
		return new String(new byte[]{
			(byte)((data >> 24) & 0xFF),
			(byte)((data >> 16) & 0xFF),
			(byte)((data >> 8) & 0xFF),
			(byte)(data & 0xFF)
		}).intern();
	}
}
