/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit8
 * @author taktod
 */
public class Bit8 extends Bit {
	/**
	 * constructor
	 */
	public Bit8() {
		this(0);
	}
	/**
	 * constructor
	 * @param value
	 */
	public Bit8(int value) {
		super(8);
		set(value);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void set(int value) {
		super.set(value & 0xFF);
	}
}
