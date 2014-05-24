/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.theora;

import com.ttProject.frame.AudioAnalyzer;

public class TheoraFrameAnalyzer extends AudioAnalyzer {
	public TheoraFrameAnalyzer() {
		super(new TheoraFrameSelector());
	}
}
