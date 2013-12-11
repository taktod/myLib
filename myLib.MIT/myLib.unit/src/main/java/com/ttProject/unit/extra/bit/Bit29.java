package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit29 extends BitN {
	public Bit29(int value) {
		this();
		set(value);
	}
	public Bit29() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8());
	}
}
