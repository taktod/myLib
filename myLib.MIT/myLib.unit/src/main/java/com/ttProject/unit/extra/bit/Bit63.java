package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit63 extends BitN {
	public Bit63(int value) {
		this();
		set(value);
	}
	public Bit63() {
		super(new Bit7(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
