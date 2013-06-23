package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Tracks extends MasterElement {
	public Tracks(long position, long size, long dataPosition) {
		super(Type.Tracks, position, size, dataPosition);
	}
	public Tracks(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Tracks.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("  ");
	}
}
