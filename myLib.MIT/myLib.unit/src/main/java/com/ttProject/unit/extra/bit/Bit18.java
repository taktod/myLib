package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit18 extends BitN {
	public Bit18(int value) {
		this();
		set(value);
	}
	public Bit18() {
		super(new Bit2(), new Bit8(), new Bit8());
	}
}
