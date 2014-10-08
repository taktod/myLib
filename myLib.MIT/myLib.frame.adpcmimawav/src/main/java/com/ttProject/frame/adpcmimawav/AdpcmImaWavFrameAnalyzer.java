/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * analyzer of adpcmImaWavFrame
 * @author taktod
 */
public class AdpcmImaWavFrameAnalyzer extends AudioAnalyzer  {
	public AdpcmImaWavFrameAnalyzer() {
		super(new AdpcmImaWavSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.ADPCM_IMA_WAV;
	}
}
