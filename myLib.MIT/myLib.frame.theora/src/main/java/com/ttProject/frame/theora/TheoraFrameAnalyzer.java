package com.ttProject.frame.theora;

import com.ttProject.frame.AudioAnalyzer;

public class TheoraFrameAnalyzer extends AudioAnalyzer {
	public TheoraFrameAnalyzer() {
		super(new TheoraFrameSelector());
	}
}
