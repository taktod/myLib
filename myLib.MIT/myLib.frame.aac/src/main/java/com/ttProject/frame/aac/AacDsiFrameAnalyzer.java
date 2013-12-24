package com.ttProject.frame.aac;

import com.ttProject.frame.AudioAnalyzer;

/**
 * dsiベースのaacのframeを解析する動作
 * flvとかmp4とかで利用する。
 * @author taktod
 */
public class AacDsiFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public AacDsiFrameAnalyzer() {
		super(new AacDsiFrameSelector());
	}
}
