/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under The MIT license.
 */
package com.ttProject.container.mkv.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.mkv.MkvBlockTag;
import com.ttProject.container.mkv.MkvTag;
import com.ttProject.container.mkv.MkvTagReader;
import com.ttProject.container.mkv.type.BlockGroup;
import com.ttProject.frame.AudioFrame;
import com.ttProject.frame.VideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * mkvの動作テスト
 * @author taktod
 */
public class MkvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(MkvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		int lastPosition = 0;
		try {
			/*
			 * test2 3 4 5 7 8はh264 + aac or mp3
			 * test1 msmpeg4v2
			 */
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.theoravorbis.mkv")
//					Thread.currentThread().getContextClassLoader().getResource("test.mkv")
//					Thread.currentThread().getContextClassLoader().getResource("test1.mkv") // msmpeg4v2なのでframeがいまのところない
//					Thread.currentThread().getContextClassLoader().getResource("test2.mkv")
//					Thread.currentThread().getContextClassLoader().getResource("test3.mkv")
//					Thread.currentThread().getContextClassLoader().getResource("test4.mkv") // theoraらしい
//					Thread.currentThread().getContextClassLoader().getResource("test5.mkv") // subtitleがはいっていて動作しない。S_TEXT/UTF8
//					Thread.currentThread().getContextClassLoader().getResource("test6.mkv") // msmpeg4v2なのでframeがいまのところない
//					Thread.currentThread().getContextClassLoader().getResource("test7.mkv")
//					Thread.currentThread().getContextClassLoader().getResource("test8.mkv")
			);
			IReader reader = new MkvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
//				logger.info(container);
				if(container instanceof BlockGroup) {
					for(MkvTag tag : ((BlockGroup)container).getChildList()) {
						if(tag instanceof MkvBlockTag) {
							MkvBlockTag blockTag = (MkvBlockTag)tag;
//							logger.info(blockTag);
							logger.info(blockTag.getFrame());
						}
					}
				}
				if(container instanceof MkvBlockTag) {
					MkvBlockTag blockTag = (MkvBlockTag)container;
//					logger.info(blockTag);
//					logger.info(blockTag.getFrame());
					if(blockTag.getFrame() instanceof VideoFrame) {
						VideoFrame vFrame = (VideoFrame)blockTag.getFrame();
						logger.info(vFrame.getCodecType() + " " + vFrame.getWidth() + "x" + vFrame.getHeight());
					}
					else if(blockTag.getFrame() instanceof AudioFrame) {
						AudioFrame aFrame = (AudioFrame)blockTag.getFrame();
						logger.info(aFrame.getCodecType() + " " + aFrame.getSampleRate() + ":" + aFrame.getChannel());
					}
				}
				lastPosition = source.position();
			}
		}
		catch(Exception e) {
			logger.warn(e);
			try {
				logger.warn("error position:" + Integer.toHexString(lastPosition));
			}
			catch(Exception ex) {
				
			}
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
