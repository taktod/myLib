/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.adts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.adts.AdtsUnitReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * loading test for adts.
 * @author taktod
 */
public class AdtsTest {
	/** logger */
	private Logger logger = Logger.getLogger(AdtsTest.class);
	/**
	 * analyzeTest
	 */
	@Test
	public void analyzeTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.aac.aac")
			);
			IReader reader = new AdtsUnitReader();
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
				catch(Exception e) {}
				source = null;
			}
		}
	}
}
