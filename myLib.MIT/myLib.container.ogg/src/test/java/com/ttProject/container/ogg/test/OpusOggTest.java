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
 * ogg load test for opus
 * @author taktod
 */
public class OpusOggTest {
	/** logger */
	private Logger logger = Logger.getLogger(OpusOggTest.class);
	/**
	 * analyze test
	 */
	@Test
	public void analyzerTest() {
		logger.info("opusOgg analyze test");
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					"http://people.xiph.org/~giles/2012/opus/detodos.opus"
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
