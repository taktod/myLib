package com.ttProject.media.mkv.element;

import org.apache.log4j.Logger;

import com.ttProject.media.mkv.Element;
import com.ttProject.media.mkv.IElementAnalyzer;
import com.ttProject.media.mkv.Type;
import com.ttProject.nio.channels.IReadChannel;

public class BlockDuration extends Element {
	private Logger logger = Logger.getLogger(BlockDuration.class);
	public BlockDuration(long position, long size, long dataPosition) {
		super(Type.BlockDuration, position, size, dataPosition);
	}
	public BlockDuration(IReadChannel ch) throws Exception {
		this(ch.position() - Type.BlockDuration.tagSize(), Element.getSize(ch), ch.position());
	}
	@Override
	public void analyze(IReadChannel ch, IElementAnalyzer analyzer)
			throws Exception {
		logger.info(getSize());
		logger.info(getDataPosition());
	}
	@Override
	public String toString() {
		return super.toString("      ");
	}
}
