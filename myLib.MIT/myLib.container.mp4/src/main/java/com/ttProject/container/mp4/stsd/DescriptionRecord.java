package com.ttProject.container.mp4.stsd;

import com.ttProject.container.mp4.Mp4Atom;
import com.ttProject.unit.extra.bit.Bit32;

public abstract class DescriptionRecord extends Mp4Atom {
	public DescriptionRecord(Bit32 size, Bit32 name) {
		super(size, name);
	}
}
