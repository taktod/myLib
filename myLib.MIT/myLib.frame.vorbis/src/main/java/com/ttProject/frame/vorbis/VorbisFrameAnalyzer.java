/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioAnalyzer;

public class VorbisFrameAnalyzer extends AudioAnalyzer {
	public VorbisFrameAnalyzer() {
		super(new VorbisFrameSelector());
	}
}
