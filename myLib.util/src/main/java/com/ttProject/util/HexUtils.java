package com.ttProject.util;

/**
 * hexのデータを確認するためのライブラリ
 * とりあえずflazrにあったのが便利なんですが、他のライブラリとの競合もあるんで抜き出しておく。
 * 元ネタはflazr
 * @author taktod
 */
public class HexUtils {
	private static final char[] HEX_DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F'
	};
	
	private static final char SEPARATOR = ' ';
	
	public static String toHex(final byte[] ba) {
		return toHex(ba, false);
	}
	public static String toHex(final byte[] ba, final boolean withSeparator) {
		return toHex(ba, 0, ba.length, withSeparator);
	}
	public static String toHex(final byte[] ba, final int offset, final int length, final boolean withSeparator) {
		final char[] buf;
		buf = new char[length * (withSeparator ? 3 : 2)];
		for(int i = offset, j = 0;i < offset + length;) {
			final char[] chars = toHexChars(ba[i ++]);
			buf[j ++] = chars[0];
			buf[j ++] = chars[1];
			if(withSeparator) {
				buf[j ++] = SEPARATOR;
			}
		}
		return new String(buf);
	}
	private static char[] toHexChars(final int b) {
		final char left = HEX_DIGITS[(b >>> 4) & 0x0F];
		final char right = HEX_DIGITS[b & 0x0F];
		return new char[] {left, right};
	}
	public static String toHex(final byte b) {
		final char[] chars = toHexChars(b);
		return String.valueOf(chars);
	}
}
