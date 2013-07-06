package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * h.264のmediaSequenceHeaderを保持しているデータ
 * 動画用(中身全部がそのままmediaSequenceHeaderになる)
 * @author taktod
 */
public class Avcc extends Atom {
	public Avcc(int position, int size) {
		super(Avcc.class.getSimpleName().toLowerCase(), position, size);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
