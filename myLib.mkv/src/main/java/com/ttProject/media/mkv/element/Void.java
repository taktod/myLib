package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Void extends Element {
	public Void(long position, long size, long dataPosition) {
		super(Type.Void, position, size, dataPosition);
	}
	public Void(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Void.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
}
