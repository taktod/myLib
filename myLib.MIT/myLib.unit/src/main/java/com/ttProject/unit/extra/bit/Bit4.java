package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * 4ビットを表現する型
 * @author taktod
 */
public class Bit4 extends Bit {
	/**
	 * コンストラクタ
	 */
	public Bit4() {
		this(0);
	}
	/**
	 * コンストラクタ
	 * @param value
	 */
	public Bit4(int value) {
		super(4);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x0F);
	}
}
