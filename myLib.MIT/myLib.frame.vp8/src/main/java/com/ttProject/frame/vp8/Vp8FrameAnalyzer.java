/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp8;

import com.ttProject.frame.VideoAnalyzer;

public class Vp8FrameAnalyzer extends VideoAnalyzer {
	public Vp8FrameAnalyzer() {
		super(new Vp8FrameSelector());
	}
}
