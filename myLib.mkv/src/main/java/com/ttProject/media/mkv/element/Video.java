package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class Video extends MasterElement {
	public Video(long position, long size, long dataPosition) {
		super(Type.Video, position, size, dataPosition);
	}
	public Video(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.Video.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
