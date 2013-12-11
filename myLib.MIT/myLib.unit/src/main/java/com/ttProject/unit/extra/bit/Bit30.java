package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit30 extends BitN {
	public Bit30(int value) {
		this();
		set(value);
	}
	public Bit30() {
		super(new Bit6(), new Bit8(), new Bit8(), new Bit8());
	}
}
