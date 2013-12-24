package com.ttProject.container.adts;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * unitのselector
 * @author taktod
 */
public class AdtsUnitAnalyzer implements IAnalyzer {
	/** 対応セレクター */
	private ISelector selector = new AdtsUnitSelector();
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
