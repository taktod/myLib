package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Minf extends ParentAtom {
	public Minf(int size, int position) {
		super(Minf.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
