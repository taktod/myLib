package com.ttProject.container.mp4.stsd;

import com.ttProject.unit.extra.bit.Bit32;

public class Avc1 extends VideoRecord {
	public Avc1(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
