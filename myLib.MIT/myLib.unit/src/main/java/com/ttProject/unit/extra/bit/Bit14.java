package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit14 extends BitN {
	public Bit14(int value) {
		this();
		set(value);
	}
	public Bit14() {
		super(new Bit6(), new Bit8());
	}
}
