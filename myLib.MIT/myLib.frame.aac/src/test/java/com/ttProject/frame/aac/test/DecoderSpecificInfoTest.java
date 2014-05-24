/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.frame.aac.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.frame.aac.DecoderSpecificInfo;
import com.ttProject.nio.channels.ByteReadChannel;
import com.ttProject.nio.channels.IReadChannel;
import com.ttProject.util.HexUtil;

/**
 * dsiの読み込みテスト
 * @author taktod
 */
public class DecoderSpecificInfoTest {
	private Logger logger = Logger.getLogger(DecoderSpecificInfoTest.class);
	@Test
	public void restore() throws Exception {
		IReadChannel channel = new ByteReadChannel(HexUtil.makeBuffer("1210"));
		DecoderSpecificInfo specificInfo = new DecoderSpecificInfo();
		specificInfo.minimumLoad(channel);
		logger.info(specificInfo);
	}
}
