package com.ttProject.media.extra;

/**
 * 4ビットを表現する型
 * @author taktod
 */
public class Bit4 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x0F);
	}
	public byte get() {
		return value;
	}
}
