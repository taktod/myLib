/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.mp3;

import org.apache.log4j.Logger;

import com.ttProject.frame.AudioSelector;
import com.ttProject.frame.mp3.type.Frame;
import com.ttProject.frame.mp3.type.ID3Frame;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.unit.IUnit;
import com.ttProject.unit.extra.BitLoader;
import com.ttProject.unit.extra.bit.Bit8;

/**
 * selector for mp3 frame.
 * @author taktod
 */
public class Mp3FrameSelector extends AudioSelector {
	/** logger */
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(Mp3FrameSelector.class);
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IUnit select(IReadChannel channel) throws Exception {
		if(channel.size() - channel.position() < 1) {
			return null;
		}
		Bit8 firstByte = new Bit8();
		BitLoader loader = new BitLoader(channel);
		loader.load(firstByte);
		Mp3Frame frame = null;
		switch(firstByte.get()) {
		case 'I': // ID3?
			frame = new ID3Frame();
			break;
		case 'T': // TAG?
			throw new Exception("Tag is undefined.");
		case 0xFF: // Frame
			frame = new Frame();
			break;
		default:
			throw new Exception("unknown frame:" + Integer.toHexString(firstByte.get()));
		}
		setup(frame);
		frame.minimumLoad(channel);
		return frame;
	}
}
