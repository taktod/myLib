/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit25
 * @author taktod
 */
public class Bit25 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit25(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit25() {
		super(new Bit1(), new Bit8(), new Bit8(), new Bit8());
	}
}
