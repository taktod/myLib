/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit31
 * @author taktod
 */
public class Bit31 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit31(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit31() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8());
	}
}
