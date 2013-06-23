package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Seek extends MasterElement {
	public Seek(long position, long size, long dataPosition) {
		super(Type.Seek, position, size, dataPosition);
	}
	public Seek(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Seek.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
