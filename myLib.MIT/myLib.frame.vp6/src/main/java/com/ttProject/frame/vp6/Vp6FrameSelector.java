/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.vp6;

import org.apache.log4j.Logger;

import com.ttProject.frame.VideoSelector;
import com.ttProject.frame.vp6.type.InterFrame;
import com.ttProject.frame.vp6.type.IntraFrame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit1;
import com.ttProject.unit.extra.bit.Bit6;

/**
 * selector for vp6 frame.
 * @author taktod
 */
public class Vp6FrameSelector extends VideoSelector {
	/** logger */
	private Logger logger = Logger.getLogger(Vp6FrameSelector.class);
	/** hold keyframe and share this. */
	private IntraFrame keyFrame = null;
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		// read first byte for check.
		if(channel.size() - channel.position() < 1) {
			// data is too short.
			return null;
		}
		Bit1 frameMode = new Bit1();
		Bit6 qp = new Bit6();
		Bit1 marker = new Bit1();
		BitLoader loader = new BitLoader(channel);
		loader.load(frameMode, qp, marker);
		Vp6Frame frame = null;
		switch(frameMode.get()) {
		case 1: // interFrame
			frame = new InterFrame(frameMode, qp, marker);
			break;
		case 0: // intraFrame(keyFrame)
			frame = new IntraFrame(frameMode, qp, marker);
			keyFrame = (IntraFrame)frame;
			break;
		default:
			throw new Exception("unexpected frameMode.:" + frameMode.get());
		}
		if(keyFrame == null) {
			logger.info("key frame is not loaded yet.");
			return null;
		}
		setup(frame);
		if(!(frame instanceof IntraFrame)) {
			frame.setKeyFrame(keyFrame);
		}
		frame.minimumLoad(channel);
		return frame;
	}
}
