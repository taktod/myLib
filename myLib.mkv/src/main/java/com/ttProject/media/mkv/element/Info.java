package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Info extends MasterElement {
	public Info(long position, long size, long dataPosition) {
		super(Type.Info, position, size, dataPosition);
	}
	public Info(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Info.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
