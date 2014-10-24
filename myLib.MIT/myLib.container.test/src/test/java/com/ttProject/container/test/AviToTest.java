/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.IWriter;
import com.ttProject.container.ogg.OggPageWriter;
import com.ttProject.container.riff.RiffFrameUnit;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * convert container fron avi to ?
 * @author taktod
 */
public class AviToTest {
	private Logger logger = Logger.getLogger(AviToTest.class);
	/**
	 * to ogg vorbis
	 * @throws Exception
	 */
	@Test
	public void ogg_vorbis() throws Exception {
		logger.info("from avi to ogg test(vorbis)");
		IWriter writer = new OggPageWriter("output.vorbis.ogg");
		writer.prepareHeader(CodecType.VORBIS);
		convertTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("vp8vorbis.avi")
			),
			writer,
			-1,
			1
		);
	}
	private void convertTest(IFileReadChannel source, IWriter writer, int videoId, int audioId) {
		try {
			IReader reader = new RiffUnitReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof RiffFrameUnit) {
					RiffFrameUnit frameUnit = (RiffFrameUnit)container;
					IFrame frame = frameUnit.getFrame();
					if(frame != null) {
						logger.info(frame.getClass() + " " + (1D * frame.getPts() / frame.getTimebase()));
						writer.addFrame(frameUnit.getTrackId(), frame);
					}
				}
			}
			// write tailer
			writer.prepareTailer(); // tailer is ok
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
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
