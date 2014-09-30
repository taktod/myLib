/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit47
 * @author taktod
 */
public class Bit47 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit47(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit47() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
