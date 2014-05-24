/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser;

import com.ttProject.frame.AudioAnalyzer;

/**
 * nellymoserFrameの解析を実行するプログラム
 * @author taktod
 */
public class NellymoserFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public NellymoserFrameAnalyzer() {
		super(new NellymoserFrameSelector());
	}
}
