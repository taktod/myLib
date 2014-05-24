/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit10 extends BitN {
	public Bit10(int value) {
		this();
		set(value);
	}
	public Bit10() {
		super(new Bit2(), new Bit8());
	}
}
