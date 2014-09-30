/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit36
 * @author taktod
 */
public class Bit36 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit36(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit36() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
