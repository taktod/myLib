package com.ttProject.frame.flv1;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * flv1のframeを解析する動
 * @author taktod
 */
public class Flv1FrameAnalyzer implements IAnalyzer {
	/** frameSelector */
	private ISelector selector = new Flv1FrameSelector();
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
