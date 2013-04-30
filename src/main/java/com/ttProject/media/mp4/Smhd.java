package com.ttProject.media.mp4;

import com.ttProject.nio.channels.IFileReadChannel;

public class Smhd extends Atom {
	public Smhd(int size, int position) {
		super(Smhd.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}
