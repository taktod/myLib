package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp4のstsdの内部データのさらに奥のデータ
 * 音声用らしい
 * @author taktod
 */
public class Esds extends Atom {
	public Esds(int size, int position) {
		super(Esds.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
