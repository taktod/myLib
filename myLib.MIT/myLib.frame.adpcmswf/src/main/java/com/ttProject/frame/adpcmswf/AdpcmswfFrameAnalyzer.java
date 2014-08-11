/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * adpcmswfのframeを解析する動作
 * @author taktod
 */
public class AdpcmswfFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public AdpcmswfFrameAnalyzer() {
		super(new AdpcmswfFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.ADPCM_SWF;
	}
}
