package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public class Dref extends Atom {
	public Dref(int position, int size) {
		super(Dref.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}