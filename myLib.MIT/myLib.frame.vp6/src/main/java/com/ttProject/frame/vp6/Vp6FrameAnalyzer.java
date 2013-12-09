package com.ttProject.frame.vp6;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

public class Vp6FrameAnalyzer implements IAnalyzer {
	private ISelector selector = new Vp6FrameSelector();
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}

}
