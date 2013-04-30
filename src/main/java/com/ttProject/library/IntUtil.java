package com.ttProject.library;

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
