package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * 5ビットを表現する型
 * @author taktod
 */
public class Bit5 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit5() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit5(int value) {
		super(5);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x1F);
	}
}
