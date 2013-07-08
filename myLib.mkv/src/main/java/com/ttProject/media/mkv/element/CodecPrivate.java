package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class CodecPrivate extends Element {
	public CodecPrivate(long position, long size, long dataPosition) {
		super(Type.CodecPrivate, position, size, dataPosition);
	}
	public CodecPrivate(IReadChannel ch) throws Exception {
		this(ch.position() - Type.CodecPrivate.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
