package com.ttProject.frame.vp8;

import com.ttProject.frame.VideoAnalyzer;

public class Vp8FrameAnalyzer extends VideoAnalyzer {
	public Vp8FrameAnalyzer() {
		super(new Vp8FrameSelector());
	}
}
