/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmmulaw;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.pcmmulaw.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector of pcm_mulaw frame
 * @author taktod
 */
public class PcmmulawFrameSelector extends AudioSelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		PcmmulawFrame frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
