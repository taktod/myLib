package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class DateUTC extends Element {
	public DateUTC(long position, long size, long dataPosition) {
		super(Type.DateUTC, position, size, dataPosition);
	}
	public DateUTC(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.DateUTC.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
