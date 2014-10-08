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
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagWriter;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.SimpleBlock;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * convert test from mkv to ?
 * @author taktod
 */
public class MkvToTest {
	/** logger */
	private Logger logger = Logger.getLogger(MkvToTest.class);
	/**
	 * to flv(h264 / aac)
	 * this test for h264 Slice frame which consists of two slice nals.
	 * @throws Exception
	 */
	@Test
	public void flv_h264_aac_ex2() throws Exception {
		logger.info("from mkv to flv test(h264 / aac)");
		FlvTagWriter writer = new FlvTagWriter("output_mpegts_h264_aac_ex2.flv");
		FlvHeaderTag flvHeader = new FlvHeaderTag();
		flvHeader.setAudioFlag(true);
		flvHeader.setVideoFlag(true);
		writer.addContainer(flvHeader);
		convertTest(
			FileReadChannel.openFileReadChannel(
					"http://49.212.39.17/ahiru.mkv"
			),
			writer,
			0,
			1
		);
	}
	/**
	 * convert process body
	 * @param source
	 * @param writer
	 */
	private void convertTest(IFileReadChannel source, IWriter writer, int videoId, int audioId) {
		try {
			writer.prepareHeader();
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof SimpleBlock) {
					SimpleBlock simpleBlock = (SimpleBlock)container;
					writer.addFrame(simpleBlock.getTrackId().get(), simpleBlock.getFrame());
				}
			}
			writer.prepareTailer();
		}
		catch(Exception e) {
			logger.error(e);
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
