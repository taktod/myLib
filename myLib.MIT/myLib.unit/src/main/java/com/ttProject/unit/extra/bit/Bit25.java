package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit25 extends BitN {
	public Bit25(int value) {
		this();
		set(value);
	}
	public Bit25() {
		super(new Bit1(), new Bit8(), new Bit8(), new Bit8());
	}
}
