/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit38
 * @author taktod
 */
public class Bit38 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit38(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit38() {
		super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
