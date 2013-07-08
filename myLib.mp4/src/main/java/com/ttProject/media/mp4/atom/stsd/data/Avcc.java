package com.ttProject.media.mp4.atom.stsd.data;

import com.ttProject.media.mp4.Atom;
import com.ttProject.media.mp4.IAtomAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

/**
 * h.264のmediaSequenceHeaderを保持しているデータ
 * 動画用(中身全部がそのままmediaSequenceHeaderになる)
 * @author taktod
 */
public class Avcc extends Atom {
	public Avcc(int size, int position) {
		super(Avcc.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer) throws Exception {

	}
}
