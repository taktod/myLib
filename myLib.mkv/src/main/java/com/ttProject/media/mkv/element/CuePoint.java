package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class CuePoint extends MasterElement {
	public CuePoint(long position, long size, long dataPosition) {
		super(Type.CuePoint, position, size, dataPosition);
	}
	public CuePoint(IReadChannel ch) throws Exception {
		this(ch.position() - Type.CuePoint.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
