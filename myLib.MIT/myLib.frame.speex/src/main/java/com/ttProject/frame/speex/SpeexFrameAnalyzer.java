/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.NullFrame;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * analyzer for speex frame
 * @author taktod
 */
public class SpeexFrameAnalyzer extends AudioAnalyzer {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(SpeexFrameAnalyzer.class);
	private SpeexFrame tmpFrame = null;
	/**
	 * constructor
	 */
	public SpeexFrameAnalyzer() {
		super(new SpeexFrameSelector());
	}
	/**
	 * analyze frame.
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		IAudioFrame frame = null;
		if(tmpFrame != null) {
			frame = tmpFrame;
			frame.load(channel);
		}
		else {
			frame = (IAudioFrame)super.analyze(channel);
		}
		if(frame == null) {
			tmpFrame = null;
			return null;
		}
		if(frame instanceof CommentFrame) {
			CommentFrame cFrame = (CommentFrame)frame;
			if(!cFrame.isComplete()) {
				tmpFrame = cFrame;
				return NullFrame.getInstance();
			}
			else {
				tmpFrame = null;
				return cFrame;
			}
		}
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
