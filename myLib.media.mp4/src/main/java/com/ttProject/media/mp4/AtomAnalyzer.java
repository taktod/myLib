package com.ttProject.media.mp4;

import com.ttProject.media.mp4.atom.Stsd;
import com.ttProject.media.mp4.atom.stsd.RecordAnalyzer;
import com.ttProject.nio.channels.IFileReadChannel;

public class AtomAnalyzer implements IAtomAnalyzer {
	@Override
	public Atom analyze(IFileReadChannel ch) throws Exception {
		Atom atom = Atom.getAtom(ch);
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
