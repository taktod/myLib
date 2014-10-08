/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.flv1;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;

/**
 * analyzer for flv1 frame.
 * @author taktod
 */
public class Flv1FrameAnalyzer extends VideoAnalyzer {
	/**
	 * constructor
	 */
	public Flv1FrameAnalyzer() {
		super(new Flv1FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.FLV1;
	}
}
