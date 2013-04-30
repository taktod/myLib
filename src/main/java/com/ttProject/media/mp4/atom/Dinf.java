package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Dinf extends ParentAtom {
	public Dinf(int size, int position) {
		super(Dinf.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}
