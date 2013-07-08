package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class Mvhd extends Atom {
	public Mvhd(int position, int size) {
		super(Mvhd.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
