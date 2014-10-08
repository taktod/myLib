/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmmulaw;

import java.nio.ByteBuffer;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.pcmmulaw.type.Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * pcm_mulaw frame
 * G711U of flash
 * @author taktod
 */
public abstract class PcmmulawFrame extends AudioFrame {
	/**
	 * ref the muted frame.
	 * @param sampleRate
	 * @param channels
	 * @param bitSize
	 * @return
	 * @throws Exception
	 */
	public static Frame getMutedFrame(int sampleRate, int channels, int bitSize) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(256);
		for(int i = 0;i < buffer.limit();i ++) {
			buffer.put((byte)0xFF);
		}
		buffer.flip();
		IReadChannel channel = new ByteReadChannel(buffer);
		Frame frame = new Frame();
		frame.setChannel(channels);
		frame.setSampleRate(sampleRate);
		frame.setBit(bitSize);
		frame.minimumLoad(channel);
		frame.load(channel);
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.PCM_MULAW;
	}
}
