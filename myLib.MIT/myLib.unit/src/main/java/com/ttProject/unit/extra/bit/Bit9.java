package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit9 extends BitN {
	public Bit9(int value) {
		this();
		set(value);
	}
	public Bit9() {
		super(new Bit1(), new Bit8());
	}
}