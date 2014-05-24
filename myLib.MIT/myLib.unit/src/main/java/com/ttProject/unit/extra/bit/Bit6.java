/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

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
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x3F);
	}
}
