/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit3
 * @author taktod
 */
public class Bit3 extends Bit {
	/**
	 * constructor
	 */
	public Bit3() {
		this(0);
	}
	/**
	 * constructor
	 * @param value
	 */
	public Bit3(int value) {
		super(3);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x07);
	}
}
