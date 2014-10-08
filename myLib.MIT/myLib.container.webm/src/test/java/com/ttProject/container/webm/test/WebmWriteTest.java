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
 * data write test for webm.
 * TODO check is this work?
 * @author taktod
 */
public class WebmWriteTest {
	/** logger */
	private Logger logger = Logger.getLogger(WebmWriteTest.class);
	/**
	 * test
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("start write test.");
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
		logger.info("end");
	}
}
