/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit63
 * @author taktod
 */
public class Bit63 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit63(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit63() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
