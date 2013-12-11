package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit45 extends BitN {
	public Bit45(int value) {
		this();
		set(value);
	}
	public Bit45() {
		super(new Bit5(), new Bit8(), new Bit8(), new Bit8(), new Bit8(), new Bit8());
	}
}
