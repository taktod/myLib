package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit10 extends BitN {
	public Bit10(int value) {
		this();
		set(value);
	}
	public Bit10() {
		super(new Bit2(), new Bit8());
	}
}
