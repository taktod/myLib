package com.ttProject.frame.mp3;

import com.ttProject.frame.AudioSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * mp3のframeを解析する動作
 * @author taktod
 */
public class Mp3FrameAnalyzer implements IAnalyzer {
	/** frameSelector */
	private AudioSelector selector = new Mp3FrameSelector();
	public AudioSelector getSelector() {
		return selector;
	}
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
