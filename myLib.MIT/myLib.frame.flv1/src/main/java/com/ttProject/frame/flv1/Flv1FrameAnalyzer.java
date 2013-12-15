package com.ttProject.frame.flv1;


import com.ttProject.frame.VideoSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * flv1のframeを解析する動作
 * @author taktod
 */
public class Flv1FrameAnalyzer implements IAnalyzer {
	/** frameSelector */
	private VideoSelector selector = new Flv1FrameSelector();
	public VideoSelector getSelector() {
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
