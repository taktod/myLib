package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit56 extends BitN {
	public Bit56(int value) {
		this();
		set(value);
	}
	public Bit56() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}