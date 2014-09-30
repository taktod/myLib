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
import com.ttProject.container.IReader;
import com.ttProject.container.flv.FlvTagReader;
import com.ttProject.nio.channels.FileReadChannel;
import com.ttProject.nio.channels.IFileReadChannel;

/**
 * flvの動作テスト
 * @author taktod
 */
public class FlvTest {
	/** ロガー */
	private Logger logger = Logger.getLogger(FlvTest.class);
	/**
	 * analyzerの動作テスト
	 */
	@Test
	public void analyzerTest() {
		IFileReadChannel source = null;
		try {
			source = FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1.flv")
			);
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
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
//	@Test
	public void vp6mp3Test() throws Exception {
		analyzerTest(
				FileReadChannel.openFileReadChannel("http://red5.googlecode.com/svn-history/r4071/java/example/trunk/oflaDemo/www/streams/toystory3-vp6.flv")
		);
	}
//	@Test
	public void flv1mp3Test() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1mp3.flv")
			)
		);
	}
//	@Test
	public void h264mp3Test() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264mp3.flv")
			)
		);
	}
//	@Test
	public void h264aacTest() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264aac.flv")
			)
		);
	}
//	@Test
	public void flv1nellyTest() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1nelly.flv")
			)
		);
	}
//	@Test
	public void flv1nelly8Test() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1nelly8.flv")
			)
		);
	}
//	@Test
	public void flv1nelly16Test() throws Exception { // このテストだけフレーム値があってなくないか？
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1nelly16.flv")
			)
		);
	}
//	@Test
	public void h264speexTest() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.h264speex.flv")
			)
		);
	}
//	@Test
	public void flv1adpcmswfTest() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.flv1adpcmswf.flv")
			)
		);
	}
//	@Test
	public void adpcm11_1Test() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.adpcm44_1.flv")
			)
		);
	}
//	@Test
	public void adpcm11_2Test() throws Exception {
		analyzerTest(
			FileReadChannel.openFileReadChannel(
					Thread.currentThread().getContextClassLoader().getResource("test.adpcm44_2.flv")
			)
		);
	}
	private void analyzerTest(IFileReadChannel source) {
		try {
			IReader reader = new FlvTagReader();
			IContainer container = null;
			while((container = reader.read(source)) != null) {
				logger.info(container);
/*				if(unit instanceof AudioTag) {
					IAudioFrame frame = ((AudioTag)unit).getFrame();
					logger.info(HexUtil.toHex(frame.getData()));
				}*/
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
