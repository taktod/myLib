package com.ttProject.media.mp4;

import com.ttProject.media.mp4.atom.Stsd;
import com.ttProject.media.mp4.atom.stsd.RecordAnalyzer;
import com.ttProject.nio.channels.IReadChannel;

public class AtomAnalyzer implements IAtomAnalyzer {
	private final Mp4Manager manager = new Mp4Manager();
	@Override
	public Atom analyze(IReadChannel ch) throws Exception {
		Atom atom = manager.getUnit(ch);
		if(atom == null) {
			return null;
		}
		if(atom instanceof Stsd) {
			try {
				atom.analyze(ch, new RecordAnalyzer());
			}
			catch (Exception e) {
				System.out.println("flvに適合しないコーデックタグをみつけました。:" + e.getMessage());
			}
		}
		else {
			atom.analyze(ch, this);
		}
		ch.position(atom.getPosition() + atom.getSize());
		return atom;
	}
}
