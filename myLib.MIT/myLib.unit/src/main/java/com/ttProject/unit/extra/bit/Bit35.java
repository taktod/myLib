/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit35
 * @author taktod
 */
public class Bit35 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit35(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit35() {
		super(new Bit3(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
