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
 * pcm_mulawのframeを解析する動作
 * @author taktod
 */
public class PcmmulawFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
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
