package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.MasterElement;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class BlockGroup extends MasterElement {
	public BlockGroup(long position, long size, long dataPosition) {
		super(Type.BlockGroup, position, size, dataPosition);
	}
	public BlockGroup(IReadChannel ch) throws Exception {
		this(ch.position() - Type.BlockGroup.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public String toString() {
		return super.toString("    ");
	}
}
