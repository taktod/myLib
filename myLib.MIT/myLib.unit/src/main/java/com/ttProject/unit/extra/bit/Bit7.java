/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit7
 * @author taktod
 */
public class Bit7 extends Bit {
	/**
	 * constructor
	 */
	public Bit7() {
		this(0);
	}
	/**
	 * constructor
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
