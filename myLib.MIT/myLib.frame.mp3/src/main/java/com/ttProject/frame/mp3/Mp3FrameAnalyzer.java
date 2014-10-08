/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * analyzer for mp3
 * @author taktod
 */
public class Mp3FrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public Mp3FrameAnalyzer() {
		super(new Mp3FrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.MP3;
	}
}
