package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Tag extends MasterElement {
	public Tag(long position, long size, long dataPosition) {
		super(Type.Tag, position, size, dataPosition);
	}
	public Tag(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Tag.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
