package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit52 extends BitN {
	public Bit52(int value) {
		this();
		set(value);
	}
	public Bit52() {
		super(new Bit4(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}