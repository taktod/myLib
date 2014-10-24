/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mpegts.MpegtsCodecType;
import com.ttProject.frame.CodecType;

/**
 * codec type detect test.
 * @author taktod
 */
public class CodecWorkTest {
	private Logger logger = Logger.getLogger(CodecWorkTest.class);
	@Test
	public void test() throws Exception {
		logger.info("detect test.");
		logger.info(MpegtsCodecType.getType(CodecType.AAC));
		logger.info(MpegtsCodecType.getType(CodecType.MP3));
		logger.info(MpegtsCodecType.getType(CodecType.H264));
	}
}
