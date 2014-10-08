/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.aac.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector for adts aacFrame.
 * @author taktod
 */
public class AacFrameSelector extends AudioSelector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(AacFrameSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
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
