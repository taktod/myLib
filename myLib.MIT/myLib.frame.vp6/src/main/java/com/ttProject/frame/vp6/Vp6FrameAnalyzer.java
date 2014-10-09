/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;

/**
 * analyzer for vp6 frame.
 * @author taktod
 */
public class Vp6FrameAnalyzer extends VideoAnalyzer {
	/**
	 * constructor
	 */
	public Vp6FrameAnalyzer() {
		super(new Vp6FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP6;
	}
}
