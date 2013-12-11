package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit31 extends BitN {
	public Bit31(int value) {
		this();
		set(value);
	}
	public Bit31() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8());
	}
}
