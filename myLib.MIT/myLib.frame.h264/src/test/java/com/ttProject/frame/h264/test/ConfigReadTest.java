/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.h264.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.h264.ConfigData;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class ConfigReadTest {
	private Logger logger = Logger.getLogger(ConfigReadTest.class);
	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cdata = new ConfigData();
		IVideoFrame frame = cdata.getNalsFrame(channel);
		logger.info(frame.getWidth());
		logger.info(frame.getHeight());
	}
}
