/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.nellymoser.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector for nellymoser frame.
 * @author taktod
 *
 */
public class NellymoserFrameSelector extends AudioSelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() == channel.position()) {
			return null;
		}
		if(channel.size() % 64 != 0) {
			throw new Exception("data size is expected for multiple of 64 byte.");
		}
		NellymoserFrame frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
