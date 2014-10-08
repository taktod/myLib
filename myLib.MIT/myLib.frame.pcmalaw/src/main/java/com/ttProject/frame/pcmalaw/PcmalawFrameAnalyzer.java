/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmalaw;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * analyzer for pcm_alaw
 * @author taktod
 */
public class PcmalawFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public PcmalawFrameAnalyzer() {
		super(new PcmalawFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.PCM_ALAW;
	}
}
