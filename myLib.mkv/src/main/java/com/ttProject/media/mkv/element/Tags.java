package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Tags extends MasterElement {
	public Tags(long position, long size, long dataPosition) {
		super(Type.Tags, position, size, dataPosition);
	}
	public Tags(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Tags.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
