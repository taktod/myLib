package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Mdia extends ParentAtom {
	public Mdia(int position, int size) {
		super(Mdia.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}