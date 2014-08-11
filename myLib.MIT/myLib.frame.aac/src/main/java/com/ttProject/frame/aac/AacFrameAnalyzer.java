/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.CodecType;

/**
 * adts形式のaacFrame解析動作
 * @author taktod
 */
public class AacFrameAnalyzer extends AudioAnalyzer {
	public AacFrameAnalyzer() {
		super(new AacFrameSelector());
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CodecType getCodecType() {
		return CodecType.AAC;
	}
}
