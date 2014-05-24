/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3;

import com.ttProject.frame.AudioAnalyzer;

/**
 * mp3のframeを解析する動作
 * @author taktod
 */
public class Mp3FrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public Mp3FrameAnalyzer() {
		super(new Mp3FrameSelector());
	}
}
