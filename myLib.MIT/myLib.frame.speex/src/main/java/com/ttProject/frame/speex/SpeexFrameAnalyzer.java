package com.ttProject.frame.speex;

import com.ttProject.frame.AudioAnalyzer;

public class SpeexFrameAnalyzer extends AudioAnalyzer {
	public SpeexFrameAnalyzer() {
		super(new SpeexFrameSelector());
	}
}
