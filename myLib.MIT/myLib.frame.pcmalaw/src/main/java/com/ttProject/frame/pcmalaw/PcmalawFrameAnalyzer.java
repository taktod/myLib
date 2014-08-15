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
 * pcm_alawのframeを解析する動作
 * @author taktod
 */
public class PcmalawFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
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
