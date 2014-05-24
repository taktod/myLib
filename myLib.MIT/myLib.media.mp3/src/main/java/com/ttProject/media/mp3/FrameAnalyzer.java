/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.mp3;

import com.ttProject.nio.channels.IReadChannel;

/**
 * mp3の解析動作
 * @author taktod
 */
public class FrameAnalyzer implements IFrameAnalyzer {
	private final Mp3Manager manager = new Mp3Manager();
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
