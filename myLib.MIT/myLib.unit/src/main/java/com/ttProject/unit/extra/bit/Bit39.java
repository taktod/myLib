/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit39 extends BitN {
	public Bit39(int value) {
		this();
		set(value);
	}
	public Bit39() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
