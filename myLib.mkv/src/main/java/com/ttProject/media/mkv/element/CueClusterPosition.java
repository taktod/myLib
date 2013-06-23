package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class CueClusterPosition extends Element {
	public CueClusterPosition(long position, long size, long dataPosition) {
		super(Type.CueClusterPosition, position, size, dataPosition);
	}
	public CueClusterPosition(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.CueClusterPosition.tagSize(), Element.getSize(ch), ch.position());
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
