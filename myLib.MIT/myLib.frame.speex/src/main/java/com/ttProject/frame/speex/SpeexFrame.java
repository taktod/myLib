/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex;

import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * speex frame
 * @author taktod
 */
public abstract class SpeexFrame extends AudioFrame {
	/** header frame */
	private HeaderFrame headerFrame = null;
	/**
	 * set the headerFrame
	 * @param headerFrame
	 */
	public void setHeaderFrame(HeaderFrame headerFrame) {
		this.headerFrame = headerFrame;
		super.setBit(headerFrame.getBit());
		super.setChannel(headerFrame.getChannel());
		super.setSampleNum(headerFrame.getSampleNum());
		super.setSampleRate(headerFrame.getSampleRate());
	}
	/**
	 * ref the headerFrame
	 * @return
	 */
	protected HeaderFrame getHeaderFrame() {
		return headerFrame;
	}
	public abstract boolean isComplete();
	/**
	 * get the muted frame.
	 * @param sampleRate
	 * @param channels
	 * @param bitSize
	 * @return
	 */
	public static Frame getMutedFrame(int sampleRate, int channels, int bitSize) throws Exception {
		String bufferString = null;
		if(channels != 1) {
			throw new RuntimeException("channels is 1 only.");
		}
		switch(sampleRate) {
		case 16000:
			bufferString = "3E9D1B9A2008017FFFFFFFFFFFDB6DB6DB6DB68400BFFFFFFFFFFFEDB6DB6DB6DB42005FFFFFFFFFFFF6DB6DB6DB6DA1002FFFFFFFFFFFFB6DB6DB6DB6DC3B60ABABABABABABABABABAB0ABABABABABABABABABAB0ABABABABABABABABABAB0ABABABABABABABABABAB7";
			break;
		default:
			throw new RuntimeException("sampleRate is unexpeted.:" + sampleRate);
		}
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer(bufferString));
		Frame frame = new Frame();
		// how about header frame?
		// frame.setHeaderFrame(headerFrame);
		frame.minimumLoad(channel);
		frame.load(channel);
		frame.setChannel(channels);
		frame.setBit(16);
		frame.setSampleRate(sampleRate);
		return frame;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.SPEEX;
	}
}
