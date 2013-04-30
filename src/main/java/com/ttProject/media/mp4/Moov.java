package com.ttProject.media.mp4;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IFileReadChannel;

public class Moov extends Atom {
	private final List<Atom> atoms = new ArrayList<Atom>();
	public Moov(int size, int position) {
		super(Moov.class.getSimpleName().toLowerCase(), size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer) throws Exception {
		if(analyzer == null) {
			return;
		}
		ch.position(getPosition() + 8);
		Atom atom = null;
		while((atom = analyzer.analize(ch)) != null) {
			atoms.add(atom);
			if(ch.position() >= getPosition() + getSize()) {
				break;
			}
		}
		analyzed();
	}
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append(super.toString()).append("[\n");
		for(Atom atom : atoms) {
			data.append(atom).append("\n");
		}
		data.append("]");
		return data.toString();
	}
}
