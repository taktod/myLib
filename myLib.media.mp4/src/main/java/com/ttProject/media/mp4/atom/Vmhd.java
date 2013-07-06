package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 映像ヘッダ情報
 * @author taktod
 */
public class Vmhd extends Atom {
	public Vmhd(int position, int size) {
		super(Vmhd.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}
