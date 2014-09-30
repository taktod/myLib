/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

/**
 * Bit10
 * @author taktod
 */
public class Bit10 extends BitN {
	/**
	 * constructor
	 * @param value
	 */
	public Bit10(int value) {
		this();
		set(value);
	}
	/**
	 * constructor
	 */
	public Bit10() {
		super(new Bit2(), new Bit8());
	}
}
