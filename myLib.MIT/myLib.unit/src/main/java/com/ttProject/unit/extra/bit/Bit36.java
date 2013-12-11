package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit36 extends BitN {
	public Bit36(int value) {
		this();
		set(value);
	}
	public Bit36() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
