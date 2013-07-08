package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class SeekHead extends MasterElement {
	public SeekHead(long position, long size, long dataPosition) {
		super(Type.SeekHead, position, size, dataPosition);
	}
	public SeekHead(IReadChannel ch) throws Exception {
		this(ch.position() - Type.SeekHead.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
