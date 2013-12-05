package com.ttProject.unit.extra;

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
	public void setData(int val) {
		if(val > 0) {
			setValue(val << 1);
		}
		else {
			setValue(val << 1 | 1);
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
