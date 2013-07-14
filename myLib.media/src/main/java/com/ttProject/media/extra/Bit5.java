package com.ttProject.media.extra;

/**
 * 5ビットを表現する型
 * @author taktod
 */
public class Bit5 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x1F);
	}
	public byte get() {
		return value;
	}
}
