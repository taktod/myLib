package com.ttProject.unit.extra;

/**
 * unsigned exp-golomb
 * @author taktod
 */
public class Ueg extends ExpGolomb {
	/**
	 * データを参照します
	 * @return
	 */
	public int getData() {
		return getValue() - 1;
	}
	public void setData(int val) {
		setValue(val + 1);
	}
	/**
	 * dump
	 */
	@Override
	public String toString() {
		return Integer.toString(getData());
	}
}
