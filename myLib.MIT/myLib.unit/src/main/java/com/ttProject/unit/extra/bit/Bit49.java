/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit49
 * @author taktod
 */
public class Bit49 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit49(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit49() {
		super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
