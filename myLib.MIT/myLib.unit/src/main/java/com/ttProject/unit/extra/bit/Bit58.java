package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit58 extends BitN {
	public Bit58(int value) {
		this();
		set(value);
	}
	public Bit58() {
		super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
