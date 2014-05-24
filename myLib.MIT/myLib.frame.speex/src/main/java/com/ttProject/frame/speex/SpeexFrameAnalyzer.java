/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex;

import com.ttProject.frame.AudioAnalyzer;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.IReadChannel;

/**
 * speexデータ解析
 * @author taktod
 */
public class SpeexFrameAnalyzer extends AudioAnalyzer {
	private SpeexFrame tmpFrame = null;
	/**
	 * コンストラクタ
	 */
	public SpeexFrameAnalyzer() {
		super(new SpeexFrameSelector());
	}
	/**
	 * フレームが読み込み途上だったらそっちの続き読み込みを実施しなければいけないので、analyzeを別途作成します。
	 */
	@Override
	public IFrame analyze(IReadChannel channel) throws Exception {
		SpeexFrame frame = null;
		if(tmpFrame != null) {
			frame = tmpFrame;
			frame.load(channel);
		}
		else {
			frame = (SpeexFrame)super.analyze(channel);
		}
		// データを評価する
		if(!frame.isComplete()) {
			tmpFrame = frame;
		}
		else {
			tmpFrame = null;
		}
		return frame;
	}
}
