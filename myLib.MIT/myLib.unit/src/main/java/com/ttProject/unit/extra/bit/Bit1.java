/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit1
 * @author taktod
 */
public class Bit1 extends Bit {
	/**
	 * constructor
	 */
	public Bit1() {
		this(0);
	}
	/**
	 * constructor
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
