/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit24
 * @author taktod
 */
public class Bit24 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit24(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit24() {
		super(new Bit8(), new Bit8(), new Bit8());
	}
}
