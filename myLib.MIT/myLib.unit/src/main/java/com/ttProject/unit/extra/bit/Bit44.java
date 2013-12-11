package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit44 extends BitN {
	public Bit44(int value) {
		this();
		set(value);
	}
	public Bit44() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
