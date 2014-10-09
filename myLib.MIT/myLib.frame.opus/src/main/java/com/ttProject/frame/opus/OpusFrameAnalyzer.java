/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.opus;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * analyzer for opus frame.
 * @author taktod
 */
public class OpusFrameAnalyzer extends AudioAnalyzer {
	private OpusFrame tmpFrame = null;
	/**
	 * constructor
	 */
	public OpusFrameAnalyzer() {
		super(new OpusFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		OpusFrame frame = null;
		if(tmpFrame != null) {
			frame = tmpFrame;
			frame.load(channel);
		}
		else {
			frame = (OpusFrame)super.analyze(channel);
		}
		if(!frame.isComplete()) {
			tmpFrame = frame;
		}
		else {
			tmpFrame = null;
		}
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.OPUS;
	}
}
