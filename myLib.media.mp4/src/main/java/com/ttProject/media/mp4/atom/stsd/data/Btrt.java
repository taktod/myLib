package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * mp4のstsdの内部データのさらに奥にあるデータ
 * 動画用のデータらしい
 * @author taktod
 */
public class Btrt extends Atom {
	public Btrt(int position, int size) {
		super(Btrt.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
