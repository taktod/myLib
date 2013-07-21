package com.ttProject.media.aac;

import java.nio.ByteBuffer;

import com.ttProject.media.IAnalyzer;
import com.ttProject.media.Unit;
import com.ttProject.nio.channels.IReadChannel;

public abstract class Frame extends Unit {
	public Frame(final int position, final int size) {
		super(position, size);
	}
	@Override
	public void analyze(IReadChannel ch, IAnalyzer<?> analyzer)
			throws Exception {
	}
	public abstract void analyze(IReadChannel ch, IFrameAnalyzer analyzer) throws Exception;
	public abstract ByteBuffer getBuffer() throws Exception;
}
