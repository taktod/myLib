package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

public abstract class VideoAnalyzer implements IAnalyzer {
	private final VideoSelector selector;
	public VideoAnalyzer(VideoSelector selector) {
		this.selector = selector;
	}
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
