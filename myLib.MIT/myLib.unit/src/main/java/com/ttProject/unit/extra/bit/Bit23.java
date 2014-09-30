/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit23
 * @author taktod
 */
public class Bit23 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit23(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit23() {
		super(new Bit7(), new Bit8(), new Bit8());
	}
}
