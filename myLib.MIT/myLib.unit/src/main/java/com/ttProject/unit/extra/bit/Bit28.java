package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit28 extends BitN {
	public Bit28(int value) {
		this();
		set(value);
	}
	public Bit28() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8());
	}
}
