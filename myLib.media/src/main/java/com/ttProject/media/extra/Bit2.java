package com.ttProject.media.extra;

/**
 * 2ビットを表現する型
 * @author taktod
 */
public class Bit2 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value & 0x03);
	}
	public byte get() {
		return value;
	}
}
