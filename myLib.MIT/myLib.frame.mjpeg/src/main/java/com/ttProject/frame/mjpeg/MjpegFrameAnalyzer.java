/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mjpeg;

import com.ttProject.frame.CodecType;
import com.ttProject.frame.VideoAnalyzer;

/**
 * analyzer for mjpeg frame.
 * @author taktod
 */
public class MjpegFrameAnalyzer extends VideoAnalyzer {
	/**
	 * constructor
	 */
	public MjpegFrameAnalyzer() {
		super(new MjpegFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.MJPEG;
	}
}
