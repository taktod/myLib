package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * 7ビットを表現する型
 * @author taktod
 */
public class Bit7 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit7() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit7(int value) {
		super(7);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x7F);
	}
}
