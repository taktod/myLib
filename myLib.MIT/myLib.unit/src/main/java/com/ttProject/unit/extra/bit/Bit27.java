/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit27
 * @author taktod
 */
public class Bit27 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit27(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit27() {
		super(new Bit3(), new Bit8(), new Bit8(), new Bit8());
	}
}
