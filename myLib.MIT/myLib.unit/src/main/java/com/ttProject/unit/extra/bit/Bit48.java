package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit48 extends BitN {
	public Bit48(int value) {
		this();
		set(value);
	}
	public Bit48() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
