package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class Skip extends Atom {
	public Skip(int position, int size) {
		super(Skip.class.getSimpleName().toLowerCase(), position, position);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("");
	}
}
