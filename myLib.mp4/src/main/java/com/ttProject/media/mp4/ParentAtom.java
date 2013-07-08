package com.ttProject.media.mp4;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IReadChannel;

/**
 * 子要素を持つAtom
 * @author taktod
 */
public abstract class ParentAtom extends Atom {
	/** 子要素一覧 */
	private final List<Atom> atoms = new ArrayList<Atom>();
	/**
	 * コンストラクタ
	 * @param name
	 * @param size
	 * @param position
	 */
	public ParentAtom(String name, int size, int position) {
		super(name, size, position);
	}
	/**
	 * 解析
	 */
	@Override
	public void analyze(IReadChannel ch, IAtomAnalyzer analyzer)
			throws Exception {
		if(analyzer == null) {
			return;
		}
		ch.position(getPosition() + 8);
		Atom atom = null;
		while((atom = analyzer.analyze(ch)) != null) {
			atoms.add(atom);
			if(ch.position() >= getPosition() + getSize()) {
				break;
			}
		}
	}
	/**
	 * 子要素一覧参照
	 * @return
	 */
	public List<Atom> getAtoms() {
		return new ArrayList<Atom>(atoms);
	}
	/**
	 * {@inheritDoc}
	 */
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
