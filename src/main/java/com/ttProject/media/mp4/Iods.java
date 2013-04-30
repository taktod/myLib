package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IFileReadChannel;

public class Iods extends Atom {
	public Iods(int size, int position) {
		super(Iods.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
