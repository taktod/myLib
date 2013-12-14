package com.ttProject.container.ogg;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * oggのデータを解析する動作
 * @author taktod
 */
public class OggPageAnalyzer implements IAnalyzer {
	private ISelector selector = new OggPageSelector();
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}
}
