/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU LESSER GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.flazr.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpMessage;
import com.flazr.rtmp.RtmpWriter;
import com.ttProject.container.IContainer;
import com.ttProject.flazr.unit.MessageManager;

/**
 * データをdownloadするwriter
 * @author taktod
 */
public class DlWriter implements RtmpWriter {
	private Logger logger = LoggerFactory.getLogger(DlWriter.class);
	private MessageManager messageManager = new MessageManager();
	@Override
	public void close() {
	}
	@Override
	public void write(RtmpMessage message) {
		try {
			IContainer container = messageManager.getTag(message);
			logger.info("{}", container);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
