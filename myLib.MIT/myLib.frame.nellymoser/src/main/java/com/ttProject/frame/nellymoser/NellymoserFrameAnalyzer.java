/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * analyzer for nellymoser frame.
 * @author taktod
 */
public class NellymoserFrameAnalyzer extends AudioAnalyzer {
	/**
	 * constructor
	 */
	public NellymoserFrameAnalyzer() {
		super(new NellymoserFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.NELLYMOSER;
	}
}
