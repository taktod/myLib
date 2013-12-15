package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * adpcmswfのframeを解析する動作
 * @author taktod
 */
public class AdpcmswfFrameAnalyzer implements IAnalyzer {
	private AudioSelector selector = new AdpcmswfFrameSelector();
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
