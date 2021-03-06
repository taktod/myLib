/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit30
 * @author taktod
 */
public class Bit30 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit30(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit30() {
		super(new Bit6(), new Bit8(), new Bit8(), new Bit8());
	}
}
