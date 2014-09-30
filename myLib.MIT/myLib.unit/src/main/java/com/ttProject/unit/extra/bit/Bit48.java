/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit48
 * @author taktod
 */
public class Bit48 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit48(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit48() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
