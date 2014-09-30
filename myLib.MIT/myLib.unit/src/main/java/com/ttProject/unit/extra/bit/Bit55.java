/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit55
 * @author taktod
 */
public class Bit55 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit55(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit55() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
