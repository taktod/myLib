package com.ttProject.media.mp3;

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
}
