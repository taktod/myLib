package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Edts extends ParentAtom {
	public Edts(int size, int position) {
		super(Edts.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
