/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit61
 * @author taktod
 */
public class Bit61 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit61(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit61() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
