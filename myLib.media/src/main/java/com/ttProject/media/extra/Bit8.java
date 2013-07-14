package com.ttProject.media.extra;

/**
 * 8ビットを表現する型
 * @author taktod
 */
public class Bit8 {
	private byte value;
	public void set(int value) {
		this.value = (byte)(value);
	}
	public byte get() {
		return value;
	}
}
