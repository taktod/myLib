package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Stbl extends ParentAtom {
	public Stbl(int position, int size) {
		super(Stbl.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}