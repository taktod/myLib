package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Minf extends ParentAtom {
	public Minf(int position, int size) {
		super(Minf.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
