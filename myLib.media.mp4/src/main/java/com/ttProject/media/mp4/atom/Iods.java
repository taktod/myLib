package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class Iods extends Atom {
	public Iods(int position, int size) {
		super(Iods.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
