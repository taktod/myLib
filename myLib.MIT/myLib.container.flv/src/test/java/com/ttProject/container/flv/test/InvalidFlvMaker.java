/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.flv.test;

import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.ttProject.container.IContainer;
import com.ttProject.container.flv.FlvHeaderTag;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.container.flv.type.AudioTag;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * try to make invalid flv files.
 * @author taktod
 */
public class InvalidFlvMaker {
	/** logger */
	private Logger logger = Logger.getLogger(InvalidFlvMaker.class);
	/**
	 * error with audio.
	 * @throws Exception
	 */
//	@Test
	public void audio() throws Exception {
		logger.info("make invalid flvs.");
		IFileReadChannel source = FileReadChannel.openFileReadChannel("http://49.212.39.17/mario.flv");
		FlvTagReader reader = new FlvTagReader();
		FileOutputStream audioOutputStream0 = new FileOutputStream("audioOutput0.flv");
		FileChannel audioOutput0 = audioOutputStream0.getChannel(); // normal
		FileOutputStream audioOutputStream1 = new FileOutputStream("audioOutput1.flv");
		FileChannel audioOutput1 = audioOutputStream1.getChannel(); // start with 20 sec pos.
		FileOutputStream audioOutputStream2 = new FileOutputStream("audioOutput2.flv");
		FileChannel audioOutput2 = audioOutputStream2.getChannel(); // have gap between 20sec to 40sec.
		FlvHeaderTag headerTag = new FlvHeaderTag();
		headerTag.setAudioFlag(true);
		headerTag.setVideoFlag(false);
		audioOutput0.write(headerTag.getData());
		audioOutput1.write(headerTag.getData());
		audioOutput2.write(headerTag.getData());
		IContainer container = null;
		while((container = reader.read(source)) != null) {
			logger.info("info:" + container.toString());
			if(container instanceof AudioTag) {
				AudioTag aTag = (AudioTag)container;
				audioOutput0.write(aTag.getData());
				if(aTag.getPts() < 20000 || aTag.getPts() > 40000) {
					audioOutput2.write(aTag.getData());
				}
				aTag.setPts(aTag.getPts() + 20000);
				audioOutput1.write(aTag.getData());
			}
		}
		logger.info("end");
		source.close();
		audioOutputStream0.close();
		audioOutputStream1.close();
		audioOutputStream2.close();
	}
}
