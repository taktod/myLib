/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav.type;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import com.ttProject.frame.adpcmimawav.AdpcmImaWavFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * frame
 * 16bit first predictor
 * 8bit first index
 * 8bit reservedBit(0x00)
 * 16bit first predictor(right)
 * 8bit first index(right)
 * 8bit reservedBit(0x00)
 * 
 * 4bit left 4bit right 4bit left ....
 * for monoral only left, right data is missing.
 * 
 * @author taktod
 */
public class Frame extends AdpcmImaWavFrame {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Frame.class);
	/** data buffer */
	private ByteBuffer buffer = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer getPackBuffer() throws Exception {
		return null;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.setSize(channel.size());
		// timebase will be the same as sampleRate
		super.setTimebase(getSampleRate());
		// sampleNum will be calcurate by byte size.
		switch(getChannel()) {
		case 1: // monoral.
			// first 4byte + sample data.
			super.setSampleNum((channel.size() - 4) * 2 + 1);
			break;
		case 2: // stereo
			// first 8byte + sample data.
			super.setSampleNum((channel.size() - 8) + 1);
			break;
		default:
			throw new RuntimeException("only for stereo or monoral.");
		}
		super.setBit(16); // 16bit force.
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
		buffer = BufferUtil.safeRead(channel, channel.size());
		super.update();
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
		setData(buffer);
	}
}
