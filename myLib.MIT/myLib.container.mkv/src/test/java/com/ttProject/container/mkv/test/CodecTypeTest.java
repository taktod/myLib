/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.mkv.MkvCodecType;

/**
 * check the codecType.
 * @author taktod
 */
public class CodecTypeTest {
	/** logger */
	private Logger logger = Logger.getLogger(CodecTypeTest.class);
	/**
	 * test
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		// h264
		String codecString = "V_MPEG4/ISO/AVC";
		logger.info(MkvCodecType.getMkvCodecType(codecString));
		// mp3
		codecString = "A_MPEG/L3";
		logger.info(MkvCodecType.getMkvCodecType(codecString));
		// vp8
		codecString = "V_VP8";
		logger.info(MkvCodecType.getMkvCodecType(codecString));
		// vorbis
		codecString = "A_VORBIS";
		logger.info(MkvCodecType.getMkvCodecType(codecString));
		// aac
		codecString = "A_AAC";
		logger.info(MkvCodecType.getMkvCodecType(codecString));
	}
}
