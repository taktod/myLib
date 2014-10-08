/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.speex.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.speex.type.Frame;
import com.ttProject.frame.speex.type.HeaderFrame;
import com.ttProject.util.HexUtil;

/**
 * test for muted frame.
 * @author taktod
 */
public class MutedFrameTest {
	/** logger */
	private Logger logger = Logger.getLogger(MutedFrameTest.class);
	@Test
	public void test() throws Exception {
		Frame frame = SpeexFrame.getMutedFrame(16000, 1, 16);
		// need to add headerFrame.
		HeaderFrame headerFrame = new HeaderFrame();
		headerFrame.fillWithFlvDefault();
		frame.setHeaderFrame(headerFrame);
		logger.info(HexUtil.toHex(frame.getData()));
	}
}
