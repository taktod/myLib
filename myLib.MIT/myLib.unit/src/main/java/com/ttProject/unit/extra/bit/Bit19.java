package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit19 extends BitN {
	public Bit19(int value) {
		this();
		set(value);
	}
	public Bit19() {
		super(new Bit3(), new Bit8(), new Bit8());
	}
}
