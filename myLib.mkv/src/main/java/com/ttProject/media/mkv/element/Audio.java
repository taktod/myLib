package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Audio extends MasterElement {
	public Audio(long position, long size, long dataPosition) {
		super(Type.Audio, position, size, dataPosition);
	}
	public Audio(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Audio.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
