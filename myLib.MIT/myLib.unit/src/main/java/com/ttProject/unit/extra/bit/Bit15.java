/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit15 extends BitN {
	public Bit15(int value) {
		this();
		set(value);
	}
	public Bit15() {
		super(new Bit7(), new Bit8());
	}
}
