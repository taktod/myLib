/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit19
 * @author taktod
 */
public class Bit19 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit19(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit19() {
		super(new Bit3(), new Bit8(), new Bit8());
	}
}
