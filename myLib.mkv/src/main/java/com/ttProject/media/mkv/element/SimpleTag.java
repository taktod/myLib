package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class SimpleTag extends MasterElement {
	public SimpleTag(long position, long size, long dataPosition) {
		super(Type.SimpleTag, position, size, dataPosition);
	}
	public SimpleTag(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.SimpleTag.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("      +");
	}
}
