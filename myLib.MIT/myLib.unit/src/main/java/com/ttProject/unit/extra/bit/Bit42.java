package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit42 extends BitN {
	public Bit42(int value) {
		this();
		set(value);
	}
	public Bit42() {
		super(new Bit2(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
