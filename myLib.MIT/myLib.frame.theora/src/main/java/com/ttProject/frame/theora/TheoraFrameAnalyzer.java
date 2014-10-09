/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import org.apache.log4j.Logger;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * analyzer for theora frame.
 * @author taktod
 */
public class TheoraFrameAnalyzer extends VideoAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(TheoraFrameAnalyzer.class);
	/**
	 * constructor
	 */
	public TheoraFrameAnalyzer() {
		super(new TheoraFrameSelector());
	}
	/**
	 * set the private data for theora.
	 * @param channel
	 * @throws Exception
	 */
	public void setPrivateData(IReadChannel channel) throws Exception {
		setSelector(new TheoraFrameSelector());
		BitLoader loader = new BitLoader(channel);
		Bit8 count = new Bit8();
		Bit8 identificationHeaderSize = new Bit8();
		Bit8 commentHeaderSize = new Bit8();
		loader.load(count, identificationHeaderSize, commentHeaderSize);
		if(count.get() != 2) {
			throw new Exception("count num is not much for theora privateData");
		}
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, identificationHeaderSize.get())));
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, commentHeaderSize.get())));
		analyze(new ByteReadChannel(BufferUtil.safeRead(channel, channel.size() - channel.position())));
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.THEORA;
	}
}
