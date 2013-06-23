package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Segment extends MasterElement {
	public Segment(long position, long size, long dataPosition) {
		super(Type.Segment, position, size, dataPosition);
	}
	public Segment(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Segment.tagSize(), Element.getSize(ch), ch.position());
	}
}
