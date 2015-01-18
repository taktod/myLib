/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.rtmp.test;

import org.apache.log4j.Logger;

import com.ttProject.rtmp.header.HeaderFactory;
import com.ttProject.rtmp.header.HeaderType;
import com.ttProject.rtmp.header.IRtmpHeader;
import com.ttProject.rtmp.message.MessageType;

public class HeaderTest {
	private Logger logger = Logger.getLogger(HeaderTest.class);
	public void test() throws Exception {
		logger.info("start header test.");
		IRtmpHeader header = HeaderFactory.getInstance().getHeader(MessageType.VIDEO_MESSAGE);
		logger.info(header);
		header.switchTo(HeaderType.Type1);
	}
}
