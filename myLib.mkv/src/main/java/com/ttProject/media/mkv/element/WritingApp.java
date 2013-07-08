package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class WritingApp extends Element {
	public WritingApp(long position, long size, long dataPosition) {
		super(Type.WritingApp, position, size, dataPosition);
	}
	public WritingApp(IReadChannel ch) throws Exception {
		this(ch.position() - Type.WritingApp.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
