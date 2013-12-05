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
	public int get() {
		return super.get() - 1;
	}
	public void set(int val) {
		super.set(val + 1);
	}
	/**
	 * dump
	 */
	@Override
	public String toString() {
		return Integer.toString(get());
	}
}
