/*
 * myLib - https://github.com/taktod/myLib
 * Copyright (c) 2014 ttProject. All rights reserved.
 * 
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 */
package com.ttProject.xuggle.frame.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.ttProject.container.IContainer;
import com.ttProject.container.IReader;
import com.ttProject.container.ogg.OggPage;
import com.ttProject.container.ogg.OggPageReader;
import com.ttProject.frame.IAudioFrame;
import com.ttProject.frame.IFrame;
import com.ttProject.frame.IVideoFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * oggコンテナのデコード動作テスト
 * @author taktod
 */
public class OggContainerTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(OggContainerTest.class);
	@Test
	public void speex() throws Exception {
		logger.info("speexテスト");
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("speex.ogg")
			)
		);
	}
	@Test
	public void vorbis() throws Exception {
		logger.info("vorbisテスト");
		decodeTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("vorbis.ogg")
			)
		);
	}
	private void decodeTest(IFileReadChannel source) {
		DecodeBase base = new DecodeBase();
		try {
			IReader reader = new OggPageReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				if(container instanceof OggPage) {
					OggPage page = (OggPage) container;
					for(IFrame frame : page.getFrameList()) {
						logger.info(frame);
						if(frame instanceof IAudioFrame) {
							base.processAudioDecode((IAudioFrame)frame);
						}
						else if(frame instanceof IVideoFrame) {
							base.processVideoDecode((IVideoFrame)frame);
						}
					}
				}
			}
		}
		catch(Exception e) {
			logger.error("例外発生", e);
		}
		finally {
			if(base != null) {
				base.close();
				base = null;
			}
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
