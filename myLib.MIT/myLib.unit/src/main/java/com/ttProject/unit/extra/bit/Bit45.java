/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit45
 * @author taktod
 */
public class Bit45 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit45(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit45() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
