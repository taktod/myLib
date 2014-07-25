/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioAnalyzer;

public class AdpcmImaWavFrameAnalyzer extends AudioAnalyzer  {
	public AdpcmImaWavFrameAnalyzer() {
		super(new AdpcmImaWavSelector());
	}
}
