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
 * analyzer of adpcmswf frame.
 * @author taktod
 */
public class AdpcmswfFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
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
