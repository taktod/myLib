package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit53 extends BitN {
	public Bit53(int value) {
		this();
		set(value);
	}
	public Bit53() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
