package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioAnalyzer;

/**
 * adpcmswfのframeを解析する動作
 * @author taktod
 */
public class AdpcmswfFrameAnalyzer extends AudioAnalyzer {
	/**
	 * コンストラクタ
	 */
	public AdpcmswfFrameAnalyzer() {
		super(new AdpcmswfFrameSelector());
	}
}
