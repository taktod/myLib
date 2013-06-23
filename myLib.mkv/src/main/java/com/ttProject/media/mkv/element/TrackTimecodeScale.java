package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class TrackTimecodeScale extends Element {
	public TrackTimecodeScale(long position, long size, long dataPosition) {
		super(Type.TrackTimecodeScale, position, size, dataPosition);
	}
	public TrackTimecodeScale(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.TrackTimecodeScale.tagSize(), Element.getSize(ch), ch.position());
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
