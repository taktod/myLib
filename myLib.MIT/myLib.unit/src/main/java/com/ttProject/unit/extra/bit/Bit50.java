package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit50 extends BitN {
	public Bit50(int value) {
		this();
		set(value);
	}
	public Bit50() {
		super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
