package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class Cues extends MasterElement {
	public Cues(long position, long size, long dataPosition) {
		super(Type.Cues, position, size, dataPosition);
	}
	public Cues(IReadChannel ch) throws Exception {
		this(ch.position() - Type.Cues.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
