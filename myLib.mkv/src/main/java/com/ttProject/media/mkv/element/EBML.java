package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class EBML extends MasterElement {
	public EBML(long position, long size, long dataPosition) {
		super(Type.EBML, position, size, dataPosition);
	}
	public EBML(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.EBML.tagSize(), Element.getSize(ch), ch.position());
	}
}
