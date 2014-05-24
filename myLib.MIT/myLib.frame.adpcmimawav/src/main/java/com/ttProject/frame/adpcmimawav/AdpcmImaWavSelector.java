/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.adpcmimawav;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.adpcmimawav.type.Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;

/**
 * 
 * @author taktod
 */
public class AdpcmImaWavSelector extends AudioSelector {
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		Frame frame = new Frame();
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
