package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit62 extends BitN {
	public Bit62(int value) {
		this();
		set(value);
	}
	public Bit62() {
		super(new Bit6(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}