package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class DisplayWidth extends Element {
	public DisplayWidth(long position, long size, long dataPosition) {
		super(Type.DisplayWidth, position, size, dataPosition);
	}
	public DisplayWidth(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.DisplayWidth.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("        ");
	}
}
