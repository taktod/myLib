/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit16
 * @author taktod
 */
public class Bit16 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit16(int value) {
		this();
		set(value);
	}
	/**
	 * constuctor
	 */
	public Bit16() {
		super(new Bit8(), new Bit8());
	}
}
