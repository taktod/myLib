/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmmulaw;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * analyzer of pcm_mulaw frame
 * @author taktod
 */
public class PcmmulawFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public PcmmulawFrameAnalyzer() {
		super(new PcmmulawFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.PCM_ALAW;
	}
}
