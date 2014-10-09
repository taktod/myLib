/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.riff.type;

import org.apache.log4j.Logger;

import com.ttProject.container.riff.IFrameEventListener;
import com.ttProject.container.riff.RiffFormatUnit;
import com.ttProject.container.riff.RiffSizeUnit;
import com.ttProject.container.riff.Type;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.IAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.BufferUtil;

/**
 * data
 * ex: stereo L R L R L R...
 * @author taktod
 */
public class Data extends RiffSizeUnit {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Data.class);
	/** passedTic passedSampleNum */
	private long passedTic = 0;
	public Data() {
		super(Type.DATA);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void minimumLoad(IReadChannel channel) throws Exception {
		super.minimumLoad(channel);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(IReadChannel channel) throws Exception {
	}
	public void analyzeFrame(IReadChannel channel, IFrameEventListener listener) throws Exception {
		RiffFormatUnit formatUnit = getFormatUnit();
		while(channel.position() < channel.size()) {
			int blockSize = 0;
			// for pcm_alaw or pcm_mulaw, data size 1, is too small.
			switch(formatUnit.getCodecType()) {
			case PCM_ALAW:
			case PCM_MULAW:
				blockSize = 0x0100; // force 256 for 1chunk.
				if(channel.size() - channel.position() < 0x0100) {
					blockSize = channel.size() - channel.position();
				}
				break;
			default:
				blockSize = formatUnit.getBlockSize();
				break;
			}
			IReadChannel frameChannel = new ByteReadChannel(BufferUtil.safeRead(channel, blockSize));
			IAnalyzer analyzer = formatUnit.getFrameAnalyzer();
			IFrame frame = analyzer.analyze(frameChannel);
			if(frame instanceof AudioFrame) {
				AudioFrame aFrame = (AudioFrame) frame;
				passedTic += aFrame.getSampleNum();
				aFrame.setPts(passedTic);
			}
			if(listener != null) {
				listener.onNewFrame(frame);
			}
		}
/*		Fmt fmt = getFmt();
		while(channel.position() < channel.size()) {
			// for pcm_alaw or pcm_mulaw, data size 1, one byte = 1 sample.
			int blockSize = 0;
			switch(fmt.getRiffCodecType()) {
			case A_LAW:
			case U_LAW:
				blockSize = 0x0100; // force 256 for 1 chunk.
				if(channel.size() - channel.position() < 0x0100) {
					blockSize = channel.size() - channel.position();
				}
				break;
			default:
				blockSize = fmt.getBlockSize();
				break;
			}
			ByteReadChannel frameChannel = new ByteReadChannel(BufferUtil.safeRead(channel, blockSize));
			IAnalyzer analyzer = getFmt().getFrameAnalyzer();
			IFrame frame = analyzer.analyze(frameChannel);
			if(frame instanceof AudioFrame) {
				AudioFrame aFrame = (AudioFrame) frame;
				passedTic += aFrame.getSampleNum();
				aFrame.setPts(passedTic);
			}
			if(listener != null) {
				listener.onNewFrame(frame);
			}
		}*/
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void requestUpdate() throws Exception {
	}
}
