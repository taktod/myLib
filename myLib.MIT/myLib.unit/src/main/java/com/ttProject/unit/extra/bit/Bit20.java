package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit20 extends BitN {
	public Bit20(int value) {
		this();
		set(value);
	}
	public Bit20() {
		super(new Bit4(), new Bit8(), new Bit8());
	}
}
