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
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.container.ogg.OggPageWriter;
import com.ttProject.frame.CodecType;
import com.ttProject.frame.speex.SpeexFrame;
import com.ttProject.frame.speex.type.CommentFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IReadChannel;

/**
 * test for speexFlv.
 * @author taktod
 */
public class SpeexFlvTest {
	private Logger logger = Logger.getLogger(SpeexFlvTest.class);
	/**
	 * make 1Frame per packet data from 2frame per packet.
	 */
	@Test
	public void make1FramePerPacket() throws Exception {
		IReadChannel source = FileReadChannel.openFileReadChannel(
				"http://streams.videolan.org/issues/2973/audio-only-speex.flv" // 2frames
//				"http://streams.videolan.org/issues/2973/test_speex.flv" // 1frame
		);
		IReader reader = new FlvTagReader();
		OggPageWriter writer = new OggPageWriter("audio-only-speex.ogg");
		writer.prepareHeader(CodecType.SPEEX);
		boolean alreadySetHeader = false;
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			logger.info(container);
			if(container instanceof AudioTag) {
				AudioTag aTag = (AudioTag) container;
				if(!alreadySetHeader) {
					SpeexFrame sFrame = (SpeexFrame)aTag.getFrame();
					alreadySetHeader = true;
					writer.addFrame(1, sFrame.getHeaderFrame());
					writer.completePage(1);
					writer.addFrame(1, new CommentFrame());
					writer.completePage(1);
				}
				logger.info(aTag.getFrame()); // 2frames or more, get the audioMultiFrame.
				writer.addFrame(1, aTag.getFrame());
			}
		}
		writer.prepareTailer();
	}
}
