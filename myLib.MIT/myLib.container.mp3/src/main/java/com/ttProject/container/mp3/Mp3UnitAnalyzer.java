package com.ttProject.container.mp3;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * unitのselector
 * @author taktod
 */
public class Mp3UnitAnalyzer implements IAnalyzer {
	/** 解析用セレクター */
	private ISelector selector = new Mp3UnitSelector();
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
