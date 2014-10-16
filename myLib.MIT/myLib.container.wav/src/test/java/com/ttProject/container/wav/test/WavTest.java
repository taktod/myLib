/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.wav.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.riff.RiffFrameUnit;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * wav load test
 * @author taktod
 */
public class WavTest {
	/** logger */
	private Logger logger = Logger.getLogger(WavTest.class);
	/**
	 * test
	 */
	@Test
	public void test() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.adpcm_ima_wav.wav")
			);
			IReader reader = new RiffUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof RiffFrameUnit) {
					IFrame frame = ((RiffFrameUnit) container).getFrame();
					if(frame != null) {
						logger.info(frame);
						logger.info(1f * frame.getPts() / frame.getTimebase());
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
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
