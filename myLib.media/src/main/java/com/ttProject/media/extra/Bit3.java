package com.ttProject.media.extra;

/**
 * 3ビットを表現する型
 * @author taktod
 */
public class Bit3 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x07);
	}
	public byte get() {
		return value;
	}
}
