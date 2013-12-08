package com.ttProject.frame.mp3;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * mp3のframeを解析する動作
 * @author taktod
 */
public class Mp3FrameAnalyzer implements IAnalyzer {
	/** frameSelector */
	private ISelector selector = new Mp3FrameSelector();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}
}
