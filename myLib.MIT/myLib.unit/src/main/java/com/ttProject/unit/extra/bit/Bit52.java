/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit52
 * @author taktod
 */
public class Bit52 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit52(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit52() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
