package com.ttProject.media.extra;

/**
 * 2ビットを表現する型
 * @author taktod
 */
public class Bit2 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit2() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit2(int value) {
		super(2);
		set(value);
	}
	@Override
	public void set(int value) {
		super.set(value & 0x03);
	}
}
