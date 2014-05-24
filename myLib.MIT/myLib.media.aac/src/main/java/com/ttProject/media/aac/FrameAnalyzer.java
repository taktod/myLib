/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.aac;

import com.ttProject.nio.channels.IReadChannel;

public class FrameAnalyzer implements IFrameAnalyzer {
	private final AacManager manager = new AacManager();
	@Override
	public Frame analyze(IReadChannel ch) throws Exception {
		Frame frame = manager.getUnit(ch);
		if(frame == null) {
			return null;
		}
		frame.analyze(ch, this);
		ch.position(frame.getPosition() + frame.getSize());
		return frame;
	}
}
