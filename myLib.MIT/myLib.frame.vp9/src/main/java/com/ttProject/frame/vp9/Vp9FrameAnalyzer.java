package com.ttProject.frame.vp9;

import com.ttProject.frame.VideoAnalyzer;

public class Vp9FrameAnalyzer extends VideoAnalyzer {
	public Vp9FrameAnalyzer() {
		super(new Vp9FrameSelector());
	}
}