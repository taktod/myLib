/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.nellymoser.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.nellymoser.NellymoserFrame;
import com.ttProject.frame.nellymoser.type.Frame;
import com.ttProject.util.HexUtil;

/**
 * muted frame test
 * @author taktod
 */
public class MutedFrameTest {
	/** logger */
	private Logger logger = Logger.getLogger(MutedFrameTest.class);
	@Test
	public void test() throws Exception {
		logger.info("mute frame test.");
		Frame frame = NellymoserFrame.getMutedFrame(44100, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(22050, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(11025, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(8000, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
		frame = NellymoserFrame.getMutedFrame(16000, 1, 16);
		logger.info(HexUtil.toHex(frame.getData()));
	}
}
