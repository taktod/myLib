/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;

/**
 * やりとりすべきpacketデータについて調べるためのテスト
 * 623
 * 000 0000 0000 1111 0101
 * 0010 0110 1111
 * 
 * @author taktod
 */
public class PacketCheckTest extends SetupBase {
	/** ロガー */
	private Logger logger = Logger.getLogger(PacketCheckTest.class);
	/**
	 * 解析動作テストをします。
	 * @throws Exception
	 */
	@Test
	public void analyze() throws Exception {
		logger.info("解析テストします。");
		init();
		IContainer container = IContainer.make();
		if(container.open("test.flv", IContainer.Type.WRITE, null) < 0) {
			throw new Exception("ファイルが開けませんでした。");
		}
		IStreamCoder coder = Encoder.speex(container);
		coder.setSampleRate(16000);
		coder.setChannels(1);
		processConvert(container, null, coder);
	}
}
