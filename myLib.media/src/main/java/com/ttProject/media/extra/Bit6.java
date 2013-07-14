package com.ttProject.media.extra;

/**
 * 6ビットを表現する型
 * @author taktod
 */
public class Bit6 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x3F);
	}
	public byte get() {
		return value;
	}
}
