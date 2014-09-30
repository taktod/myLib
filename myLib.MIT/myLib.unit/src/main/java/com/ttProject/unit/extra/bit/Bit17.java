/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit17
 * @author taktod
 */
public class Bit17 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit17(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit17() {
		super(new Bit1(), new Bit8(), new Bit8());
	}
}
