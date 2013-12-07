package com.ttProject.container.flv;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IAnalyzer;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * flvデータを解析します。(内容データもばっちり解析します)
 * @author taktod
 */
public class FlvTagAnalyzer implements IAnalyzer {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTagAnalyzer.class);
	private ISelector selector = new FlvTagSelector();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit analyze(IReadChannel channel) throws Exception {
		IUnit unit = selector.select(channel);
		return null;
	}
}
