package com.ttProject.frame.speex;

import com.ttProject.frame.AudioAnalyzer;

/**
 * speexデータ解析
 * @author taktod
 */
public class SpeexFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public SpeexFrameAnalyzer() {
		super(new SpeexFrameSelector());
	}
}
