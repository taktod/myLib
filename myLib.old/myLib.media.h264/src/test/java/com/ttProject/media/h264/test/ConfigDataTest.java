/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.media.h264.test;

import java.util.List;

import org.apache.log4j.Logger;

import com.ttProject.media.h264.ConfigData;
import com.ttProject.media.h264.Frame;
import com.ttProject.media.h264.frame.SequenceParameterSet;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

public class ConfigDataTest {
	private Logger logger = Logger.getLogger(ConfigDataTest.class);
//	@Test
	public void test() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cdata = new ConfigData();
		List<Frame> frames = cdata.getNals(channel);
		SequenceParameterSet sps = (SequenceParameterSet)frames.get(0);
		logger.info(sps.getWidth());
		logger.info(sps.getHeight());
	}
}
