package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Mdia extends ParentAtom {
	public Mdia(int size, int position) {
		super(Mdia.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
