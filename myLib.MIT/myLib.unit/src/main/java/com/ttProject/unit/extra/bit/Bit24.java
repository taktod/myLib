package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit24 extends BitN {
	public Bit24(int value) {
		this();
		set(value);
	}
	public Bit24() {
		super(new Bit8(), new Bit8(), new Bit8());
	}
}
