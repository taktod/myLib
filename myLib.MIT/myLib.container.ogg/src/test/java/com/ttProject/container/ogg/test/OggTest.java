/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.ogg.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.ogg.OggPageReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * ogg load test.
 * @author taktod
 */
public class OggTest {
	/** logger */
	private Logger logger = Logger.getLogger(OggTest.class);
	/**
	 * analyze test
	 */
	@Test
	public void analyzerTest() {
		logger.info("ogg analyze test");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.speex16.ogg")
//					Thread.currentThread().getContextClassLoader().getResource("test.vorbis.ogg")
			);
			IReader reader = new OggPageReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
			}
		}
		catch(Exception e) {
			logger.warn(e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
	/**
	 * this is the test for loooong speex.
	 * this test data will be terminated.
	 */
//	@Test
	public void analyzeTest2() {
		logger.info("ogg analyze test2");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/mario.speex2.ogg"
			);
			IReader reader = new OggPageReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
			}
		}
		catch(Exception e) {
			logger.warn(e);
		}
		finally {
			if(source != null) {
				try {
					source.close();
				}
				catch(Exception e) {
				}
				source = null;
			}
		}
	}
}
