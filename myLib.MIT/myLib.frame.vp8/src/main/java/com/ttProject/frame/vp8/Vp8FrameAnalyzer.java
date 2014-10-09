/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp8;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;

/**
 * analyzer for vp8 frame.
 * @author taktod
 */
public class Vp8FrameAnalyzer extends VideoAnalyzer {
	/**
	 * constructor
	 */
	public Vp8FrameAnalyzer() {
		super(new Vp8FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP8;
	}
}
