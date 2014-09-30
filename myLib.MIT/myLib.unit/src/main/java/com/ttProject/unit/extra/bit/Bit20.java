/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit20
 * @author taktod
 */
public class Bit20 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit20(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit20() {
		super(new Bit4(), new Bit8(), new Bit8());
	}
}
