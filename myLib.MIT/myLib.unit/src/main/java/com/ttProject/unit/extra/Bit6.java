package com.ttProject.unit.extra;

/**
 * 6ビットを表現する型
 * @author taktod
 */
public class Bit6 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit6() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit6(int value) {
		super(6);
		set(value);
	}
	/**
	 * データ設定
	 * @param value
	 */
	public void set(int value) {
		super.set(value & 0x3F);
	}
}
