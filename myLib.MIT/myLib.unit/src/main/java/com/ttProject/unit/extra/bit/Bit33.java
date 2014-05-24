/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit33 extends BitN {
	public Bit33(int value) {
		this();
		set(value);
	}
	public Bit33() {
		super(new Bit1(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
