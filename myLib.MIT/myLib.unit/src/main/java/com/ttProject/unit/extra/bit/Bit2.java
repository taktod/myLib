/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit2
 * @author taktod
 */
public class Bit2 extends Bit {
	/**
	 * constructor
	 */
	public Bit2() {
		this(0);
	}
	/**
	 * constructor
	 * @param value
	 */
	public Bit2(int value) {
		super(2);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0x03);
	}
}
