/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

public class InvalidFileCheck {
	/** logger */
	private Logger logger = Logger.getLogger(InvalidFileCheck.class);
	@Test
	public void check() throws Exception {
		logger.info("want to check the troubled flv.");
		IFileReadChannel source = FileReadChannel.openFileReadChannel("../myLib.container.flv/converted.flv");
		FlvTagReader reader = new FlvTagReader();
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			logger.info(container);
			Thread.sleep(10);
		}
	}
}
