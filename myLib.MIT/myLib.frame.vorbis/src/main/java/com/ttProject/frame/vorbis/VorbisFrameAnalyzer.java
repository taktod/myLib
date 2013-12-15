package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioAnalyzer;

public class VorbisFrameAnalyzer extends AudioAnalyzer {
	public VorbisFrameAnalyzer() {
		super(new VorbisFrameSelector());
	}
}
