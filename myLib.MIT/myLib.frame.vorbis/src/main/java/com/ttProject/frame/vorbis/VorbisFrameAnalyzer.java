/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;
import com.ttProject.util.BufferUtil;

/**
 * analyze for vorbis frame.
 * @author taktod
 */
public class VorbisFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public VorbisFrameAnalyzer() {
		super(new VorbisFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPrivateData(IReadChannel channel) throws Exception {
		// reset the selector
		setSelector(new VorbisFrameSelector());
		// for xuggle, IStreamCoder need to have this information.
		BitLoader loader = new BitLoader(channel);
		Bit8 count = new Bit8();
		Bit8 identificationHeaderSize = new Bit8();
		Bit8 commentHeaderSize = new Bit8();
		loader.load(count, identificationHeaderSize, commentHeaderSize);
		if(count.get() != 2) {
			throw new Exception("count num is unexpected.");
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
		return CodecType.VORBIS;
	}
}
