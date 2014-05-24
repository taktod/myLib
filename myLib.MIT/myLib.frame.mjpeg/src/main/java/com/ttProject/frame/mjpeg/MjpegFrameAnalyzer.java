/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mjpeg;

import com.ttProject.frame.VideoAnalyzer;

/**
 * mjpegのframe解析
 * @author taktod
 */
public class MjpegFrameAnalyzer extends VideoAnalyzer {
	/**
	 * コンストラクタ
	 */
	public MjpegFrameAnalyzer() {
		super(new MjpegFrameSelector());
	}
}
