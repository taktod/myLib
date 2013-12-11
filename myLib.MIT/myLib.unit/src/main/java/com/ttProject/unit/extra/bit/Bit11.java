package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit11 extends BitN {
	public Bit11(int value) {
		this();
		set(value);
	}
	public Bit11() {
		super(new Bit3(), new Bit8());
	}
}
