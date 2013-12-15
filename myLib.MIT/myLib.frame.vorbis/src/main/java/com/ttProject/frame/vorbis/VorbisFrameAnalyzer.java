package com.ttProject.frame.vorbis;

import com.ttProject.frame.AudioSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

public class VorbisFrameAnalyzer implements IAnalyzer {
	private AudioSelector selector = new VorbisFrameSelector();
	public AudioSelector getSelector() {
		return selector;
	}
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		return null;
	}

}
