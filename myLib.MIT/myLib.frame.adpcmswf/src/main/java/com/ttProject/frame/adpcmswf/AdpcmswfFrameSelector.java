/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmswf;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.adpcmswf.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * adpcmのframe選択動作
 * @author taktod
 */
public class AdpcmswfFrameSelector extends AudioSelector {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.position() == channel.size()) {
			return null;
		}
		AdpcmswfFrame frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
