package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IFileReadChannel;

public class Stsd extends Atom {
	public Stsd(int size, int position) {
		super(Stsd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
