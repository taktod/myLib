/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mp3;

import com.ttProject.frame.mp3.Mp3Frame;
import com.ttProject.frame.mp3.Mp3FrameSelector;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.ISelector;
import com.ttProject.unit.IUnit;

/**
 * get mp3unit selector.
 * @author taktod
 */
public class Mp3UnitSelector implements ISelector {
	/** passed sampleNum */
	private long passedTic = 0;
	/** mp3 frame selector */
	private ISelector mp3FrameSelector = new Mp3FrameSelector();
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		int position = channel.position();
		Mp3Frame frame = (Mp3Frame)mp3FrameSelector.select(channel);
		if(frame == null) {
			return null;
		}
		Mp3Unit unit = new Mp3Unit(frame, position, passedTic);
		passedTic += frame.getSampleNum();
		return unit;
	}
}
