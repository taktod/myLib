package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit32 extends BitN {
	public Bit32(int value) {
		this();
		set(value);
	}
	public Bit32() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}