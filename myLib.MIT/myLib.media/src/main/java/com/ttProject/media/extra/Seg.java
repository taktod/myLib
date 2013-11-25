package com.ttProject.media.extra;

/**
 * signed exp-golomb
 * @author taktod
 */
public class Seg extends ExpGolomb {
	/**
	 * データを参照します
	 * @return
	 */
	public int getData() {
		int value = getValue();
		if((value & 0x01) == 1) {
			return -1 * (value >>> 1);
		}
		else {
			return (value >>> 1);
		}
	}
	/**
	 * dump
	 */
	@Override
	public String toString() {
		return Integer.toString(getData());
	}
}
