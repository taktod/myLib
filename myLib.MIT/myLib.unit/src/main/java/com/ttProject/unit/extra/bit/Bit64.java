package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit64 extends BitN {
	public Bit64(int value) {
		this();
		set(value);
	}
	public Bit64() {
		super(new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}