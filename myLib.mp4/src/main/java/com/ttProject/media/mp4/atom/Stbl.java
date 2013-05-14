package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.ParentAtom;

public class Stbl extends ParentAtom {
	public Stbl(int size, int position) {
		super(Stbl.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}
