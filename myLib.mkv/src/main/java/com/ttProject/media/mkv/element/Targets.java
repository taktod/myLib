package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Targets extends MasterElement {
	public Targets(long position, long size, long dataPosition) {
		super(Type.Targets, position, size, dataPosition);
	}
	public Targets(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Targets.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
