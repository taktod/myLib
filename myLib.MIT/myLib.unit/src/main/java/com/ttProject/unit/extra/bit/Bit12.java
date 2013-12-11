package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit12 extends BitN {
	public Bit12(int value) {
		this();
		set(value);
	}
	public Bit12() {
		super(new Bit4(), new Bit8());
	}
}
