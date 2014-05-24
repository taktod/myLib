/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.flv1;

import com.ttProject.frame.VideoAnalyzer;

/**
 * flv1のframeを解析する動作
 * @author taktod
 */
public class Flv1FrameAnalyzer extends VideoAnalyzer {
	/**
	 * コンストラクタ
	 */
	public Flv1FrameAnalyzer() {
		super(new Flv1FrameSelector());
	}
}
