package com.ttProject.container.mkv;

import org.apache.log4j.Logger;

import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * mkvのelementを解析して取り出すselector
 * @author taktod
 */
public class MkvElementSelector implements ISelector {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvElementSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			// データがもうない
			return null;
		}
		return null;
	}
}
