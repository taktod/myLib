/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.webm.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.webm.WebmTagReader;
import com.ttProject.container.webm.WebmTagWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * webmによるデータの書き出しテスト
 * @author taktod
 */
public class WebmWriteTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(WebmWriteTest.class);
	/**
	 * 動作テスト
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("書き込みテスト開始");
		IReadChannel source = FileReadChannel.openFileReadChannel(
				Thread.currentThread().getContextClassLoader().getResource("test.webm")
		);
		IReader reader = new WebmTagReader();
		IWriter writer = new WebmTagWriter("output.webm");
		writer.prepareHeader(CodecType.VP8, CodecType.VORBIS);
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			if(container instanceof MkvBlockTag) {
				MkvBlockTag blockTag = (MkvBlockTag)container;
				IFrame frame = blockTag.getFrame();
				logger.info(frame);
				writer.addFrame(blockTag.getTrackId().get(), frame);
			}
		}
		writer.prepareTailer();
		logger.info("処理おわり");
	}
}
