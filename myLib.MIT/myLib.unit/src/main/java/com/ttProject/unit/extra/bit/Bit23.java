package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit23 extends BitN {
	public Bit23(int value) {
		this();
		set(value);
	}
	public Bit23() {
		super(new Bit7(), new Bit8(), new Bit8());
	}
}