/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.Bit;

/**
 * Bit5
 * @author taktod
 */
public class Bit5 extends Bit {
	/**
	 * constructor
	 */
	public Bit5() {
		this(0);
	}
	/**
	 * constructor
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
