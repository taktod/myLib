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

/**
 * configDataの読み込み動作テスト
 * @author taktod
 */
public class ConfigReadTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(ConfigReadTest.class);
//	@Test
	public void test_old() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cdata = new ConfigData();
		IVideoFrame frame = cdata.getNalsFrame(channel);
		logger.info(frame.getWidth());
		logger.info(frame.getHeight());
	}
	@Test
	public void test() throws Exception {
//		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("014D401EFFE10019674D401E924201405FF2E02200000300C800002ED51E2C5C9001000468EE32C8"));
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("01420016FFE1002E6742801696540501ED80A84000000300400000053800007A10000F425FC638C00003D080007A12FE31C3B4244D4001000468CE3520"));
		// channelのデータを読み込んでpspとppsが取得できれば御の字
		ConfigData cData = new ConfigData();
		cData.analyzeData(channel);
		logger.info(cData.getSpsList());
		logger.info(cData.getPpsList());
		logger.info(cData.getNalSizeBytes());
		logger.info(cData.getSpsList().get(0).getWidth());
		logger.info(cData.getSpsList().get(0).getHeight());
		ConfigData cData2 = new ConfigData();
		logger.info(HexUtil.toHex(cData2.makeConfigData(cData.getSpsList().get(0), cData.getPpsList().get(0))));
	}
	//01420016FFE1002E6742801696540501ED80A84000000300400000053800007A10000F425FC638C00003D080007A12FE31C3B4244D4001000468CE3520
	//01428016FFE1002E6742801696540501ED80A84000000300400000053800007A10000F425FC638C00003D080007A12FE31C3B4244D4001000468CE3520
}
