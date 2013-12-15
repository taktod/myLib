package com.ttProject.frame.aac;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.IUnit;

/**
 * dsiベースのaacのframeを解析する動作
 * flvとかmp4とかで利用する。
 * @author taktod
 */
public class AacDsiFrameAnalyzer implements IAnalyzer {
	private AacDsiFrameSelector selector = new AacDsiFrameSelector();
	
	/**
	 * selector参照
	 * @return
	 */
	public AacDsiFrameSelector getSelector() {
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
