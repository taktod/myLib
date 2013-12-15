package com.ttProject.frame.vp8;

import com.ttProject.frame.VideoSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

public class Vp8FrameAnalyzer implements IAnalyzer {
	private VideoSelector selector = new Vp8FrameSelector();
	public VideoSelector getSelector() {
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
