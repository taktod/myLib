package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 各チャンクが保持しているサンプル数を保持しています。
 * @author taktod
 *
 */
public class Stsc extends Atom {
	public Stsc(int size, int position) {
		super(Stsc.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
