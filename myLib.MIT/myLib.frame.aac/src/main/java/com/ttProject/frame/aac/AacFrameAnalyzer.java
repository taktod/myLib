package com.ttProject.frame.aac;

import com.ttProject.frame.AudioAnalyzer;

public class AacFrameAnalyzer extends AudioAnalyzer {
	public AacFrameAnalyzer() {
		super(new AacFrameSelector());
	}
}
