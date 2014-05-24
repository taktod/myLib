/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit27 extends BitN {
	public Bit27(int value) {
		this();
		set(value);
	}
	public Bit27() {
		super(new Bit3(), new Bit8(), new Bit8(), new Bit8());
	}
}
