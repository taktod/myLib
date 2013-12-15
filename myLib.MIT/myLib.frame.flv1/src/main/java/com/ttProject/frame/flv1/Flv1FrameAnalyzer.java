package com.ttProject.frame.flv1;

import com.ttProject.frame.VideoAnalyzer;

/**
 * flv1のframeを解析する動作
 * @author taktod
 */
public class Flv1FrameAnalyzer extends VideoAnalyzer {
	public Flv1FrameAnalyzer() {
		super(new Flv1FrameSelector());
	}
}
