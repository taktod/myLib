package com.ttProject.media.extra;

/**
 * 7ビットを表現する型
 * @author taktod
 */
public class Bit7 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x7F);
	}
	public byte get() {
		return value;
	}
}
