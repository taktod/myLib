/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.pcmalaw;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.pcmalaw.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * selector of pcm_alaw frame
 * @author taktod
 */
public class PcmalawFrameSelector extends AudioSelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		PcmalawFrame frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
