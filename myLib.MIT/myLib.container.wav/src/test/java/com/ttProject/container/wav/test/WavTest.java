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
import com.ttProject.container.riff.IFrameEventListener;
import com.ttProject.container.riff.RiffUnitReader;
import com.ttProject.container.riff.type.Data;
import com.ttProject.frame.IFrame;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * wavファイルの読み込みテスト
 * @author taktod
 */
public class WavTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(WavTest.class);
	/**
	 * 読み込みテスト
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
				logger.info(container);
				if(container instanceof Data) {
					// データだった場合は処理はここでおわり。(とりあえず)
					Data data = (Data)container;
					data.analyzeFrame(source, new IFrameEventListener() {
						@Override
						public void onNewFrame(IFrame frame) {
							logger.info(frame);
						}
					});
					break;
				}
			}
		}
		catch(Exception e) {
			logger.warn("例外発生", e);
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
