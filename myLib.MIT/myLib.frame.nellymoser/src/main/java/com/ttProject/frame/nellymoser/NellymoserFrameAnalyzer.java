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
