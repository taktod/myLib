package com.ttProject.container.adts;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.AacFrameSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * adtsのコンテナからaacのunitを抜き出す動作
 * @author taktod
 */
public class AdtsUnitSelector implements ISelector {
	/** 経過サンプル数を保持 */
	private long passedTic = 0;
	private ISelector aacFrameSelector = new AacFrameSelector();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		int position = channel.position();
		AacFrame frame = (AacFrame)aacFrameSelector.select(channel);
		if(frame == null) {
			return null;
		}
		AdtsUnit unit = new AdtsUnit(frame, position, passedTic);
		passedTic += frame.getSampleNum();
		return unit;
	}
}
