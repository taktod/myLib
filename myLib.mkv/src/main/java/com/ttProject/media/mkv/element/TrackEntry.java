package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class TrackEntry extends MasterElement {
	public TrackEntry(long position, long size, long dataPosition) {
		super(Type.TrackEntry, position, size, dataPosition);
	}
	public TrackEntry(IReadChannel ch) throws Exception {
		this(ch.position() - Type.TrackEntry.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
