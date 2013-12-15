package com.ttProject.frame;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

public abstract class AudioAnalyzer implements IAnalyzer {
	private final AudioSelector selector;
	public AudioAnalyzer(AudioSelector selector) {
		this.selector = selector;
	}
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
