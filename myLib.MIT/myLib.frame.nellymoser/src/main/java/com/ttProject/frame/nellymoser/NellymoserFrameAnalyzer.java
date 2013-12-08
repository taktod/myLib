package com.ttProject.frame.nellymoser;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * nellymoserFrameの解析を実行するプログラム
 * @author taktod
 */
public class NellymoserFrameAnalyzer implements IAnalyzer {
	/** selector */
	private ISelector selector = new NellymoserFrameSelector();
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
