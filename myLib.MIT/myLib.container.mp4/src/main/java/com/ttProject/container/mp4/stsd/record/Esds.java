package com.ttProject.container.mp4.stsd.record;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit32;

public class Esds extends Mp4Atom {
	public Esds(Bit32 size, Bit32 name) {
		super(size, name);
	}
	@Override
	protected void requestUpdate() throws Exception {
	}
}
