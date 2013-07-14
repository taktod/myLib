package com.ttProject.util;

/**
 * bitのデータを確認するためのライブラリ
 * とりあえず1111 1111  1111 1111みたいな表示にしたいところ。
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
			// 先頭に0を追加しておく。
			char[] buf = new char[length - data.length()];
			for(int i = 0;i < buf.length;i ++) {
				buf[i] = '0';
			}
			return new String(buf) + data;
		}
		return data;
	}
}
