package com.ttProject.frame.speex;

import com.ttProject.frame.AudioSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

public class SpeexFrameAnalyzer implements IAnalyzer {
	private AudioSelector selector = new SpeexFrameSelector();
	public AudioSelector getSelector() {
		return selector;
	}
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}

}
