package com.ttProject.media.mp4;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IFileReadChannel;

public abstract class ParentAtom extends Atom {
	private final List<Atom> atoms = new ArrayList<Atom>();
	public ParentAtom(String name, int size, int position) {
		super(name, size, position);
	}
	@Override
	public void analyze(IFileReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
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
	}
	public List<Atom> getAtoms() {
		return new ArrayList<Atom>(atoms);
	}
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space)).append("[\n");
		for(Atom atom : atoms) {
			data.append(atom).append("\n");
		}
		data.append(space).append("]");
		return data.toString();
	}
}
