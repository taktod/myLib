package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mp4のstsdの内部データのさらに奥にあるデータ
 * 動画用のデータらしい
 * @author taktod
 */
public class Btrt extends Atom {
	public Btrt(int size, int position) {
		super(Btrt.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
