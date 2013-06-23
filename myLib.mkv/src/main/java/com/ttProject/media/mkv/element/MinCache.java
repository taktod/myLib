package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class MinCache extends Element {
	public MinCache(long position, long size, long dataPosition) {
		super(Type.MinCache, position, size, dataPosition);
	}
	public MinCache(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.MinCache.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
