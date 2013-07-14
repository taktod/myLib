package com.ttProject.media.extra;

/**
 * 1ビットを表現する型
 * @author taktod
 */
public class Bit1 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x01);
	}
	public byte get() {
		return value;
	}
}
