package com.ttProject.media.mkv;

import java.util.ArrayList;
import java.util.List;

import com.ttProject.nio.channels.IFileReadChannel;

/**
 * 子要素を持つElement
 * @author taktod
 */
public abstract class MasterElement extends Element {
	/** 子要素一覧 */
	private final List<Element> elements = new ArrayList<Element>();
	public MasterElement(Type type, long position, long size, long dataPosition) {
		super(type, position, size, dataPosition);
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
		if(analyzer == null) {
			return;
		}
		ch.position((int)getDataPosition());
		Element element = null;
		while((element = analyzer.analyze(ch)) != null) {
			elements.add(element);
			if(ch.position() >= getDataPosition() + getSize()) {
				break;
			}
		}
	}
	public List<Element> getElements() {
		return new ArrayList<Element>(elements);
	}
	@Override
	public String toString(String space) {
		StringBuilder data = new StringBuilder();
		data.append(super.toString(space)).append("[\n");
		for(Element element : elements) {
			data.append(element).append("\n");
		}
		data.append(space).append("]");
		return data.toString();
	}
}
