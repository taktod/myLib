/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mpegts.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mpegts.MpegtsPacketReader;
import com.ttProject.container.mpegts.type.Pes;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.frame.extra.VideoMultiFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mpegts work test.
 * @author taktod
 */
public class MpegtsTest {
	/** logger */
	private Logger logger = Logger.getLogger(MpegtsTest.class);
	/**
	 * normal test
	 * @throws Exception
	 */
	@Test
	public void test() throws Exception {
		logger.info("test");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264aac.ts")
			)
		);
	}
	/**
	 * recorded by iphone5S, this data have multi nal units.(two slice nal consist one slice data.)
	 * @throws Exception
	 */
	@Test
	public void largeSliceTest() throws Exception {
		logger.info("largeSliceTest");
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.ts"
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		try {
			IReader reader = new MpegtsPacketReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof Pes) {
					Pes pes = (Pes) container;
					IFrame frame = pes.getFrame();
					if(frame != null && frame instanceof IVideoFrame) {
						logger.info("pesFrame");
						if(frame instanceof VideoMultiFrame) {
							for(IVideoFrame vFrame : ((VideoMultiFrame) frame).getFrameList()) {
								logger.info(vFrame + ":" + vFrame.getSize() + "bytes" + " " + (vFrame.getPts() + 0) + " pts" + " " + vFrame.getDts() + " dts");
							}
						}
					}
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
