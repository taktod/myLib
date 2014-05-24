/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
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
		analyze(ch, (IFrameAnalyzer)null);
	}
	public abstract void analyze(IReadChannel ch, IFrameAnalyzer analyzer) throws Exception;
	public abstract ByteBuffer getBuffer() throws Exception;
}
