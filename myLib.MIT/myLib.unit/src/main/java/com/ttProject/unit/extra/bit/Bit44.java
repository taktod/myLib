/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit44
 * @author taktod
 */
public class Bit44 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit44(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit44() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
