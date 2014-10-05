/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.adts;

import com.ttProject.frame.aac.AacFrame;
import com.ttProject.frame.aac.AacFrameSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * selector for adts.
 * @author taktod
 */
public class AdtsUnitSelector implements ISelector {
	/** passed samplenum */
	private long passedTic = 0;
	/** selector for aac frame. */
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
