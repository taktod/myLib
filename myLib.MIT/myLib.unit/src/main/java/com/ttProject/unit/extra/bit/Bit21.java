package com.ttProject.unit.extra.bit;

import com.ttProject.unit.extra.BitN;

public class Bit21 extends BitN {
	public Bit21(int value) {
		this();
		set(value);
	}
	public Bit21() {
		super(new Bit5(), new Bit8(), new Bit8());
	}
}
