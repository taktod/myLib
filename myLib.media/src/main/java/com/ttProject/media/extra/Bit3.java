package com.ttProject.media.extra;

/**
 * 3ビットを表現する型
 * @author taktod
 */
public class Bit3 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit3() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit3(int value) {
		super(3);
		set(value);
	}
	/**
	 * データ設定
	 * @param value
	 */
	public void set(int value) {
		super.set(value & 0x07);
	}
}
