/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit18
 * @author taktod
 */
public class Bit18 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit18(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit18() {
		super(new Bit2(), new Bit8(), new Bit8());
	}
}
