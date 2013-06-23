package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class CueTrackPositions extends MasterElement {
	public CueTrackPositions(long position, long size, long dataPosition) {
		super(Type.CueTrackPositions, position, size, dataPosition);
	}
	public CueTrackPositions(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.CueTrackPositions.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
