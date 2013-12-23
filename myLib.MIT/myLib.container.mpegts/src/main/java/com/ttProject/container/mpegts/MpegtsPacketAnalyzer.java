package com.ttProject.container.mpegts;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * mpegtsPacketを解析します。
 * @author taktod
 */
public class MpegtsPacketAnalyzer implements IAnalyzer {
	private ISelector selector = new MpegtsPacketSelector();
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		if(unit != null) {
			unit.load(channel);
		}
		return unit;
	}
}
