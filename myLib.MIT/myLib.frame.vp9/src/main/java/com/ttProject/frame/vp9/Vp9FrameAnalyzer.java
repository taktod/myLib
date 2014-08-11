/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp9;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;

public class Vp9FrameAnalyzer extends VideoAnalyzer {
	public Vp9FrameAnalyzer() {
		super(new Vp9FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.VP9;
	}
}
