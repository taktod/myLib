package com.ttProject.media.mp4.atom;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * メディアデータ本体を保持しているタグ
 * @author taktod
 */
public class Mdat extends Atom {
	public Mdat(int size, int position) {
		super(Mdat.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
	@Override
	public String toString() {
		return super.toString();
	}
}
