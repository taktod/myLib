package com.ttProject.unit.extra;

/**
 * 1ビットを表現する型
 * @author taktod
 */
public class Bit1 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit1() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit1(int value) {
		super(1);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x01);
	}
}
