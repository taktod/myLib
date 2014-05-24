/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6;

import com.ttProject.frame.VideoAnalyzer;

/**
 * vp6のframe解析
 * @author taktod
 */
public class Vp6FrameAnalyzer extends VideoAnalyzer {
	/**
	 * コンストラクタ
	 */
	public Vp6FrameAnalyzer() {
		super(new Vp6FrameSelector());
	}
}
