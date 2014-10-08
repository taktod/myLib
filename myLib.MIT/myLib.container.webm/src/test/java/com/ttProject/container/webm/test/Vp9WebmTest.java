/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.webm.test;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.container.webm.WebmTagReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * vp9 webm load test
 * @author taktod
 */
public class Vp9WebmTest {
	/** logger */
	private Logger logger = Logger.getLogger(Vp9WebmTest.class);
	/**
	 * analyze file test.
	 */
//	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel("http://yt-dash-mse-test.commondatastorage.googleapis.com/media/feelings_vp9-20130806-247.webm");
			IReader reader = new WebmTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof SimpleBlock) {
					SimpleBlock simpleBlock = (SimpleBlock) container;
					logger.info(simpleBlock);
					logger.info(simpleBlock.getFrame());
				}
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
