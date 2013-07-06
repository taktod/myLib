package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Edts extends ParentAtom {
	public Edts(int position, int size) {
		super(Edts.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
