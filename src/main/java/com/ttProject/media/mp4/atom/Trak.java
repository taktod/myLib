package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Trak extends ParentAtom {
	public Trak(int size, int position) {
		super(Trak.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
