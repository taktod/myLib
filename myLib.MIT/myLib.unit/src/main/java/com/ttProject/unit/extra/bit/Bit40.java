package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit40 extends BitN {
	public Bit40(int value) {
		this();
		set(value);
	}
	public Bit40() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}