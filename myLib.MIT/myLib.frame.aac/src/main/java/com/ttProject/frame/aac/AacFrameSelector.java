package com.ttProject.frame.aac;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * adts形式のaacFrame選択動作
 * @author taktod
 *
 */
public class AacFrameSelector extends AudioSelector {
	/** ロガー */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacFrameSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// adtsのヘッダデータを解析します。
		if(channel.position() == channel.size()) {
			return null;
		}
		AacFrame frame = null;
		frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
