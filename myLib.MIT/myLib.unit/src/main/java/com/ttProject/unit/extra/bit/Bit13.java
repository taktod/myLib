/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit13 extends BitN {
	public Bit13(int value) {
		this();
		set(value);
	}
	public Bit13() {
		super(new Bit5(), new Bit8());
	}
}
