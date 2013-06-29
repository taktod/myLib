package com.ttProject.media.mkv.element;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IFileReadChannel;

public class BlockDuration extends Element {
	public BlockDuration(long position, long size, long dataPosition) {
		super(Type.BlockDuration, position, size, dataPosition);
	}
	public BlockDuration(IFileReadChannel ch) throws Exception {
		this(ch.position() - Type.BlockDuration.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IFileReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
		System.out.println(getSize());
		System.out.println(getDataPosition());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
