package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 各サンプルのデータサイズを保持してあります。
 * @author taktod
 */
public class Stsz extends Atom {
	public Stsz(int size, int position) {
		super(Stsz.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("          ");
	}
}
