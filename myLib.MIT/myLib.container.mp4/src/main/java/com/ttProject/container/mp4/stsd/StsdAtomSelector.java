package com.ttProject.container.mp4.stsd;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * stsdが保持しているatomの内部データ解析
 * @author taktod
 */
public class StsdAtomSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(StsdAtomSelector.class);
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		return null;
	}
}
