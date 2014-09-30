/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit4
 * @author taktod
 */
public class Bit4 extends Bit {
	/**
	 * constructor
	 */
	public Bit4() {
		this(0);
	}
	/**
	 * constructor
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
