/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.nellymoser.type.Frame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * nellymoser frame
 * @author taktod
 */
public abstract class NellymoserFrame extends AudioFrame {
	/**
	 * muted frame sample.
	 * @param sampleRate
	 * @param channels
	 * @param bitSize
	 * @return
	 */
	public static Frame getMutedFrame(int sampleRate, int channels, int bitSize) throws Exception {
		String bufferString = null;
		if(channels != 1) {
			throw new RuntimeException("supported for monoral only.");
		}
		switch(sampleRate) {
		case 44100:
			bufferString = "408A31C618638C31C618638C31C6B8A9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFFFF6FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAFFFFFFFFFFFF";
			break;
		case 22050:
			bufferString = "408A31C618638C31C618638C31C6B8A9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFFFF6FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAFFFFFFFFFFFF";
			break;
		case 11025:
			bufferString = "408A31C618638C31C618638C31C6B8A9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFFFF6FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAFFFFFFFFFFFF";
			break;
		case 8000:
			bufferString = "408A31C618638C31C618638C31C6B8A9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFFFF6FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAFFFFFFFFFFFF";
			break;
		case 16000:
			bufferString = "408A31C618638C31C618638C31C6B8A9AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFFFFFFFFFFFF6FAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAFFFFFFFFFFFF";
			break;
		default:
			throw new RuntimeException("unexpected sampleRate:" + sampleRate);
		}
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer(bufferString));
		Frame frame = new Frame();
		frame.minimumLoad(channel);
		frame.load(channel);
		frame.setChannel(1);
		frame.setSampleRate(sampleRate);
		frame.setBit(16);
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.NELLYMOSER;
	}
}
