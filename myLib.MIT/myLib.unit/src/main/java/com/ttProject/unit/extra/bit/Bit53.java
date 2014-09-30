/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit53
 * @author taktod
 */
public class Bit53 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit53(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit53() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
